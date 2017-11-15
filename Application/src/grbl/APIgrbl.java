package grbl;

import java.io.*;
// import cmdline class

public class APIgrbl {

        public static void Run(File file, String port)
        {
            try{

                BufferedReader in = new BufferedReader(new FileReader(file));
                int offset = 0;
                int length = 127;
                int x = 0;
                File directory = new File("C:\\example");                   //todo find path
                // will stop at end of file
                while(x >= 0)
                {
                    // read every 127 characters and create a file with them for stream.py to use
                    File temp = File.createTempFile("tempfile", ".tmp" , directory);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                    char [] cbuf = new char[127];

                    // x is the number of characters read or -1 if end of file
                    x = in.read(cbuf, offset, length);

                    // write the gcode to the file
                    String gcode = cbuf.toString();
                    bw.write(gcode);
                    bw.close();

                    // execute stream.py
                    //CmdLine.executeCmd("stream.py tempfile.tmp " + port);             //todo

                    // read starting from the last line read
                    offset += length;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private static File getFile()
        {
            File f = new File("C:\\example");                   //todo
            File[] matchingFiles = f.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("gcode");
                }
            });
            return null;
        }

        private static String detectPort()
        {
            return null;
        }               //todo

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
