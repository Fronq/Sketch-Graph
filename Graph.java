package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Graph extends JPanel {

    //JCOMPONENTS, OBJECTS, AND HELPER VARIABLES;

    private Draw draw;
    private Settings settings;
    private JTextArea equationTextArea;
    private JScrollPane equationScroll;
    private JLabel yInterLabel;
    private JButton settingsButton;
    private JPanel northPanel, southPanel;
    private boolean isSettingsVisible;
    private Modes mode;

    //SIZING VARIABLES

    //Window size
    private int winSize;
    //Window radius
    private int rad;
    //Distance between tick marks
    private int scaleSize;
    //The value of the distance between tick marks
    private int scale;
    //The length of the tick
    private int tickSize;
    //The size of the tick label text
    private int textSize;
    //The offset of the text
    private int textOffset;
    //The offset of the text as a result of blank spaes
    private int blankOffset;
    //The size of the text for the scale labels
    private Font font;
    //Determines whether the grid is shown
    private boolean showGrid;

    //GRAPH CALCULATION VARIABLES

    //Whether the graph is shown or not
    private boolean drawGraph = false;
    //The y values to the right and the left of the y-axis
    private double[] yRight,yLeft;
    //The coefficients for a quadratic function or linear function (minus the c)
    private double a, b,c;
    //The coefficients for a polynomial function
    private double[] A;
    //The degree of the function
    private int deg, oldDeg, maxDeg;
    //The coordinates of the drawn line
    private ArrayList<int[]> points;
    //The value of the y-intercept
    private double yinter;
    //The value of the coefficient of determination
    private double rSquared;
    private double coDetermination[];

    public Graph(int winSize, Modes mode, Settings settings){

        //Initializes this (Graph)
        setPreferredSize(new Dimension(winSize,winSize));
        setLayout(new BorderLayout());

        //Initializes objects
        draw = new Draw(this);
        this.mode = mode;
        this.settings = settings;

        //Initializes sizing variables
        this.winSize = winSize;
        rad = winSize/2;
        scaleSize = winSize/20;
        scale = 1;
        tickSize = winSize/300;
        textSize = winSize/50;
        textOffset = textSize/3;
        blankOffset = winSize/24;
        font = new Font("Arial", Font.PLAIN, textSize);
        showGrid = true;

        //Sets the default degree to 2 and default max degree to 20
        deg = 2;
        maxDeg = 20;

        //Initializes Jcomponents and input

        initInput();
        initDirectionPanels();
        isSettingsVisible = false;
        initSettingsButton();
        initEquationTextArea();
        initEquationScroll();
        initYInterceptLabel();
    }

    //Initializes the northPanel and southPanel
    public void initDirectionPanels(){
        northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        add(northPanel, BorderLayout.NORTH);

        southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        add(southPanel, BorderLayout.SOUTH);
    }

    //Initializes the settings button
    public void initSettingsButton(){
        settingsButton = new JButton("<<");
        settingsButton.setFocusPainted(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setBackground(new Color(0,0,0,0));
        settingsButton.setOpaque(false);
        //Checks to see when a button is hovered over
        settingsButton.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ButtonModel model = (ButtonModel) e.getSource();
                if (model.isRollover()) {
                    settingsButton.setForeground(new Color(100,100,100));
                } else {
                    settingsButton.setForeground(Color.BLACK);
                }
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSettingsVisible = !isSettingsVisible;
                settings.setVisible(isSettingsVisible);

                if (isSettingsVisible){
                    settingsButton.setText(">>");
                }
                else{
                    settingsButton.setText("<<");
                }
            }
        });

        northPanel.add(settingsButton, BorderLayout.EAST);
    }

    //Initializes the equation text area
    public void initEquationTextArea(){

        equationTextArea = new JTextArea("y = ");
        equationTextArea.setFont(font);
        equationTextArea.setOpaque(false);
        equationTextArea.setEditable(false);
    }

    //Initializes the equation scroll bar
    public void initEquationScroll() {
        equationScroll = new JScrollPane(equationTextArea);
        equationScroll.setPreferredSize(new Dimension(winSize,35));
        equationScroll.setOpaque(false);
        southPanel.add(equationScroll, BorderLayout.WEST);

    }

    //Initializes the y intercept label
    public void initYInterceptLabel(){
        yInterLabel = new JLabel("Y-Intercept = ");
        yInterLabel.setOpaque(false);
        yInterLabel.setFont(font);
        yInterLabel.setForeground(new Color(100,100,100));
        northPanel.add(yInterLabel, BorderLayout.WEST);
    }

    public void setYInterceptLabel(){
        yInterLabel.setText("Y-Intercept = " + String.format("%.2f",yinter));
    }

    //Sets the equation text in the equation text area
    public void setEquationTextArea(double[] cfs){

        if (cfs.length == 0){
            equationTextArea.setText("");
        }
        else {
            //n is the amount of coefficients
            int n = cfs.length;
            //Sets the start of the equation
            String equation = "y = ";

            //Loops n times
            for (int i = 0; i < n; i++) {

                //Adds the coefficient to the equation, starting from the end of the array
                equation += cfs[n - 1 - i];

                //Adds a superscript with the dependent variable after the coefficient if necessary
                if (n > 2 && i < n - 2) {
                    String expo = superscript(n - 1 - i);
                    equation += "x" + expo + " + ";
                }
                //Adds the dependent variable after the coefficient if necessary
                else if (i == n - 2) {
                    equation += "x + ";
                }
            }

            equationTextArea.setText(equation);
        }
    }

    //Sets the equation text and scroll bar visibility
    public void setEquationVisible(boolean isVisible){
        equationScroll.setVisible(isVisible);
        equationTextArea.setVisible(isVisible);
    }

    //Sets the grid visibility
    public void setGridVisible(boolean isVisible){
        showGrid = isVisible;
    }

    //Clears the graph
    public void clear(){
        drawGraph = false;
        setEquationTextArea(new double[]{});
        points.clear();
    }

    //Initializes the mouseWheelListener, mouseListener, and the mouseMotionListener
    public void initInput(){
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoom(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                draw.setMouseReleased(e);
            }
            public void mousePressed(MouseEvent e){
                draw.setMousePressed(e);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                draw.setMouseLocation(e);
            }
        });
    }

    //Uses the mouse wheel rotation to zoom in and out of the graph
    public void zoom(MouseWheelEvent e){
        int notches = e.getWheelRotation();
        scale += notches;

        if (scale < 1)
            scale = 1;
        repaint();

        if (points != null)
            reprocess();

    }

    //Changes the mode of the graph
    public void changeMode(Modes mode){
        this.mode = mode;
    }
    //Sets the degree of the graph (only for polynomial mode)
    public void setDegree(int deg){
        this.deg = deg;
    }

    //Draws the ticks, labels, axis relative to the window size
    public void drawAxis(Graphics g){
        //Draws tick marks and labels from outside to inside
        for (int i = 0; i < 10;i++){

            //TICKS

            g.setColor(Color.BLACK);
            //Left
            g.drawLine(i*scaleSize,rad-tickSize,i*scaleSize,rad+tickSize);
            //Right
            g.drawLine(winSize-(i*scaleSize),rad-tickSize,winSize-(i*scaleSize),rad+tickSize);
            //Up
            g.drawLine(rad-tickSize,i*scaleSize,rad+tickSize,i*scaleSize);
            //Down
            g.drawLine(rad-tickSize,winSize-(i*scaleSize),rad+tickSize,winSize-(i*scaleSize));

            //After every 2 ticks the labels are drawn
            if (i % 2 == 0 && i != 0){

                //LABELS

                g.setFont(font);
                g.setColor(new Color(0,0,0,150));

                //blank is used to align the text depending on the number's amount of digits
                String blank = "";
                String number = "" + (scale*(10-i));
                int amountOfSpaces = 7-number.length();

                if (amountOfSpaces > 0){
                    for (int j = 0; j < (amountOfSpaces*1.5); j++){
                        blank += " ";
                    }
                }

                //Left
                g.drawString(blank + "-" + number, (i*scaleSize)-blankOffset-(2*textOffset), rad+textSize+textOffset);
                //Right
                g.drawString(blank + number, winSize-(i*scaleSize)-blankOffset-textOffset, rad+textSize+textOffset);
                //Up
                g.drawString(number, rad+(2*textOffset), (i*scaleSize)+textOffset);
                //Down
                g.drawString("-" + number, rad+(2*textOffset), winSize-(i*scaleSize)+textOffset);
            }

        }

        //Y axis
        g.drawLine(rad,0,rad,winSize);
        //X axis
        g.drawLine(0,rad,winSize,rad);
    }

    //Draws the grid lines
    public void drawGrid(Graphics g){
        for (int i = 0; i < 10; i++){

            g.setColor(new Color(0,0,0,30));

            //Left
            g.drawLine(i*scaleSize,0,i*scaleSize,winSize);
            //Right
            g.drawLine(winSize-(i*scaleSize),0,winSize-(i*scaleSize),winSize);
            //Up
            g.drawLine(0,i*scaleSize,winSize,i*scaleSize);
            //Down
            g.drawLine(0,winSize-(i*scaleSize),winSize,winSize-(i*scaleSize));
        }
    }

    //Relates the coordinates of the drawn line to create a formula
    public void process(ArrayList<int[]> points) {

        this.points = points;

        if (mode == Modes.LINEAR) {
            linearRegression();

            yRight = new double[rad];
            yLeft = new double[rad];

            //Uses the equation to create and store points into the two arrays (y = bx + a)
            for (int i = 0; i < rad; i++) {
                double yRightValue = b * (i / (scaleSize * 1.0 / scale)) + a;
                yRight[i] = convertY(yRightValue);
                double yLeftValue = b * (-i / (scaleSize * 1.0 / scale)) + a;
                yLeft[i] = convertY(yLeftValue);
            }

            //Update equation
            double[] cfs = {a, b};
            setEquationTextArea(cfs);
        }
        else if (mode == Modes.QUADRATIC) {

            //Quadratic Regression matrix equation:
            // [ΣXi^4 ΣXi^3 ΣXi^2] [a]   [ΣXi^2Yi]
            // [ΣXi^3 ΣXi^2 ΣXi  ] [b] = [ΣXiYi  ]
            // [ΣXi^2 ΣXi n      ] [c]   [ΣYi    ]

            //Initialize matrix variables
            double n = points.size();
            double sumXi = 0, sumXi2 = 0, sumXi3 = 0, sumXi4 = 0;
            double sumYi = 0, sumXiYi = 0, sumXi2Yi = 0;

            for (int i = 0; i < points.size(); i++) {

                //Converts the coordinates of the drawn line to points on the Cartesian plane
                double pointX = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale);
                double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);
                //Adds values
                sumXi += pointX;
                sumXi2 += pointX * pointX;
                sumXi3 += pointX * pointX * pointX;
                sumXi4 += pointX * pointX * pointX * pointX;
                sumYi += pointY;
                sumXiYi += pointY * pointX;
                sumXi2Yi += pointY * pointX * pointX;
            }

            //Initialize matrix row lines
            double[] aLine = {sumXi4, sumXi3, sumXi2, sumXi2Yi};
            double[] bLine = {sumXi3, sumXi2, sumXi, sumXiYi};
            double[] cLine = {sumXi2, sumXi, n, sumYi};

            //USE NAIVE GAUSSIAN ELIMINATION TO SOLVE MATRIX

            //Forward elimination, creates values of zero in the corner
            //Create zeroes by finding the factor between a value in the same column of two different rows
            //One row should be the row that contains the value being converted to zero and the other should be above it
            //The first row is subtracted by the second row multiplied by the factor
            // [x x x] [a]   [y]
            // [0 x x] [b] = [y]
            // [0 0 x] [c]   [y]
            //Step 1 - Creates two zeroes in the first column
            double qba = bLine[0] / aLine[0];
            double qca = cLine[0] / aLine[0];

            for (int i = 0; i < 4; i++) {
                bLine[i] -= aLine[i] * qba;
                cLine[i] -= aLine[i] * qca;
            }
            //Step 2 - Creates one zero in the second column
            double qcb = cLine[1] / bLine[1];
            for (int i = 0; i < 4; i++) {
                cLine[i] -= bLine[i] * qcb;
            }

            //Backwards substitution
            //Solve for c by isolating
            //Solve for b by isolating and substituting in c
            //Solve for a by isolating and substituting in b and c
            c = cLine[3] / cLine[2];
            b = (bLine[3] - (bLine[2] * c)) / bLine[1];
            a = (aLine[3] - (aLine[2] * c) - (aLine[1] * b)) / aLine[0];


            yRight = new double[rad];
            yLeft = new double[rad];

            //Uses the equation to create and store points into the two arrays (y = ax^2 + bx + c)
            for (int i = 0; i < rad; i++) {

                double xRight = i / (scaleSize * 1.0 / scale);
                double xLeft = -i / (scaleSize * 1.0 / scale);

                double yRightValue = (a * xRight * xRight) + (b * xRight) + c;
                yRight[i] = convertY(yRightValue);
                double yLeftValue = (a * xLeft * xLeft) + (b * xLeft) + c;
                yLeft[i] = convertY(yLeftValue);
            }

            double[] cfs = {c, b, a};
            setEquationTextArea(cfs);
        } else if (mode == Modes.POLYNOMIAL) {

            polynomialRegression();

            yRight = new double[rad];
            yLeft = new double[rad];

            //Uses the equation to create and store points into the two arrays
            // y = a0 + a1x + a2x + a3x + ... + aix
            for (int i = 0; i < rad; i++) {

                double xRight = i / (scaleSize * 1.0 / scale);
                double xLeft = -i / (scaleSize * 1.0 / scale);
                double yRightValue = 0;
                double yLeftValue = 0;

                for (int j = 0; j < deg + 1; j++) {
                    yRightValue += A[deg - j] * pow(xRight, deg - j);
                    yLeftValue += A[deg - j] * pow(xLeft, deg - j);
                }

                yRight[i] = convertY(yRightValue);
                yLeft[i] = convertY(yLeftValue);
            }
            setEquationTextArea(A);
        }
        else if (mode == Modes.AUTO) {

            coDetermination = new double[maxDeg];

            for (int d = 1; d < maxDeg+1; d++) {

                deg = d;
                polynomialRegression();

                //R^2 variables
                double yMean = 0;
                for (int i = 0; i < points.size(); i++) {
                    //Converts the coordinates of the drawn line to points on the Cartesian plane
                    double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);
                    yMean += pointY;
                }
                yMean /= (double) points.size();

                //SST - total sum of squares: Σ(y-ymean)^2
                //SSR - residual sum of sqaures: Σ(y-yprojected)^2
                double SST = 0, SSR = 0;

                for (int i = 0; i < points.size(); i++) {
                    //Gets values for y-ymean
                    double pointX = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale);
                    double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);
                    double yProjected = 0;

                    for (int j = 0; j < deg + 1; j++) {
                        yProjected += A[deg - j] * pow(pointX, deg - j);
                    }

                    double yMinusyMean = pointY - yMean;
                    double yMinusyProjected = pointY - yProjected;

                    //Adds values
                    SST += (yMinusyMean * yMinusyMean);
                    SSR += (yMinusyProjected * yMinusyProjected);
                }

                //coDetermination = 1-(SSR/SST);
                coDetermination[d-1] = SSR / ((points.size() - deg - 1));
                //System.out.println(coDetermination[d-1]+ " deg: " + deg);
            }

            double diff;
            double greatestDiff = 0;
            for (int i = 1; i < 9; i++){
                diff = coDetermination[i-1] - coDetermination[i];
                if (diff > greatestDiff){
                    greatestDiff = diff;
                    deg = i+1;
                }
            }
            calcRSqaured();
            //If the user drawn line is at least 90% of the accuracy of linear graph, the graph shown will be linear
            if (rSquared > 0.9){
                deg = 1;
            }

            polynomialRegression();

            yRight = new double[rad];
            yLeft = new double[rad];
            //Uses the equation to create and store points into the two arrays
            // y = a0 + a1x + a2x + a3x + ... + aix
            for (int i = 0; i < rad; i++) {

                double xRight = i / (scaleSize * 1.0 / scale);
                double xLeft = -i / (scaleSize * 1.0 / scale);
                double yRightValue = 0;
                double yLeftValue = 0;

                for (int j = 0; j < deg + 1; j++) {
                    yRightValue += A[deg - j] * pow(xRight, deg - j);
                    yLeftValue += A[deg - j] * pow(xLeft, deg - j);
                }

                yRight[i] = convertY(yRightValue);
                yLeft[i] = convertY(yLeftValue);
            }
            setEquationTextArea(A);
        }
        yinter = (rad - yRight[0]) / (scaleSize * 1.0 / scale);
        setYInterceptLabel();
        drawGraph = true;
    }

    public void calcRSqaured(){
        double xMean = 0, yMean = 0;
        for (int i = 0; i < points.size(); i++) {
            //Converts the coordinates of the drawn line to points on the Cartesian plane
            double pointX = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale);
            double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);

            xMean += pointX;
            yMean += pointY;
        }
        xMean /= (double) points.size();
        yMean /= (double) points.size();

        //FINDS Σ(x-xmean)*Σ(y-ymean), Σ(x-xmean)^2, Σ(y-ymean)^2 (sumXXYY, sumXXXX, sumYYYY)

        double sumXXYY = 0;
        double sumXXXX = 0;
        double sumYYYY = 0;

        for (int i = 0; i < points.size(); i++) {
            //Gets values for x-xmean and y-ymean
            double xMinusxMean = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale) - xMean;
            double yMinusyMean = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale) - yMean;
            //Adds values
            sumXXYY += (xMinusxMean * yMinusyMean);
            sumXXXX += (xMinusxMean * xMinusxMean);
            sumYYYY += (yMinusyMean * yMinusyMean);
        }

        //Calculates the Pearson correlation coefficient
        double r = sumXXYY / Math.sqrt(sumXXXX * sumYYYY);

        //Calculates rSquared
        rSquared = r*r;
    }

    public void polynomialRegression(){
        //POLYNOMIAL REGRESSION
        // Degree 2 Matrix:
        // [n ΣXi ΣXi^2      ] [a0]   [ΣYi    ]
        // [ΣXi ΣXi^2 ΣXi^3  ] [a1] = [ΣXiYi  ]
        // [ΣXi^2 ΣXi^3 ΣXi^4] [a2]   [ΣXi^2Yi]
        //To solve for higher degrees, extend the matrix by one row (and the left side by one column)

        //Stores a copy of the degree to be used when reprocessing
        oldDeg = deg;

        //Initializes matrix variables
        double n = points.size();
        double sumXi[] = new double[deg * 2];
        double sumYiXi[] = new double[deg + 1];

        for (int i = 0; i < points.size(); i++) {

            //Converts the coordinates of the drawn line to points on the Cartesian plane
            double pointX = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale);
            double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);
            //Adds values
            for (int j = 0; j < sumXi.length; j++) {
                sumXi[j] += pow(pointX, j + 1);
            }

            for (int j = 0; j < sumYiXi.length; j++) {
                sumYiXi[j] += pointY * pow(pointX, j);
            }

        }

        //Initializes matrix
        double[][] matrix = new double[deg + 1][deg + 2];

        //Assigns values to the matrix
        for (int i = 0; i < deg + 1; i++) {
            double[] line = new double[deg + 2];

            //The first value of the first row of the matrix will be n
            if (i == 0) {
                line[0] = n;
                for (int j = 0; j < deg; j++) {
                    line[j + 1] = sumXi[j];
                }
            } else {
                for (int j = 0; j < deg + 1; j++) {
                    line[j] = sumXi[j + i - 1];
                }
            }
            line[line.length - 1] = sumYiXi[i];
            matrix[i] = line;

        }

        //NAIVE GAUSSIAN ELIMINATION
        //Forward elimination

        //Amount of steps is equal to the degree
        int steps = deg;

        //i corresponds to the column of the matrix
        for (int i = 0; i < steps; i++) {
            //The amount of zeros created will be one less each time
            int zeros = steps - i;

            for (int j = 0; j < zeros; j++) {
                //The factor between the first non-zero value of the current line and the first line
                double q = matrix[deg - j][i] / matrix[deg - zeros][i];

                //Subtracts each value in the current line by the first line multiplied by the factor
                for (int k = 0; k < deg + 2; k++) {
                    matrix[deg - j][k] -= matrix[deg - zeros][k] * q;
                }
            }
        }

        //Backwards substitution

        //Stores the coefficients
        A = new double[deg + 1];

        //The for loops acts to isolate for A[n] by subtracting the other terms from y and then dividing by the
        //coefficient of the A being found
        //Loops n times, with n being the amount of coefficients
        for (int i = 0; i < deg + 1; i++) {

            //The line row, going from bottom to top
            int row = deg - i;

            //The last column value of the matrix (y value)
            double num = matrix[row][deg + 1];
            //Initialize the coefficient of the A value that is being found
            double div = 0;

            //Loops for the amount of columns
            for (int j = 0; j < deg + 1; j++) {
                //If the value is the coefficient of the A value, store it in div
                if (matrix[row][j] == matrix[row][row]) {
                    div = matrix[row][j];
                }
                //If the value is not zero, subtract it times the corresponding A value from num
                else if (matrix[row][j] != 0) {
                    num -= matrix[row][j] * A[j];
                }
            }
            //Divide by the coefficient of the A value
            num /= div;

            //Set A to the final value
            A[row] = num;
        }
    }
    public void linearRegression(){
        /*
            Linear Regression Function: y = a + bx
            Slope (b) of Regression line: b = r * Sy/Sx
            Y-Intercept (a) of Regression Line: a = ymean - b*xmean
            r - Pearson correlation coefficient: [Σ((x-xmean)*(y-ymean))]/[sqrt((Σ(x-xmean)^2)*(Σ(y-ymean)^2))]
            Sy - Standard Deviation of y: sqrt([Σ(y-ymean)^2]/[n-1])
            Sx - Standard Deviation of x: sqrt([Σ(x-ymean)^2]/[n-1])
            n: Amount of points
            Σ - Summation
            */

        //FINDS THE MEAN OF X AND Y

        double xMean = 0, yMean = 0;
        for (int i = 0; i < points.size(); i++) {
            //Converts the coordinates of the drawn line to points on the Cartesian plane
            double pointX = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale);
            double pointY = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale);

            xMean += pointX;
            yMean += pointY;
        }
        xMean /= (double) points.size();
        yMean /= (double) points.size();

        //FINDS Σ(x-xmean)*Σ(y-ymean), Σ(x-xmean)^2, Σ(y-ymean)^2 (sumXXYY, sumXXXX, sumYYYY)

        double sumXXYY = 0;
        double sumXXXX = 0;
        double sumYYYY = 0;

        for (int i = 0; i < points.size(); i++) {
            //Gets values for x-xmean and y-ymean
            double xMinusxMean = (points.get(i)[0] - rad) / (scaleSize * 1.0 / scale) - xMean;
            double yMinusyMean = (rad - points.get(i)[1]) / (scaleSize * 1.0 / scale) - yMean;
            //Adds values
            sumXXYY += (xMinusxMean * yMinusyMean);
            sumXXXX += (xMinusxMean * xMinusxMean);
            sumYYYY += (yMinusyMean * yMinusyMean);
        }

        //Calculates the Pearson correlation coefficient
        double r = sumXXYY / Math.sqrt(sumXXXX * sumYYYY);
        //Calculates the standard deviation of x and y
        double Sy = Math.sqrt(sumYYYY / (double) (points.size() - 1));
        double Sx = Math.sqrt(sumXXXX / (double) (points.size() - 1));
        //Calculates the slope
        b = r * (Sy / Sx);
        //Calculates the y-intercept
        a = yMean - (b * xMean);
    }

    //Re-generates the values using the scale, a ,b, c, A[], or oldDeg
    public void reprocess(){
        for (int i = 0; i < rad; i++) {
            double yRightValue = 0;
            double yLeftValue = 0;
            if (mode == Modes.LINEAR) {
                yRightValue = b * (i / (scaleSize * 1.0 / scale)) + a;
                yLeftValue = b * (-i / (scaleSize * 1.0 / scale)) + a;
            }
            else if (mode == Modes.QUADRATIC){
                double xRight = i/(scaleSize*1.0/scale);
                double xLeft = -i/(scaleSize*1.0/scale);
                yRightValue = (a * xRight * xRight) + (b * xRight) + c;
                yLeftValue = (a * xLeft * xLeft) + (b * xLeft) + c;
            }
            else if (mode == Modes.POLYNOMIAL || mode == Modes.AUTO){
                double xRight = i/(scaleSize*1.0/scale);
                double xLeft = -i/(scaleSize*1.0/scale);
                yRightValue = 0;
                yLeftValue = 0;

                for (int j = 0; j < oldDeg+1; j++){
                    yRightValue += A[oldDeg-j]*pow(xRight,oldDeg-j);
                    yLeftValue += A[oldDeg-j]*pow(xLeft,oldDeg-j);
                }
            }
            yRight[i] = convertY(yRightValue);
            yLeft[i] = convertY(yLeftValue);
        }
    }

    //Converts a y value on a cartesian plane to a corresponding y value on the window
    public double convertY(double coord){
        return rad-(coord * (scaleSize*1.0/scale));
    }

    //Returns the power of a number
    public double pow(double base, int exponent){
        double num = 1;

        for (int i = 0; i < exponent;i++){
            num *= base;
        }
        return num;
    }

    //Returns the superscript of a number (from 1 - 20)
    public String superscript(int num){
        switch(num) {
            case 0: return "⁰";
            case 1: return "¹";
            case 2: return "²";
            case 3: return "³";
            case 4: return "⁴";
            case 5: return "⁵";
            case 6: return "⁶";
            case 7: return "⁷";
            case 8: return "⁸";
            case 9: return "⁹";
            case 10: return "¹⁰";
            case 11: return "¹¹";
            case 12: return "¹²";
            case 13: return "¹³";
            case 14: return "¹⁴";
            case 15: return "¹⁵";
            case 16: return "¹⁶";
            case 17: return "¹⁷";
            case 18: return "¹⁸";
            case 19: return "¹⁹";
            case 20: return "²⁰";
        }
        return "";
    }

    //Draws the graphics
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //Draws the axis, ticks, and labels
        drawAxis(g);
        //Draws the user drawn line
        draw.paint(g);

        //Draws the grid
        if (showGrid){
            drawGrid(g);
        }

        //Draws the calculated graph
        if (drawGraph){
            g.setColor(Color.BLACK);
            //Draws connecting lines between points
            for (int i = 1; i < rad; i++){
                g.drawLine(rad+i-1, (int)(yRight[i-1]),rad+i,(int)(yRight[i]));
                g.drawLine(rad-i+1, (int)(yLeft[i-1]),rad-i,(int)(yLeft[i]));
            }
        }
        repaint();


    }
}
