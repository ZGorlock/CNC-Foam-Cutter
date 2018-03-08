package grbl;

import utils.CmdLine;
import utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DetectGrbl {

    /**
     *  The Machine type to be set by detect.py
     */
    private String type;
    private String cncName = "123";
    private String hotName = "231";

    public DetectGrbl(){
        detectMachine();
    }

    private void detectMachine()
    {
        Process process = null;

        // The command needed to run detect.py
        String command = "\"import detect; detect.Detect().getName()\"";

        while (process == null) {
            process = CmdLine.executeCmdAsThread("py " + "-c " + command);
            if (process != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line = "";
                while (line.isEmpty()) {
                    try {
                        line = r.readLine();

                        if (line != null && !line.isEmpty()) {
                            this.type = line;
                        }
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("Error attempting to run detect.py! Reattempting...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     *
     * @return the detected machine
     */
    public String getType()
    {
        return this.type;
    }

}
