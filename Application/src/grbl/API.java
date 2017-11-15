import java.io.*;
import java.util.*;

public class API
{
    public static void Run(File file, String port)
    {
        try{
            
            BufferedReader in = new BufferedReader(new FileReader(file));
            int offset = 0;
            int length = 127;
            int x = 0;
            // will stop at end of file
            while(x >= 0) 
            {   
                // read every 127 characters and create a file with them for stream.py to use
                File temp = File.createTempFile("tempfile", ".tmp");
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                char [] cbuf = new char[127];

                // x is the number of characters read or -1 if end of file
                x = in.read(cbuf, offset, length);

                // write the gcode to the file
                String gcode = cbuf.toString();
                bw.write(gcode);
                bw.close();

                // execute stream.py
                CmdLine.executeCmd("stream.py tempfile.tmp " + port);

                // read starting from the last line read
                offset += length;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void getFile()
    {

    }

    private static String detectPort()
    {
        return null;
    }

    public static void Main(String [] args)
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
        
        Run(file, port);
    }
}