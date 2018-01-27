package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashSet;

public class RequestHandler extends Task<Void>
{
    private ArrayList<String> response;
    private HashSet<String> viewed;

    private String request;

    RequestHandler()
    {
        this.response = new ArrayList<>();
        this.viewed = new HashSet<>();
    }

    @Override
    public Void call() throws Exception
    {
        while(true)
        {
            response = APIgrbl.grbl.getResponse();
            if(response.size() > 0 && !viewed.contains(response.get(0)))
            {
                for(String s : response)
                {
                    updateMessage(s);
                    viewed.add(s);
                    Thread.sleep(1000000);
                }
            }

            if(isCancelled())
                return null;
        }
    }
}