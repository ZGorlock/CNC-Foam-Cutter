package grbl;

import java.io.*;
import java.util.*;

public class Modifier
{
    private ArrayList<String> commands;
    private String file;

    Modifier(String file)
    {
        this.file = file;
        commands = new ArrayList<String>();
    }

    public ArrayList<String> getCommands()
    {
        return this.commands;
    }

    private static final String[] ACCEPTABLE_GCODE = new String[]
    {       "G0", "G1", "G2", "G3", "G4", "G10L2", "G10L20", "G17", "G18",
            "G19", "G20", "G21", "G28", "G28.1", "G30", "G30.1", "G38.2",
            "G38.3", "G38.4", "G38.5", "G53", "G80", "G90", "G91", "G91.1",
            "G92", "G92.1", "G93", "G94", "G40", "G43.1", "G49", "G54", "G55",
            "G56", "G57", "G58", "G59", "G61", "M0", "M1", "M2", "M30*","M7*",
            "M8","M9","M3", "M4", "M5","F", "I", "J", "K", "L", "N", "P", "R",
            "S", "T", "X", "Y", "Z", "G00", "G01", "Z01"
    };

    private static final HashSet<String> ACCEPTABLE_SET = new HashSet<>(Arrays.asList(ACCEPTABLE_GCODE));

    private void removeBadCommands()
    {
        for(int index = 0; index < commands.size(); index++)
        {
            String s = commands.get(index);
            char [] cbuf = s.toCharArray();
            int i = 0;

            // find the command
            while((i < cbuf.length) && (cbuf[i] != ' ' ))
            {
                i++;
            }
            
            // check the acceptable set
            if(!ACCEPTABLE_SET.contains(s.substring(0, i)))
            {
                commands.remove(index); //continue or remove
                index--;
            }
        }
    }

    private void removeComments()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(this.file));
            String st;
            while ((st = br.readLine()) != null)
            {
                StringBuilder sb = new StringBuilder();

                char [] cbuf = st.toCharArray();

                for(int i = 0; i < cbuf.length; i++)
                {
                    if(cbuf[i] == ';')  break;
                    sb.append(cbuf[i]);
                }

                // write string
                if(sb.length() > 1) sb.append('\n');
                String str = sb.toString();
                commands.add(str);
            }
            br.close();
        }catch(IOException e)
        {

        }

    }

    private void writeCommands()
    {
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.file));

            for(String s : commands)
            {
                bw.write(s);
            }

            bw.close();
        }catch(IOException e)
        {

        }
    }

    public void printCommands()
    {
        for(String s : commands)
        {
            System.out.print(s);
        }
    }

    public void modify()
    {
        removeComments();
        removeBadCommands();
        writeCommands();
    }
}