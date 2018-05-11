package calculator;

import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;

public class GraphAxes extends Pane {
	/**
	 * Axis x
	 */
	private NumberAxis xAxis;
	/**
	 * Axis y
	 */
    private NumberAxis yAxis;

    /**
     * Constructs axes pane 
     * @param width width of x axis
     * @param height height of y axis
     * @param xLow lowest x value displayed on x axis
     * @param xHi highest x value displayed on x axis
     * @param xTickUnit x axis step
     * @param yLow lowest y value displayed on y axis
     * @param yHi highest y value displayed on y axis
     * @param yTickUnit y axis step
     */
    public GraphAxes(int width, int height,
            double xLow, double xHi, double xTickUnit,
            double yLow, double yHi, double yTickUnit) {
    	
        setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        setPrefSize(width, height);
        setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        xAxis = new NumberAxis(xLow, xHi, xTickUnit);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setMinorTickVisible(false);
        xAxis.setPrefWidth(width);
        xAxis.setLayoutY(height / 2);

        yAxis = new NumberAxis(yLow, yHi, yTickUnit);
        yAxis.setSide(Side.LEFT);
        yAxis.setMinorTickVisible(false);
        yAxis.setPrefHeight(height);
        yAxis.layoutXProperty().bind(
            Bindings.subtract(
                (width / 2) + 1,
                yAxis.widthProperty()
            )
        );

        getChildren().setAll(xAxis, yAxis);
    }

    /**
     * @return Returns yAxis
     */
    public NumberAxis getXAxis() {
        return xAxis;
    }

    /**
     * @return Returns yAxis
     */
    public NumberAxis getYAxis() {
        return yAxis;
    }

}
