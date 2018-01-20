package grbl;

import utils.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class APIgrbl {

    private double x;
    private double y;
    private double z;
    private double a;
    private double percentage;

    public static APIgrbl grbl;

    public APIgrbl(){
        percentage = x = y = z = a = 0.0;
        grbl = this;
    }

    public void start(String filename)
    {
        // directory to grbl package
        String directoryGrbl = Paths.get("").toAbsolutePath().toString() +"\\src\\grbl\\";

        // directory to gcode package
        String directoryGcode = Paths.get("").toAbsolutePath().toString() +"\\resources\\gcode\\";

        // directory to temp file
        String directoryTemp = Paths.get("").toAbsolutePath().toString() +"\\src\\grbl\\temp\\";

        // Modifies to gbrl acceptable gcode
        Modifier m = new Modifier(filename, directoryGcode);
        m.modify();

        // api needs grbl, gcode, temp
        run(filename,directoryGrbl, directoryGcode, directoryTemp, m.getCommandSize());
    }

    public double getCoordinateX() {
        return x;
    }

    public double getCoordinateY() {
        return y;
    }

    public double getCoordinateZ() {
        return z;
    }

    public double getCoordinateA() {
        return a;
    }

    public double getPercentage() {
        return percentage;
    }

    public void run(String filename, String directoryGrbl, String directoryGcode, String directoryTemp, int size)
    {
        try{

            BufferedReader in = new BufferedReader(new FileReader(new File(directoryGcode, filename)));  // gcode
            int commandsRead = 0;
            StringBuilder response = new StringBuilder();

            // will stop at end of file
            while(commandsRead < size)
            {
                updateCoordinates();

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

                // execute stream.py with the file created, get input stream as a response
                Process process = CmdLine.executeCmdAsThread("py " +  directoryGrbl + "stream.py "+ directoryTemp + "tempfile.txt\n");
                BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while (true)
                {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    response.append(line).append(System.lineSeparator());
                }
                // set percentage done
                percentage = ((double)commandsRead / (double) size) * 100.00;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void updateCoordinates() { //TODO ask grbl for coordinates in real time
        /*
        Process process = CmdLine.executeCmdAsThread("?\n");
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

        try {
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                // do something with the line
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        */
    }
}
    