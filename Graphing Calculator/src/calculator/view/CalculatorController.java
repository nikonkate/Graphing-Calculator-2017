package calculator.view;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import calculator.GraphAxes;
import calculator.GraphFunction;
import calculator.GraphPlot;
import calculator.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;


public class CalculatorController {
	
	/**
	 * The desired Graph step for a plot, 
	 * the plot for a function will be calculated
	 * at each (x + GRAPH_STEP) within given interval 
	 */
	private final double GRAPH_STEP = 0.1;
	
	/**
	 * The desired Intersection area
	 * the intersects function will explore more closely
	 * once the distance between f(x) and g(x) is less 
	 * than INTERSECTION AREA
	 */
	private final double INTERSECTION_AREA = 0.08;
	
	/**
	 * The desired Intersection step
	 * the intersects function will evaluate f(x) and g(x)
	 * where x is between given boundaries and is increased
	 * each iteration with INTERSECTION STEP
	 */
	private final double INTERSECTION_STEP = 0.0001;
	/**
	 * First graph
	 */
	private GraphFunction graphF1 = new GraphFunction();
	/**
	 * Second graph
	 */
	private GraphFunction graphF2 = new GraphFunction();
	/**
	 * Third graph
	 */
	private GraphFunction graphF3 = new GraphFunction();
	/**
	 * The axes of graph
	 */
	private GraphAxes axes;
	/**
	 * The intersection points that are currently displayed
	 */
	private ArrayList<Circle> intersectionPoints = new ArrayList<Circle>();
	/**
	 * Reference to main
	 */
	private Main main;
	
	/**
	 * Graphing calculator buttons and UI elements
	 */
	@FXML private Button reloadButton;
	@FXML private Button calculateButton;
	@FXML private AnchorPane graphScene;
	@FXML private TextField equation1;
	@FXML private TextField equation2;
	@FXML
	private TextField equation3;
	@FXML
	private ColorPicker color1;
	@FXML
	private ColorPicker color2;
	@FXML
	private ColorPicker color3;
	@FXML
	private CheckBox checkbox1;
	@FXML
	private CheckBox checkbox2;
	@FXML
	private CheckBox checkbox3;
	@FXML
	private ChoiceBox<String> choiceBoxMinMax;
	@FXML
	private ChoiceBox<String> choiceBoxEq1;
	@FXML
	private ChoiceBox<String> choiceBoxEqAUC;
	@FXML
	private TextField textFieldIntervalFrom;
	@FXML
	private TextField textFieldIntervalTo;
	@FXML
	private TextField textFieldIntervalSlopeFrom;
	@FXML
	private TextField textFieldIntervalSlopeTo;
	@FXML
	private ChoiceBox<String> choiceBoxEqSlope;
	@FXML
	private ChoiceBox<String> choiceBoxEqIntersection1;
	@FXML
	private ChoiceBox<String> choiceBoxEqIntersection2;
	@FXML
	private Label computedValue1;
	@FXML
	private Label computedValue2;
	@FXML
	private Label computedValue3;
	@FXML
	private CheckBox showIntersections;
	
	/**
	 *  Scientific calculator buttons and UI elements
	 */
	@FXML private Button zero;
	@FXML private Button one;
	@FXML private Button two;
	@FXML private Button three;
	@FXML private Button four;
	@FXML private Button five;
	@FXML private Button six;
	@FXML private Button seven;
	@FXML private Button eight;
	@FXML private Button nine;
	
	@FXML private Button e;
	@FXML private Button pi;
	@FXML private Button procent;
	@FXML private Button rightBrk;
	@FXML private Button leftBrk;
	@FXML private Button sin;
	@FXML private Button cos;
	@FXML private Button tan;
	@FXML private Button asin;
	@FXML private Button acos;
	@FXML private Button atan;
	@FXML private Button sinh;
	@FXML private Button cosh;
	@FXML private Button tanh;
	@FXML private Button asinh;
	@FXML private Button acosh;
	@FXML private Button atanh;
	@FXML private Button div;
	@FXML private Button mult;
	@FXML private Button minus;
	@FXML private Button plus;
	@FXML private Button x;
	@FXML private Button dot;
	@FXML private Button log10;
	@FXML private Button log2;
	@FXML private Button hat;
	@FXML private Button sq2;
	@FXML private Button sq3;
	@FXML private Button factorial;
	@FXML private Button sqrt;
	@FXML private Button sqrt3;
	@FXML private Button sqrtY;
	@FXML private Button logY;
	@FXML private Button ln;
	
	@FXML private ChoiceBox<String> precision;
	@FXML private Button clear;
	@FXML private Button deleteOne;
	@FXML private Button equals;
	@FXML private TextField valueX;
	
	@FXML private TextField calculatorField;
	
	/**
	 * Functional interface for eval function
	 */
	@FunctionalInterface
	interface Expression {
	    double eval();
	    
	}
	
	
	/**
	 * Parses through the given string and evaluates it
	 * Rules:
	 * If expression then: expression `+` term, expression `-` term
	 * If term then: term `*` factor, term `/` factor
	 * If factor then: `+` factor , `-` factor , `(` expression `)`,
	 * 				    factor `^` factor
	 * @param str mathematical equation to evaluate
	 * @return Returns the result of evaluated equation
	 */
	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean currentOp(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) {
	            	Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error");
	                alert.setHeaderText("Something went wrong");
	                alert.setResizable(false);
	                alert.setContentText("Unexpected : " + (char)ch);
	                alert.showAndWait();
	            	throw new RuntimeException("Unexpected: " + (char)ch);
	            }
	            return x;
	        }

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if(currentOp('+')) 
	                	x += parseTerm(); // addition
	                else if(currentOp('-')) 
	                	x -= parseTerm(); // subtraction
	                else 
	                	return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if(currentOp('*')) 
	                	x *= parseFactor(); // multiplication
	                else if(currentOp('/')) 
	                	x /= parseFactor(); // division
	                else return x;
	            }
	        }
	        

	        double parseFactor() {
	            if(currentOp('+')) 
	            	return parseFactor(); // unary plus
	            if(currentOp('-')) 
	            	return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (currentOp('(')) { // parentheses
	                x = parseExpression();
	                currentOp(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	            	
	                while ((ch >= '0' && ch <= '9') || ch == '.') 
	                	nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	                
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	            	
	                while (ch >= 'a' && ch <= 'z')
	                	nextChar();
	                
	                String func = str.substring(startPos, this.pos);
	                int base = 10; //default
	                if (func.equals("log")) {
	                	// read base
	                	int baseStartPos = this.pos;
	                	while ((ch >= '0' && ch <= '9')) 
		                	nextChar();
	                	
	                	base = Integer.parseInt(str.substring(baseStartPos, this.pos));
	                }
	                x = parseFactor();
	                if (func.equals("sqrt")) 
	                	x = Math.sqrt(x);
	                else if (func.equals("sin")) 
	                	x = Math.sin(x); //x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) 
	                	x = Math.cos(x);
	                else if (func.equals("tan")) 
	                	x = Math.tan(x);
	                else if (func.equals("atan"))
	                	x = Math.atan(x);
	                else if (func.equals("acos"))
	                	x = Math.acos(x);
	                else if (func.equals("asin"))
	                	x = Math.asin(x);
	                else if (func.equals("log")) 
	                	x = Math.log(x) / Math.log(base);
	                else if (func.equals("sinh"))
	                	x = Math.sinh(x);
	                else if (func.equals("cosh"))
	                	x = Math.cosh(x);
	                else if (func.equals("tanh"))
	                	x = Math.tanh(x);
	                else if (func.equals("asinh"))
	                	x = Math.log(x + Math.sqrt(x*x + 1.0));
	                else if (func.equals("acosh"))
	                	x = Math.log(x + Math.sqrt(x*x - 1.0)); 
	                else if (func.equals("atanh"))
	                	x = 0.5*Math.log((x + 1.0) / (x - 1.0)); 
	                else if (func.equals("ln"))
	                	x = Math.log(x);
	                else {
	                	Alert alert = new Alert(AlertType.ERROR);
		                alert.setTitle("Error");
		                alert.setHeaderText("Something went wrong");
		                alert.setResizable(false);
		                alert.setContentText("Unknown function: " + func);
		                alert.showAndWait();
		                throw new RuntimeException("Unknown function: " + func);
	                }
	                
	            } else {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error");
	                alert.setHeaderText("Something went wrong");
	                alert.setResizable(false);
	                alert.setContentText("Unexpected character: " + (char)ch);
	                alert.showAndWait();
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if(currentOp('^')) 
	            	x = Math.pow(x, parseFactor()); // exponentiation
	            
	            if(currentOp('!'))
	            	x = factorial(x);
	            if(currentOp('%'))
	            	x = x / 100;

	            return x;
	        }
	        
	    }.parse();
	}
	
	/**
	 * Calculates the factorial using recursion
	 * @param number number to find the factorial of
	 * @return factorial of number
	 */
	private static double factorial(double number) {
	      if (number <= 1) 
	    	  return 1;
	      else 
	    	  return number * factorial(round(number - 1,2));
	}

	/**
	 * Default constructor is called before the initialize method
	 */
	public CalculatorController() {}
	
	/**
	 * Initializes the controller, called after the fxml file has been loaded
	 */
	@FXML
	private void initialize() {
		//Default equation
		equation1.setText("sin(2*x)");
		equation2.setText("0.5*log10(x)");
		checkbox1.setSelected(true);
		checkbox2.setSelected(true);
		showIntersections.setSelected(true);
		color1.setValue(Color.BLUE);
		color2.setValue(Color.GREEN);
		
		// Set choice boxes
		choiceBoxMinMax.setItems(FXCollections.observableArrayList("Min", "Max"));
		choiceBoxEq1.setItems(FXCollections.observableArrayList("y1", "y2", "y3"));
		textFieldIntervalFrom.setText("0");
		textFieldIntervalTo.setText("1");
		choiceBoxEqAUC.setItems(FXCollections.observableArrayList("y1", "y2", "y3"));
		choiceBoxEqSlope.setItems(FXCollections.observableArrayList("y1", "y2", "y3"));
		choiceBoxEqIntersection1.setItems(FXCollections.observableArrayList("y1", "y2", "y3"));
		choiceBoxEqIntersection2.setItems(FXCollections.observableArrayList("y1", "y2", "y3"));
		textFieldIntervalSlopeFrom.setText("0");
		textFieldIntervalSlopeTo.setText("1");
		precision.setItems(FXCollections.observableArrayList("1", "2", "3","4","5","6","7","8","9","No rounding"));
		
		// Set default selection
		choiceBoxMinMax.getSelectionModel().selectFirst();
		choiceBoxEq1.getSelectionModel().selectFirst();
		choiceBoxEqAUC.getSelectionModel().selectFirst();
		choiceBoxEqSlope.getSelectionModel().selectFirst();
		choiceBoxEqIntersection1.getSelectionModel().selectFirst();
		choiceBoxEqIntersection2.getSelectionModel().select(1);
		precision.getSelectionModel().selectFirst();
		
		// Add event handlers for Scientific Calculator
		zero.setOnAction(e -> numpadBtnPressed(zero.getText()));
		one.setOnAction(e -> numpadBtnPressed(one.getText()));
        two.setOnAction(e -> numpadBtnPressed(two.getText()));
        three.setOnAction(e -> numpadBtnPressed(three.getText()));
        four.setOnAction(e -> numpadBtnPressed(four.getText()));
        five.setOnAction(e -> numpadBtnPressed(five.getText()));
        six.setOnAction(e -> numpadBtnPressed(six.getText()));
        seven.setOnAction(e -> numpadBtnPressed(seven.getText()));
        eight.setOnAction(e -> numpadBtnPressed(eight.getText()));
        nine.setOnAction(e -> numpadBtnPressed(nine.getText()));
        mult.setOnAction(e -> numpadBtnPressed(mult.getText()));
        plus.setOnAction(e -> numpadBtnPressed(plus.getText()));
        minus.setOnAction(e -> numpadBtnPressed(minus.getText()));
        div.setOnAction(e -> numpadBtnPressed(div.getText()));
        
        sin.setOnAction(e -> numpadBtnPressed(sin.getText()));
        cos.setOnAction(e -> numpadBtnPressed(cos.getText()));
        tan.setOnAction(e -> numpadBtnPressed(tan.getText()));
        asin.setOnAction(e -> numpadBtnPressed(asin.getText()));
        acos.setOnAction(e -> numpadBtnPressed(acos.getText()));
        atan.setOnAction(e -> numpadBtnPressed(atan.getText()));
        sinh.setOnAction(e -> numpadBtnPressed(sinh.getText()));
        cosh.setOnAction(e -> numpadBtnPressed(cosh.getText()));
        tanh.setOnAction(e -> numpadBtnPressed(tanh.getText()));
        asinh.setOnAction(e -> numpadBtnPressed(asinh.getText()));
        acosh.setOnAction(e -> numpadBtnPressed(acosh.getText()));
        atanh.setOnAction(e -> numpadBtnPressed(atanh.getText()));
        dot.setOnAction(e -> numpadBtnPressed(dot.getText()));
        leftBrk.setOnAction(e -> numpadBtnPressed(leftBrk.getText()));
        rightBrk.setOnAction(e -> numpadBtnPressed(rightBrk.getText()));
        hat.setOnAction(e -> numpadBtnPressed(hat.getText()));
        log10.setOnAction(e -> numpadBtnPressed("log10("));
        log2.setOnAction(e -> numpadBtnPressed("log2("));
        pi.setOnAction(e -> numpadBtnPressed("pi"));
        e.setOnAction(e -> numpadBtnPressed("e"));
        sq2.setOnAction(e -> numpadBtnPressed("^2"));
        sq3.setOnAction(e -> numpadBtnPressed("^3"));
        x.setOnAction(e -> numpadBtnPressed("x"));
        factorial.setOnAction(e -> numpadBtnPressed("!"));
        sqrt.setOnAction(e -> numpadBtnPressed("sqrt("));
    	sqrt3.setOnAction(e -> numpadBtnPressed("3*sqrt("));
    	sqrtY.setOnAction(e -> numpadBtnPressed("*sqrt"));
    	logY.setOnAction(e -> numpadBtnPressed("log"));
    	procent.setOnAction(e -> numpadBtnPressed("%"));
    	ln.setOnAction(e -> numpadBtnPressed("ln("));
    	
        calculatorField.setEditable(true); //allow custom user input
        
		axes = new GraphAxes(400,300,-8,8,1,-6,6,1);
		graphScene.getChildren().add(axes); //add axes
		Map<Double,Double> variables1 = new HashMap<>();
		Map<Double,Double> variables2 = new HashMap<>();
		variables1= evalString(equation1.getText());
		variables2 = evalString(equation2.getText());
		
		graphF1.setEquation(equation1.getText());
		graphF1.variables = variables1;
		GraphPlot grp = new GraphPlot(variables1, -8.0, 8.0, GRAPH_STEP, axes, color1.getValue());
		graphF1.graphPlot = grp;
		
		
		graphF2.setEquation(equation2.getText());
		graphF2.variables = variables2;
		GraphPlot grp2 = new GraphPlot(variables2, -8.0, 8.0, GRAPH_STEP, axes, color2.getValue());
		graphF2.graphPlot = grp2;
		
		graphScene.getChildren().add(grp); // plot	
		graphScene.getChildren().add(grp2); // plot	
		findIntersection(choiceBoxEqIntersection1.getValue(), choiceBoxEqIntersection2.getValue());
		updateCalculations();
	}
	
	
	/**
	 * Called by the main application to give reference to itself
	 * @param main instance of main
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
	/**
	 * Reloads the Graph Pane once reload button is pressed
	 */
	public void reloadButtonPressed() {
		// Reload graph pane
		updateGraphScene();
	}
	
	/**
	 * Reloads the calculation fields once the button is pressed
	 */
	public void calculateButtonPressed() {
		updateCalculations();
		if(showIntersections.isSelected()) {
			graphScene.getChildren().removeAll(intersectionPoints);
			findIntersection(choiceBoxEqIntersection1.getValue(), choiceBoxEqIntersection2.getValue());
		}
		else
			graphScene.getChildren().removeAll(intersectionPoints);
	}
	
	/**
	 * Shows/Hides the intersection points once checkbox is changed
	 */
	public void showIntersectionButtonPressed() {
		if(showIntersections.isSelected())
			findIntersection(choiceBoxEqIntersection1.getValue(), choiceBoxEqIntersection2.getValue());
		else
			graphScene.getChildren().removeAll(intersectionPoints);
	}

	/**
	 * Adds new text to calculator field once numpad button is pressed
	 * @param val new string to add
	 */
	public void numpadBtnPressed(String val) {
		String oldV = calculatorField.getText();
		calculatorField.setText(oldV + val);
	}
	
	/**
	 * Clears the calculator field once the button is pressed
	 */
	public void clearButtonPressed() {
		calculatorField.setText("");
	}
	
	/**
	 * Evaluates the expression in calculator field once 
	 * the button is pressed
	 */
	public void enterButtonPressed() {
		String newString = calculatorField.getText().replaceAll("x", valueX.getText());
		newString = newString.replaceAll("pi", new BigDecimal(Math.PI).toPlainString());
		newString = newString.replaceAll("e", new BigDecimal(Math.E).toPlainString());
		Double res = eval(newString);
		try {
			if(precision.getValue().equalsIgnoreCase("No rounding"))
				calculatorField.setText(new BigDecimal(res).toPlainString());
			else
				calculatorField.setText(Double.toString(round(res,Integer.parseInt(precision.getValue()))));
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Something went wrong");
            alert.setResizable(false);
            alert.setContentText("Critical error, make sure your expression is mathematically correct");
            alert.showAndWait();
		}
	}
	
	/**
	 * Deletes a single char from calculator field
	 */
	public void deleteButtonPressed() {
		if(!calculatorField.getText().equals("") && calculatorField.getText() != null) {
			String newString = calculatorField.getText().substring(0, calculatorField.getText().length()-1);
			calculatorField.setText(newString);
		}
	}
	
	/**
	 * Updates graph pane
	 */
	private void updateGraphScene() {
		graphScene.getChildren().clear();
		reloadGraphs();
	}
	
	/**
	 * Reevaluates functions and plots them
	 */
	private void reloadGraphs() {
		
		String eq1 = equation1.getText();
		String eq2 = equation2.getText();
		String eq3 = equation3.getText();
		axes = new GraphAxes(400,300,-8,8,1,-6,6,1);
		
		if(eq1 != null && !eq1.equals("") && checkbox1.isSelected()) {
			Map<Double,Double> variables = evalString(eq1);
			graphF1.setEquation(eq1);
			graphF1.variables = variables;
			GraphPlot ghP = new GraphPlot(variables, -8.0, 8.0, GRAPH_STEP,axes, color1.getValue());
			graphF1.graphPlot = ghP;
			graphScene.getChildren().add(ghP);
		}
		
		if(eq2 != null && !eq2.equals("") && checkbox2.isSelected()) {
			Map<Double,Double> variables = evalString(eq2);
			graphF2.setEquation(eq2);
			graphF2.variables = variables;
			GraphPlot ghP = new GraphPlot(variables, -8.0, 8.0, GRAPH_STEP,axes, color2.getValue());
			graphF2.graphPlot = ghP;
			graphScene.getChildren().add(ghP);
		}
		
		if(eq3 != null && !eq3.equals("") && checkbox3.isSelected()) {
			Map<Double,Double> variables = evalString(eq3);
			graphF3.setEquation(eq3);
			graphF3.variables = variables;
			GraphPlot ghP = new GraphPlot(variables, -8.0, 8.0, GRAPH_STEP,axes, color3.getValue());
			graphF3.graphPlot = ghP;
			graphScene.getChildren().add(ghP);
		}
	}
	
	/**
	 * Updates the calculations such as Min/Max, AUC, Slope and Intersections
	 */
	private void updateCalculations() {
		computedValue1.setText("is: " + findMinMax(choiceBoxMinMax.getValue(), choiceBoxEq1.getValue()));
		computedValue2.setText("is: " + findAUC(textFieldIntervalFrom.getText(), textFieldIntervalTo.getText(), choiceBoxEqAUC.getValue()));
		computedValue3.setText("is: " + findSlope(textFieldIntervalSlopeFrom.getText(), textFieldIntervalSlopeTo.getText(),choiceBoxEqSlope.getValue()));
		if(showIntersections.isSelected())
			findIntersection(choiceBoxEqIntersection1.getValue(), choiceBoxEqIntersection2.getValue());
	}
	
	/**
	 * Finds minimum or maximum of the given function on certain interval
	 * @param minORmax min or max
	 * @param function given function
	 * @return Returns minimum or maximum of the given function
	 */
	private String findMinMax(String minORmax, String function) {
		String result = "";
		
		GraphFunction f = getGraphFunction(function) ;
		
		if(minORmax.equalsIgnoreCase("min")) {
			result = findMin(f);
		}
		else if(minORmax.equalsIgnoreCase("max")) {
			result = findMax(f);
		}
		
		return result;
	}
	
	/**
	 * Find Area Under a Curve for given function and given interval
	 * @param from start x
	 * @param to end x
	 * @param function chosen function
	 * @return Returns Area Under a Curve
	 */
	private String findAUC(String from, String to, String function) {
		String result = "";
		GraphFunction f = getGraphFunction(function) ;
		
		Double res = integrate(Double.parseDouble(from), Double.parseDouble(to), f);
		result = Double.toString(round(res,4));
		
		//System.out.println(result);
		return result;
	}
	
	/**
	 * Finds a slope of the given function on the given interval
	 * using formula slope = (x2 - x1)/(y2 - y1)
	 * @param from start x
	 * @param to end x
	 * @param function chosen function
	 * @return Returns slope of the function on given interval in form x/y
	 */
	private String findSlope(String from, String to, String function) {
		String result = "";
		GraphFunction f = getGraphFunction(function) ;
		
		String newEquationA = f.getEquation().replaceAll("x", String.valueOf(from));
	    Double aY = eval(newEquationA);
	    
	    String newEquationB = f.getEquation().replaceAll("x", String.valueOf(to));
	    Double bY = eval(newEquationB);
	    
	    Double aX = Double.parseDouble(from);
	    Double bX = Double.parseDouble(to);
	    
	    Double slopeX = bX - aX;
	    Double slopeY = bY - aY;
	    
	    result = slopeX.intValue() + "/" + slopeY.intValue();
		return result;
	}
	
	/**
	 * Finds the intersection points between f1 and f2
	 * If y1 is less than y2 + INTERSECTION AREA and 
	 * y1 is greater than y2 - INTERSECTION AREA then
	 * it will explore it more closely at around specific x value
	 * until it finds(or not) the y value where functions intersect
	 * @param function1 equation of f1
	 * @param function2 equation of f2
	 */
	private void findIntersection(String function1, String function2) {
		String result = "";
		
		GraphFunction f1 = getGraphFunction(function1);
		GraphFunction f2 = getGraphFunction(function2);
		
		if(f1.getEquation().equals(f2.getEquation())) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Something went wrong");
            alert.setResizable(false);
            alert.setContentText("Can't find intersection of same function");
            alert.showAndWait();
            return; // do not allow the intersection of same function
		}
			
		
		for(Double key : f1.variables.keySet()) {
			Double f1Y = f1.variables.get(key);
			Double f2Y = f2.variables.get(key);
			
			/* 
			 * If two functions are very close to each other, 
			 * then evaluate them around this x position
			 */
			if(f1Y <= f2Y + INTERSECTION_AREA && f1Y >= f2Y - INTERSECTION_AREA ) {
				if(intersects(f1,f2, key)) {
					Circle crc = new Circle(2.0);
					crc.setLayoutX(GraphPlot.mapX(key, this.axes));
					crc.setLayoutY(GraphPlot.mapY(f2Y, axes));
					crc.setFill(Color.RED);
					intersectionPoints.add(crc);
					graphScene.getChildren().add(crc);
				}
			}
		}
		return;
	}
	
	/**
	 * Evaluates f1 and f2 from floor(x) to ceil(x)
	 * if y1 and y2 are equal to each other with precision of 3
	 * decimals after a point then we consider it as an intersection
	 * INTERSECTION STEP could be increased for more precision
	 * @param f1 GraphFunction of f1
	 * @param f2 GraphFunction of f2
	 * @param poi Point of interest the point around which we should explore
	 * @return Returns true if f1 and f2 intersect with precision of 3 decimal values after a point otw returns false
	 */
	private boolean intersects(GraphFunction f1, GraphFunction f2, Double poi) {
		int ceil = (int) Math.ceil(poi);
		int floor = (int) Math.floor(poi);
		for(double xi = floor; xi <= ceil + INTERSECTION_STEP; xi += INTERSECTION_STEP){
			String newEquationA = f1.getEquation().replaceAll("x", "(" + new BigDecimal(xi).toPlainString() + ")");
		    Double f1Y = eval(newEquationA);
		    
		    String newEquationB = f2.getEquation().replaceAll("x", "(" + new BigDecimal(xi).toPlainString() + ")");
		    Double f2Y = eval(newEquationB);
		    
		    if(round(f1Y, 3) == round(f2Y, 3))
		    	return true;
			
		}
		
		return false;
	}
	
	/**
	 * Rounds the given double to n decimal points
	 * using Half Up rounding mode
	 * @param value double to round
	 * @param n decimal points
	 * @return Returns rounded double
	 */
	private static double round(double value, int n) {
		try {
			if (n < 0) {
		    	Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Something went wrong");
	            alert.setResizable(false);
	            alert.setContentText("Unexpected error in rounding: " + n);
	            alert.showAndWait();
		    	throw new IllegalArgumentException();
		    }

		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(n, RoundingMode.HALF_UP);
		    
		    return bd.doubleValue();
		    
		} catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Something went wrong");
	        alert.setResizable(false);
	        alert.setContentText("Critical error, make sure your expression is mathematically correct and/or requested interval is defined in f(x)");
	        alert.showAndWait();
	        return -1.0;
		}

	}
	
	/**
	 * Gets Graph Function for the given string
	 * @param function name of the function
	 * @return Graph Function for the given string
	 */
	private GraphFunction getGraphFunction(String function) {
		if(function.equals("y1"))
			return graphF1;
		else if(function.equals("y2"))
			return graphF2;
		else
			return graphF3;
	}
	
	/**
	 * Finds minimum for the given graph
	 * @param f graph
	 * @return Returns minimum of the graph
	 */
	private String findMin(GraphFunction f) {
		String result = "";
		Double currentMin = Double.MAX_VALUE;
		
		for(Double key : f.variables.keySet()) {
			if(f.variables.get(key) < currentMin)
				currentMin = f.variables.get(key);
		}
		
		result =  Double.toString(round(currentMin,4));
		
		return result;
	}
	
	/**
	 * Finds maximum for the given graph
	 * @param f graph
	 * @return Returns maximum of the graph
	 */
	private String findMax(GraphFunction f) {
		String result = "";
		Double currentMax = Double.MIN_VALUE;
		
		for(Double key : f.variables.keySet()) {
			if(f.variables.get(key) > currentMax)
				currentMax = f.variables.get(key);
		}
		
		result = Double.toString(round(currentMax,4));
		
		return result;
	}
	

	/**
	 * Integrates graph from a to b using Simpson's rule.
	 * N can be increased for more precision.
	 * @param a lower boundary
	 * @param b upper boundary
	 * @param graphF graph
	 * @return Returns result of integration
	 */
	public static double integrate(double a, double b, GraphFunction graphF) {
		   
	      int N = 10000;                    // precision parameter
	      double h = (b - a) / (N - 1);     // step size
	 
	      String newEquationA = graphF.getEquation().replaceAll("x", "(" + new BigDecimal(a).toPlainString() + ")");
	      Double resA = eval(newEquationA);
	      
	      String newEquationB = graphF.getEquation().replaceAll("x", "(" + new BigDecimal(b).toPlainString() + ")");
	      Double resB = eval(newEquationB);
	      
	      // 1/3 terms
	      double sum = 1.0 / 3.0 * (resA + resB);

	      // 4/3 terms
	      for (int i = 1; i < N - 1; i += 2) {
	    	 BigDecimal x = new BigDecimal(a + h * i);
	         String newEquationC = graphF.getEquation().replaceAll("x", "(" + x.toPlainString() + ")");
		     Double resC = eval(newEquationC);
		     
	         sum += 4.0 / 3.0 * resC;
	      }

	      // 2/3 terms
	      for (int i = 2; i < N - 1; i += 2) {
	         BigDecimal x = new BigDecimal(a + h * i);
	         String newEquationC = graphF.getEquation().replaceAll("x", "(" + x.toPlainString() + ")");
		     Double resC = eval(newEquationC);
	         sum += 2.0 / 3.0 * resC;
	      }
	      
	      return sum * h;
	}

	
	/**
	 * Evaluates given string as mathematical expression
	 * by replacing x with value between axis lower x
	 * and axis upper x
	 * @param eq string to evaluate
	 * @return Returns x and y values of the given function
	 */
	private Map<Double,Double> evalString(String eq) {

		Map<Double,Double> variables = new HashMap<>();
		for(Double i = -8.0; i < 9.0; i += GRAPH_STEP) {
			String newEquation = eq.replaceAll("x",  "(" + new BigDecimal(i).toPlainString() + ")");
			Double res = eval(newEquation);
			variables.put(i, res);
		}
		return variables;
	}
}
