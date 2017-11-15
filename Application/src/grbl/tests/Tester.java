package grbl.tests;

import java.io.*;
import java.util.*;
// import cmdline.class todo

/*
    This is the testing class for grbl,
    It will load files from the testing 
    folder that represent a series of 
    g code instructions and prompt grbl
    for an  appropriate response 'ok'.

    Author @Nicolas Lopez Bravo
*/

public class Tester
{   //grabs all files from destinated folder
    private static ArrayList<String> loadFiles()
    {
        ArrayList<String> files = new ArrayList<String>();
        File directory = new File("directory"); // get tests directory
        
        for (File file : directory.listFiles())
        {
            files.add(file.getPath());
        }
        return files;
    }

    private static void send(String file, String port) //send using stream.py
    {
        String command = "stream.py" + file + " " + port;
        //CmdLine cmdLine = new CmdLine();                          //todo cmdline dependency
        //cmdLine.executeCmd(command, true);
    }

    private static String read()
    {
        return "";
    } // read response from port

    private static void evaluate(String port)
    {
        List<String> files = loadFiles();
        while(files.size() > 0)
        {
            String file = files.remove(0);
            send(file,port);
            String response = read();

            if(response != "ok")
                System.out.println("ERROR "+ response + " in File " + file);
            else
                System.out.println("Correct");
        }
    }

    public static void main(String [] args)
    {
        evaluate("COM3");
    } //no need to actively find port for testing
}