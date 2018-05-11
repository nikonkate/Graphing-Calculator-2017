package calculator;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.*;

public class GraphFunction {

	/**
	 * Equation of the function
	 */
	private String equation;
	/**
	 * GraphPlot of this function
	 */
	public GraphPlot graphPlot;
	
	/**
	 * x and y variables of the function in some interval
	 */
	public Map<Double,Double> variables;
	
	/**
	 * Default constructor
	 */
	public GraphFunction() {
		this(null);
	}
	
	/**
	 * Constructor with given equation
	 * @param equation desired function to plot
	 */
	public GraphFunction(String equation) {
		this.equation = equation;
		this.graphPlot = new GraphPlot();
		this.variables = new HashMap<Double,Double>();
	}
	
	/**
	 * Sets the equation for this GraphFunction
	 * @param equation new equation to set
	 */
	public void setEquation(String equation){
		this.equation = equation;
	}
	
	/**
	 * @return Returns the equation
	 */
	public String getEquation() {
		return this.equation;
	}	
}
