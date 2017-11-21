package grbl;

import utils.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class APIgrbl {

            public static void run(String filename, String directoryGrbl, String directoryGcode, String directoryTemp, int size)
            {
                try{
    
                    BufferedReader in = new BufferedReader(new FileReader(new File(directoryGcode, filename)));  // gcode
                    int commandsRead = 0;
                    StringBuilder response = new StringBuilder();

                    // will stop at end of file
                    while(commandsRead < size)
                    {
                        // read every 127 characters and create a file with them for stream.py to use
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directoryTemp,"tempfile.txt")));
                        Queue<String> carryOver = new ArrayDeque<>();
                        StringBuilder sb = new StringBuilder();

                        int x = 0;
                        while(x < 127)
                        {
                            if(carryOver.size() == 1)
                            {
                                String carried = carryOver.remove();
                                sb.append(carried);
                                sb.append('\n');

                                commandsRead += 1;

                                // must add one for added chars '\n'
                                x += carried.length() + 1;
                            }

                            String newCommand = in.readLine();

                            if(newCommand != null)
                            {
                                if((newCommand.length() + x + 1) < 127)
                                {
                                    // update counts
                                    commandsRead += 1;
                                    x += newCommand.length() + 1;

                                    // append what will be written
                                    sb.append(newCommand);
                                    sb.append('\n');
                                }
                                else
                                {
                                    // end of 127 char limit
                                    carryOver.add(newCommand);
                                    x = 127;
                                }
                            }else
                            {
                                // end of file, stopping conditions
                                x = 127;
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
                        double percentage = ((double)commandsRead / (double) size) * 100.00;
                        System.out.println(percentage + "% done");
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            public APIgrbl(){}
    
            public static void start(String filename)
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
    }
    