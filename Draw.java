package com.company;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Draw {

    //Graph object
    private Graph graph;

    //Mouse location variables
    private int mouseX, mouseY;
    //Whether the user is currently drawing (holding down mouse)
    boolean isDrawing;
    //Stores the points of the line
    private ArrayList<int[]> points = new ArrayList<>();
    //Whether the line is displayed
    private boolean displayLine;

    public Draw(Graph graph){
        //Initializes objects and variables
        this.graph = graph;
        displayLine = false;
    }

    public void paint(Graphics g){

        //Draws the line by cycling through the points array and drawing a line connecting a point and its previous point
        if (displayLine) {
            for (int i = 1; i < points.size(); i++) {
                g.drawLine(points.get(i - 1)[0], points.get(i - 1)[1], points.get(i)[0], points.get(i)[1]);
            }
        }

    }

    //Sets the mouseX and mouseY, adds points to the mouse location when drawing
    public void setMouseLocation(MouseEvent e){
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        if (isDrawing){
            points.add(new int[]{mouseX, mouseY});
        }
    }

    //Allows drawing, displays the line and clears the previous points array
    public void setMousePressed(MouseEvent e){
        isDrawing = true;
        displayLine = true;
        points.clear();
    }

    //Stops drawing, hides the line, and gives the graph object the points array to process
    public void setMouseReleased(MouseEvent e){
        isDrawing = false;
        displayLine = false;
        if (points.size()>1) {
            graph.process(points);
        }
        else{
            graph.clear();
        }
    }

}
