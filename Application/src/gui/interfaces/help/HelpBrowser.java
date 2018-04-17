/*
 * File:    HelpBrowser.java
 * Package: gui.interfaces.help
 * Author:  Zachary Gill
 */

package gui.interfaces.help;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import utils.MachineDetector;

import java.net.URL;

/**
 * The web browser for the Help page.
 */
public class HelpBrowser extends Region
{
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
    public HelpBrowser()
    {
        getStyleClass().add("browser");
        URL url = this.getClass().getResource(MachineDetector.isHotWireMachine() ? "help_hotwire.html" : "help_cnc.html");
        webEngine.load(url.toString());
        getChildren().add(browser);
    }
    
    private Node createSpacer()
    {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    @Override
    protected void layoutChildren()
    {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }
    
    @Override
    protected double computePrefWidth(double height)
    {
        return 750;
    }
    
    @Override
    protected double computePrefHeight(double width)
    {
        return 500;
    }
    
}
