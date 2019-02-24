/*
Frank Chen
ICS4U
January 12 2019
This program allows a user drawn line to be converted into a polynomial function
This is done by using linear and polynomial regression to determine the function of best fit
 */

package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        //Window size
        int winSize = 600;

        //Initializes frame
        JFrame frame = new JFrame("Sketch Graph");

        //Initializes a container panel
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(winSize,winSize));

        //Initializes the settings and graph panel
        Settings settingsPanel = new Settings(winSize, Modes.AUTO);
        Graph graphPanel = new Graph(winSize, Modes.AUTO, settingsPanel);
        settingsPanel.setGraph(graphPanel);

        //Adds the settings and graph panel to the container panel
        container.add(graphPanel);
        container.add(settingsPanel, BorderLayout.EAST);

        //Adds the container panel to the frame
        frame.getContentPane().add(container);
        //Adjusts frame properties
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
