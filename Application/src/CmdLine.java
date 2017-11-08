/*
 * File:    CmdLine.java
 * Package:
 * Author:  Zachary Gill
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Provides access to the Windows command line.
 */
public class CmdLine
{
    
    //Functions
    
    /**
     * Executes a command on the system command line.
     *
     * @param cmd  The command to execute.
     * @param wait Whether to wait for the execution to finish or not.
     * @return The output.
     */
    public static String executeCmd(String cmd, boolean wait)
    {
        try {
            ProcessBuilder builder = buildProcess(cmd, true);
            
            Process process = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder response = new StringBuilder();
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                response.append(line).append(System.lineSeparator());
            }
            
            if (wait) {
                process.waitFor();
            }
            r.close();
            process.destroy();
            
            return response.toString();
            
        } catch (IOException | InterruptedException e) {
            return "";
        }
    }
    
    /**
     * Executes a command on the system command line.
     *
     * @param cmd The command to execute.
     * @return The output.
     */
    public static String executeCmd(String cmd)
    {
        return executeCmd(cmd, true);
    }
    
    /**
     * Executes a command on the system command line as a thread.
     *
     * @param cmd The command to execute.
     * @return The process running the command execution.
     */
    public static Process executeCmdAsThread(String cmd)
    {
        try {
            ProcessBuilder builder = buildProcess(cmd, true);
            
            return builder.start();
            
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Builds a process from a command.
     *
     * @param cmd              The command to build a process for.
     * @param useScriptCommand Whether or not to include the script command at the beginning ("cmd.exe  /c")
     * @return The process that was built.
     */
    private static ProcessBuilder buildProcess(String cmd, boolean useScriptCommand)
    {
        ProcessBuilder builder;
        if (useScriptCommand) {
            builder = new ProcessBuilder("cmd.exe", "/c", cmd);
        } else {
            builder = new ProcessBuilder(cmd);
        }
        builder.redirectErrorStream(true);
        
        return builder;
    }
    
}
