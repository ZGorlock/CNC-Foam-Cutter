package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import javafx.concurrent.Task;

public class BackgroundProcessUI extends Task<Void>
{
    private int index;

    BackgroundProcessUI(int index)
    {
        this.index = index;
    }

    @Override
    protected Void call() throws Exception
    {
        while(true)
        {
            String coordinate;

            switch (index)
            {
                case 0:
                    coordinate = String.format("%.2f", APIgrbl.grbl.getCoordinateX());
                    break;
                case 1:
                    coordinate = String.format("%.2f", APIgrbl.grbl.getCoordinateY());
                    break;
                case 2:
                    coordinate = String.format("%.2f", APIgrbl.grbl.getCoordinateZ());
                    break;
                case 3:
                    coordinate = APIgrbl.grbl.getStatus();
                    break;
                case 4:
                    coordinate = String.format("%.2f", APIgrbl.grbl.getPercentage()) + " %";
                    break;
                default:
                    coordinate = "";
                    break;
            }

            updateMessage(coordinate);

            if(isCancelled())
            {
                return null;
            }
        }
    }
}
