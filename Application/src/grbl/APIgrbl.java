package grbl;

import utils.*;
import java.io.*;
import java.nio.file.Paths;

public class APIgrbl {
    
            public static void run(String filename)
            {
                try{
    
                    BufferedReader in = new BufferedReader(new FileReader(filename));  // gcode
                    int offset = 0;
                    int length = 127;
                    int x = 0;
    
                    // will stop at end of file
                    while(x >= 0)
                    {
                        // read every 127 characters and create a file with them for stream.py to use
                        BufferedWriter bw = new BufferedWriter(new FileWriter("tempfile.txt"));
                        char [] cbuf = new char[127];
    
                        // x is the number of characters read or -1 if end of file
                        x = in.read(cbuf, offset, length);
    
                        // write the gcode to the file so it can be streamed
                        String gcode = cbuf.toString();
                        System.out.println(gcode);
                        bw.write(gcode);
                        bw.close();

                        // this is how to get the path of the file to be executed, it brings you to application\
                        String directory = Paths.get("").toAbsolutePath().toString() +"\\src\\grbl\\";

                        // execute stream.py with the file created, get input stream as a response
                        Process process = CmdLine.executeCmdAsThread("py " +  directory + "stream.py tempfile.txt\n");
                        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        
                        StringBuilder response = new StringBuilder();
                        String line;
                        while (true) {
                            line = r.readLine();
                            System.out.println(line + " rec");
                            if (line == null) {
                                break;
                            }
                            response.append(line).append(System.lineSeparator());
                        }
                        // read starting from the last line read
                        offset += length;
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            public APIgrbl(){}
    
            public static void start(String filename)
            {
                Modifier m = new Modifier(filename);
                m.modify();
                run(filename);
            }
    
    }
    