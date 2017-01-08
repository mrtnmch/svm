package cz.martinmach;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("SVM");
        final NumberAxis xAxis = new NumberAxis(0, 10, 1);
        final NumberAxis yAxis = new NumberAxis(-10, 10, 1);
        final ScatterChart<Number,Number> sc = new
                ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("X features");
        yAxis.setLabel("Y features");
        sc.setTitle("SVM");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Positive");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Negative");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Test");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "sample.fxml"));
        Parent root = (Parent) loader.load();
        Controller controller = loader.getController();

        for(RealVector vector : controller.getPositive()) {
            double[] array = vector.toArray();
            series1.getData().add(new XYChart.Data(array[0], array[1]));
        }

        for(RealVector vector : controller.getNegative()) {
            double[] array = vector.toArray();
            series2.getData().add(new XYChart.Data(array[0], array[1]));
        }

        for(RealVector vector : controller.getTest()) {
            double[] array = vector.toArray();
            series3.getData().add(new XYChart.Data(array[0], array[1]));
        }

        sc.getData().addAll(series1, series2, series3);
        Scene scene  = new Scene(sc, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void addToList(ObservableList<XYChart.Data> list, List<RealVector> data) {
        for(RealVector vector : data) {

        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
