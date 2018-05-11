package calculator;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GraphPlot extends Pane {
	
	/**
	 * Path of the given function
	 */
	public Path path;
	
	/**
	 * Default constructor
	 */
	public GraphPlot() {}
	
	/**
	 * Constructs new GraphPlot
	 * @param f x and y values of some specific function to plot
	 * @param xMin lowest x value
	 * @param xMax highest x value
	 * @param xInc desired x axis step 
	 * @param axes the axes of the graph
	 * @param color desired color of the plot
	 */
	public GraphPlot(Map<Double, Double> f, Double xMin, Double xMax, 
			Double xInc, GraphAxes axes, Color color) {
		this.path = new Path();
		path.setStroke(color);
        path.setStrokeWidth(2);

        path.setClip(new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight()));

        Double x = xMin;
        Double y = f.get(x);
        
        while(y.isNaN()) {
        	x += xInc;
        	y = f.get(x);
        }

        path.getElements().add(new MoveTo(mapX(x, axes), mapY(y, axes)));
        

        x += xInc;
        
        while (x < xMax) {
        	
            y = f.get(x);
            
            if(!y.isNaN())
            	path.getElements().add(new LineTo(mapX(x, axes), mapY(y, axes)));

            x += xInc;
        }

        setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
        setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        getChildren().setAll(axes, path);
    }

	/**
	 * Converts given x value to new x value using given axes
	 * @param x the x value to convert
	 * @param axes the axes to use 
	 * @return converted x value
	 */
    public static double mapX(double x, GraphAxes axes) {
        double tx = axes.getPrefWidth() / 2;
        double sx = axes.getPrefWidth() / (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());

        return x * sx + tx;
    }

    /**
     * Converts given y value to new y value using given axes
     * @param y the y value to convert
     * @param axes the axes to use
     * @return converted y value
     */
    public static double mapY(double y, GraphAxes axes) {
        double ty = axes.getPrefHeight() / 2;
        double sy = axes.getPrefHeight() / (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());

        return -y * sy + ty;
    }

}
