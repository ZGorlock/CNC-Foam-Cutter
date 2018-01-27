package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import javafx.concurrent.Task;

import java.util.ArrayList;

public class RequestHandler extends Task<Void>
{
    private String request;
    RequestHandler(String command)
    {
        this.request = command;
    }

    @Override
    protected Void call() throws Exception
    {
        ArrayList<String> response;
        APIgrbl.grbl.sendRequest(request);
        response = APIgrbl.grbl.getResponse();

        for(String str : response)
        {
            updateMessage(str);
        }

        return null;
    }
}