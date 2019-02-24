package com.company;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class Settings extends JPanel implements ActionListener {

    //OBJECTS

    private Graph graph;
    private Modes mode;

    //JCOMPONENTS

    private JButton modeButton;
    private JButton equationButton;
    private JButton gridButton;
    private JButton helpButton;
    private JSlider degreeSlider;
    private Hashtable degreeLabels;
    private JPanel degreePanel;
    private JLabel degreeLabel;

    //Window size
    private int winSize;

    //VARIABLES

    private boolean showEquation;
    private boolean showGrid;

    public Settings(int winSize, Modes mode){
        //Initializes this (Settings)
        setVisible(false);
        setPreferredSize(new Dimension(winSize/3,winSize));
        setBackground(new Color(200,200,200));
        setLayout(new GridLayout(5,1));

        //Initializes objects
        this.mode = mode;
        this.winSize = winSize;

        //Initializes variables
        showEquation = true;
        showGrid = true;

        //Initializes JComponents
        initModeButton();
        initDegreeComponents();
        initEquationButton();
        initGridButton();
        initHelpButton();
    }

    //Initializes the mode button
    public void initModeButton(){
        modeButton = new JButton("MODE: AUTO");
        modeButton.setBorderPainted(false);
        modeButton.setBackground(new Color(180,180,180));
        modeButton.addActionListener(this);
        add(modeButton);
    }

    //Initializes the degree components and panel
    private void initDegreeComponents(){

        degreeLabel = new JLabel("DEGREE: 2");
        degreePanel = new JPanel(new GridLayout(2,1));
        degreePanel.setBackground(new Color(200,200,200));

        initDegreeLabels();
        initDegreeSlider();

        degreePanel.add(degreeLabel);
        degreePanel.add(degreeSlider);
        add(degreePanel);
    }

    //Initializes the degree slider labels
    private void initDegreeLabels(){
        degreeLabels = new Hashtable();

        JLabel label1 = new JLabel("2");
        JLabel label2 = new JLabel("20");

        degreeLabels.put(2, label1);
        degreeLabels.put(20,label2);
    }

    //Initializes the degree slider
    private void initDegreeSlider(){
        degreeSlider = new JSlider(2,20,2);
        degreeSlider.setPreferredSize(new Dimension(winSize/3,degreeSlider.getPreferredSize().height));
        degreeSlider.setMajorTickSpacing(5);
        degreeSlider.setMinorTickSpacing(1);
        degreeSlider.setPaintLabels(true);
        degreeSlider.setPaintTicks(true);
        degreeSlider.setForeground(Color.BLACK);
        degreeSlider.setOpaque(false);
        degreeSlider.setLabelTable(degreeLabels);
        degreeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                int degree = source.getValue();
                graph.setDegree(degree);
                setDegreeLabel(degree);
            }
        });
        add(degreeSlider);

    }

    //Initializes the equation button
    private void initEquationButton(){
        equationButton = new JButton("HIDE EQUATION");
        equationButton.setBorderPainted(false);
        equationButton.setBackground(new Color(180,180,180));
        equationButton.addActionListener(this);
        add(equationButton);
    }

    //Initializes the grid button
    private void initGridButton(){
        gridButton = new JButton("HIDE GRID");
        gridButton.setBorderPainted(false);
        gridButton.setBackground(new Color(200,200,200));
        gridButton.addActionListener(this);
        add(gridButton);
    }
    //Initializes the help button
    private void initHelpButton(){
        helpButton = new JButton("HELP");
        helpButton.setBorderPainted(false);
        helpButton.setBackground(new Color(180,180,180));
        helpButton.addActionListener(this);
        add(helpButton);
    }

    //Opens the help text pane
    public void showHelp(){
        String message = "This program allows a user drawn line to be converted into a polynomial function.\n" +
                "This is done by using linear and polynomial regression to determine the function of best fit.\n" +
                "CONTROLS:\n" +
                "Mouse Drag: Draws a line when the mouse is pressed and creates the graph upon release\n" +
                "Mouse Click: Clears the graph\n" +
                "Mouse Scroll: Changes the scale of the graph";



        JOptionPane help = new JOptionPane();
        help.showMessageDialog(null,message,"Help",JOptionPane.QUESTION_MESSAGE);
    }

    //Initializes the degree label
    public void setDegreeLabel(int degree){
        degreeLabel.setText("DEGREE: " + degree);
    }

    //Cycles through the different modes
    public void switchMode(){
        if (mode == Modes.LINEAR){
            mode = Modes.QUADRATIC;
            modeButton.setText("MODE: QUADRATIC");
        }
        else if (mode == Modes.QUADRATIC){
            mode = Modes.POLYNOMIAL;
            modeButton.setText("MODE: POLYNOMIAL");
        }
        else if (mode == Modes.POLYNOMIAL){
            mode = Modes.AUTO;
            modeButton.setText("MODE: AUTO");
        }
        else if (mode == Modes.AUTO){
            mode = Modes.LINEAR;
            modeButton.setText("MODE: LINEAR");
        }
        graph.changeMode(mode);
    }

    //Toggles the equation visibility and updates button text
    public void toggleEquation(){
        showEquation = !showEquation;
        graph.setEquationVisible(showEquation);
        if (showEquation){
            equationButton.setText("HIDE EQUATION");
        }
        else{
            equationButton.setText("SHOW EQUATION");
        }

    }

    //Toggles the grid visibility and updates button text
    public void toggleGrid(){
        showGrid = !showGrid;
        graph.setGridVisible(showGrid);
        if (showGrid){
            gridButton.setText("HIDE GRID");
        }
        else{
            gridButton.setText("SHOW GRID");
        }
    }

    //Initializes the graph object
    public void setGraph(Graph graph){
        this.graph = graph;
    }

    //Detects button press
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == modeButton){
            switchMode();
        }
        else if (e.getSource() == equationButton){
            toggleEquation();
        }
        else if (e.getSource() == gridButton){
            toggleGrid();
        }
        else if (e.getSource() == helpButton){
            showHelp();
        }
    }

}
