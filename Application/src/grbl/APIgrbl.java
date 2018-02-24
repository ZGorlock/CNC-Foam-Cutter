package grbl;

import gui.Interfaces.MainMenu.GcodeController;
import gui.Interfaces.MainMenu.ModelController;
import gui.Interfaces.MainMenu.TraceController;
import utils.CmdLine;

import javax.jws.WebParam;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static gui.Interfaces.MainMenu.MenuController.paused;

public class APIgrbl extends Thread
{
    // GUI elements
    private double x;
    private double y;
    private double z;
    private double percentage;
    private String status;

    private List<String> commandFromUI;
    private List<String> cmdBlock;

    private List<String> updateCodeSent;
    private List<String> codeBlock;

    // Process elements
    private String filename;

    // Controller
    public static APIgrbl grbl;

    public APIgrbl(String filename)
    {
        // Init GUI variables
        percentage = x = y = z = 0.0;
        status = "Off";

        // Init grbl controller
        grbl = this;

        // Init file to process
        this.filename = filename;

        // Init variables for user request
        commandFromUI = new ArrayList<>();
        cmdBlock = new ArrayList<>();

        // Init variables for visual update on the code being sent
        updateCodeSent = new ArrayList<>();
        codeBlock = new ArrayList<>();
    }

    public void run()
    {
        // path to directory
        String thisPath = Paths.get("").toAbsolutePath().toString();

        // directory to grbl package
        String directoryGrbl =  thisPath +"\\src\\grbl\\";

        // directory to gcode package
        String directoryGcode = thisPath +"\\resources\\gcode\\";

        // directory to temp file
        String directoryTemp = thisPath +"\\src\\grbl\\temp\\";

        // Modifies to gbrl acceptable gcode
        Modifier m = new Modifier(filename, directoryGcode);
        m.modify();

        // api needs grbl, gcode, temp
        partitionAndStream(filename,directoryGrbl, directoryGcode, directoryTemp, m.getCommandSize());
    }

    private void partitionAndStream(String filename, String directoryGrbl, String directoryGcode, String directoryTemp, int size)
    {
        try{

            BufferedReader in = new BufferedReader(new FileReader(new File(directoryGcode, filename)));  // gcode
            int commandsRead = 0;

            // will stop at end of file
            while(commandsRead < size)
            {
                // read every 127 characters and create a file with them for stream.py to use
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directoryTemp,"tempfile.txt")));
                Queue<String> carryOver = new ArrayDeque<>();
                StringBuilder sb = new StringBuilder();

                int charsUsed = 0;
                while(charsUsed < 127)
                {
                    if(carryOver.size() == 1)
                    {
                        String carried = carryOver.remove();
                        sb.append(carried);
                        sb.append('\n');

                        commandsRead += 1;

                        // must add one for added chars '\n'
                        charsUsed += carried.length() + 1;
                    }

                    String newCommand = in.readLine();

                    if(newCommand != null)
                    {
                        if((newCommand.length() + charsUsed + 1) < 127)
                        {
                            // update counts
                            commandsRead += 1;
                            charsUsed += newCommand.length() + 1;

                            // append what will be written
                            sb.append(newCommand);
                            sb.append('\n');
                        }
                        else
                        {
                            // end of 127 char limit
                            carryOver.add(newCommand);
                            charsUsed = 127;
                        }
                    }else
                    {
                        // end of file, stopping conditions
                        charsUsed = 127;
                        commandsRead = size;
                    }
                }

                // create string to be printed
                String gcode = sb.toString();

                bw.write(gcode);
                bw.close();

                // Update UI
                while (gcode.contains("\n\n")) {
                    gcode = gcode.replaceAll("\\n\\n", "\n");
                }
                if (!gcode.isEmpty()) {
                    GcodeController.codeBlock.add(gcode);
                }

                // Check for User Input
                checkForCommand(directoryGrbl,directoryTemp);

                // Check for pause/resume/stop
                if(paused)
                {
                    synchronized(this) {
                        while (paused) {
                            try{
                                wait();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            // The current thread will be blocked until some else calls notify()
                        }
                    }
                }

                // execute stream.py with the file created, get input stream as a response
                Process process = CmdLine.executeCmdAsThread("py " +  directoryGrbl + "stream.py "+ directoryTemp + "tempfile.txt\n"); //TODO need to handle if this returns null, retry / throw exception
                BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while (true)
                {
                    line = r.readLine();

                    if (line == null) {
                        break;
                    }
                    updateCoordinates(line);

                    // Check for User Input
                    checkForCommand(directoryGrbl,directoryTemp);
                }
                // set percentage done
                percentage = ((double)commandsRead / (double) size) * 100.00;

                // Update UI
                ModelController.percentage = String.format("%.2f", percentage) + " %";
                ModelController.timerem = "5 years";
                //Temporary sleep for demo
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        //TODO stopping
    }

    private void checkForCommand(String directoryGrbl, String directoryTemp)
    {
        // check for commands from UI
        if(commandFromUI.size() > 0)
        {
            //create new process and add to the response for UI
            handleRequest(directoryGrbl,directoryTemp);
        }
    }

    /**
     * Handles a Resume event.
     */
    public synchronized void initiateResume()
    {
        notify();
    }

    private void updateCoordinates(String line)
    {
        // Parse line into coordinates
        String [] decomposed = line.split(",");
        
        if (decomposed.length == 4) {
            setStatus(decomposed[0].substring(1));
            String[] first = decomposed[1].split(":");
            setX(Double.parseDouble(first[1]));
            setY(Double.parseDouble(decomposed[2]));
            setZ(Double.parseDouble(decomposed[3]));
        }

        String x = String.format("%.2f", getCoordinateX());
        String y = String.format("%.2f", getCoordinateY());
        String z = String.format("%.2f", getCoordinateZ());
        String status = getStatus();

        TraceController.coordinateBlock.clear();
        TraceController.coordinateBlock.add(0,x);
        TraceController.coordinateBlock.add(1,y);
        TraceController.coordinateBlock.add(2,z);
        TraceController.coordinateBlock.add(3,status);
    }

    public void sendRequest(String command)
    {
        commandFromUI.add(command);
    }

    private void handleRequest(String directoryGrbl, String directoryTemp)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directoryTemp,"tempCommand.txt")));
            bw.write(commandFromUI.get(0));
            bw.close();

            // execute stream.py with the command being sent, get input stream as a response
            Process process = CmdLine.executeCmdAsThread("py -3 " +  directoryGrbl + "stream.py "+ directoryTemp + "tempCommand.txt\n");
            if (process == null) {
                return;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while (true)
            {
                line = r.readLine();
                if (line == null || line.isEmpty())
                {
                    break;
                }
                GcodeController.commandBlock.add(' ' + line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        commandFromUI.remove(0);
    }

    // Getters and Setters
    public double getCoordinateX() {
        return x;
    }

    public double getCoordinateY() {
        return y;
    }

    public double getCoordinateZ() { return z; }

    public String getStatus( ){return status; }

    public double getPercentage() {
        return percentage;
    }

    private void setX(double x){ this.x = x; }

    private void setY(double y){ this.y = y; }

    private void setZ(double z){ this.z = z; }

    private void setStatus(String status){ this.status = status;}
}
    