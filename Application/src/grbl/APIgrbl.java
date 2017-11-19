package grbl;

import utils.*;
import java.io.*;

public class APIgrbl {

        private static boolean checkCommand(char [] cbuf, boolean newLine)
        {
            for(int i = 0; i < cbuf.length; i++)
            {
                if(newLine) // first time or new line has been read
                {
                    // if next command is not an acceptable gcode, skip the line.
                    StringBuilder newCommand = new StringBuilder();
                    while (cbuf[i] != ' ' && i < cbuf.length)
                        newCommand.append(cbuf[i++]);

                    if (!Constants.getAcceptableSet().contains(newCommand))
                    {
                        System.out.println("ERROR UNSUPORTED COMMAND " + newCommand);
                        return false;
                    }
                }

                if(cbuf[i] == '\n') newLine = true;
                else newLine = false;
            }
            return true;
        }

        public static void run(File file)
        {
            try{

                BufferedReader in = new BufferedReader(new FileReader(file));  // gcode
                int offset = 0;
                int length = 127;
                int x = 0;
                File directory = new File(Constants.getTemp());

                // will stop at end of file
                while(x >= 0)
                {
                    // read every 127 characters and create a file with them for stream.py to use
                    File temp = File.createTempFile("tempfile", ".tmp" , directory);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                    char [] cbuf = new char[127];
                    boolean newLine = false;
                    if(x == 0) newLine = true;

                    // x is the number of characters read or -1 if end of file
                    x = in.read(cbuf, offset, length);

                    // Make sure code is compilable
                    if(!checkCommand(cbuf, newLine)) return;

                    // write the gcode to the file so it can be streamed
                    String gcode = cbuf.toString();
                    bw.write(gcode);
                    bw.close();

                    // execute stream.py with the file created, get input stream as a response
                    Process process = CmdLine.executeCmdAsThread("stream.py tempfile.tmp\n");
                    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    
                    StringBuilder response = new StringBuilder();
                    String line;
                    while (true) {
                        line = r.readLine();
                        System.out.println(line +" is the line");
                        if (line == null) {
                            break;
                        }
                        response.append(line).append(System.lineSeparator());
                    }
                    //System.out.println(response.toString());

                    // read starting from the last line read
                    offset += length;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private static String detectPort()
        {
            Process process = CmdLine.executeCmdAsThread("detect.py ");
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line = null;
            try {
                line = r.readLine();
            }catch(IOException e)
            {
                // Unable to read line
                System.out.println("Unable to run detect.py properly");
            }
            return line;
        }

        private static File getFile()
        {
            File f = new File(Constants.getGsrc());
            File[] matchingFiles = f.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("resources/gcode");
                }
            });

            return null;
        }
        public APIgrbl(){}

        public static void start()
        {
            File file = getFile();
            String port = detectPort();

            if(port == null)
                System.out.println("Please connect your machine...");

            // will not advance until machine is connected
            while(port == null)
            {
                port = detectPort();
            }

            run(file);
        }

}
