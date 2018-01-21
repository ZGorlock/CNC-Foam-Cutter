package grbl;

import utils.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class APIgrbl extends Thread
{
    private double x;
    private double y;
    private double z;
    private double percentage;
    private String status;
    private String filename;
    public static APIgrbl grbl;

    public APIgrbl(String filename){
        percentage = x = y = z = 0.0;
        status = "Off";
        grbl = this;
        this.filename = filename;
    }

    public void run()
    {
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
                    updateCoordinates(line);
                }
                // set percentage done
                percentage = ((double)commandsRead / (double) size) * 100.00;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void updateCoordinates(String line)
    {
        String [] decomposed = line.split(",");
        if(decomposed[0].compareTo("") == 0) return;
        setStatus(decomposed[0].substring(1));
        String [] first = decomposed[1].split(":");
        setX(Double.parseDouble(first[1]));
        setY(Double.parseDouble(decomposed[2]));
        setZ(Double.parseDouble(decomposed[3]));
    }
}
    