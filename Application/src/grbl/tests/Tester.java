package grbl.tests;

import java.io.*;
import java.util.*;
import utils.CmdLine;
import utils.Constants;


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
        ArrayList<String> files= new ArrayList<>();
        File directory = new File(Constants.getInputTestPath());

        try {
            for (File file : directory.listFiles()) {
                files.add(file.getPath());
                System.out.println(file.getPath());
            }
        }catch(NullPointerException e){
            System.out.println("Trying to access: " + directory.toString() + " from " + Constants.getInputTestPath());
        }

        return files;
    }

    private static void send(String file) //send using stream.py
    {
        String command = "stream.py" + file + " " + Constants.getPort();
        CmdLine.executeCmd(command, true);
    }

    private static String read()
    {
        // return the ok
        return "";
    } // read response from port

    private static void evaluate()
    {
        List<String> files = loadFiles();
        while(files.size() > 0)
        {
            String file = files.remove(0);
            send(file);
            String response = read();

            if(response != "ok")
                System.out.println("ERROR "+ response + " in File " + file);
            else
                System.out.println("Correct");
        }
    }

    public static void main(String [] args)
    {
        //no need to actively find port for testings
        loadFiles();
        evaluate();
    }
}