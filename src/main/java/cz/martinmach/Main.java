package cz.martinmach;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private ListView<String> traningDataList;
    private LineChart<Number, Number> sc;
    private BorderPane pane;

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
