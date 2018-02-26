package grbl;

import java.io.*;
import java.util.*;

public class Modifier
{
    private ArrayList<String> commands;
    private String file;
    private String directory;
    Modifier(String file, String directory)
    {
        this.file = file;
        this.directory = directory;
        commands = new ArrayList<String>();
    }

    public ArrayList<String> getCommands()
    {
        return this.commands;
    }

    public int getCommandSize(){return this.commands.size();}

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

    private static final Character[] UNACCEPTABLE_PARAMETERS = new Character[]
            {       'E', 'A'
            };

    private static final HashSet<Character> UNACCEPTABLE_PARAMETERS_SET = new HashSet<>(Arrays.asList(UNACCEPTABLE_PARAMETERS));

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
            BufferedReader br = new BufferedReader(new FileReader(new File(this.directory, this.file)));
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
            //none
        }

    }

    private void convertBadParameters()
    {
        for(int index = 0; index < commands.size(); index++)
        {
            String s = commands.get(index);
            char [] cbuf = s.toCharArray();
            int i = 0;
            boolean removed = false;
            while(i < cbuf.length)
            {
                if(UNACCEPTABLE_PARAMETERS_SET.contains(cbuf[i]))
                {
                    cbuf[i] = 'F';
                    int j = i + 1;
                    while(j < cbuf.length)
                    {
                        if(cbuf[j++] == 'F')
                        {
                            // must remove instead of convert
                            removed = removeBadParameters(s,index);
                        }
                    }
                    if(!removed)
                    {
                        commands.remove(index);
                        StringBuilder newsb = new StringBuilder();
                        boolean nonEmpty = false;

                        for(char c : cbuf)
                        {
                            if((c != ' ' || c != '\n') && !s.isEmpty())
                                nonEmpty = true;
                            newsb.append(c);
                        }
                        String news = newsb.toString();

                        if(nonEmpty)
                            commands.add(index, news);
                    }
                }
                i++;
            }
        }
    }

    private boolean removeBadParameters(String s, int index)
    {
        char [] cbuf = s.toCharArray();
        int i = 0;

        while(i < cbuf.length)
        {
            if(UNACCEPTABLE_PARAMETERS_SET.contains(cbuf[i]))
            {
                // i will be the starting index, j will be the ending index
                int j = i;
                while(j < cbuf.length && cbuf[j++] != ' ') {}
                StringBuilder newSB = new StringBuilder(s.substring(0,i));
                newSB.append(s.substring(j));
                // make sure it ends in a new line
                if(!s.contains("\n"))
                    newSB.append('\n');


                commands.remove(index);

                String news = newSB.toString();
                //  Make sure you didn't elimintate the whole command
                boolean continueOnThisCommand = false;
                for(char c : news.toCharArray()) {
                    if (c != ' ' || c != '\n')
                        continueOnThisCommand = true;
                        break;
                }

                // Reset the command you're looking at
                if(continueOnThisCommand)
                {
                    commands.add(index, news);
                    cbuf = news.toCharArray();
                }else{
                    return true;
                }
                i = j;
            }
            i++;
        }
        return true;
    }

    private void writeCommands()
    {
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.directory,this.file)));

            for(String s : commands)
            {
                bw.write(s);
            }

            bw.close();
        }catch(IOException e)
        {
            // none
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
        convertBadParameters();
        writeCommands();
    }
}