package cz.martinmach;

import cz.martinmach.svm.Classification;
import cz.martinmach.svm.PointPair;
import cz.martinmach.svm.SolutionNotFoundException;
import cz.martinmach.svm.SupportVectorMachine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final DataFileFactory dataFileFactory;
    private SupportVectorMachine svm;
    private LineChart<Number, Number> sc;
    private BorderPane pane;
    private ListView<String> trainingDataList;
    private TrainingData trainingData;
    private StackPane stackPane;
    private Button trainButton;
    private TextField precisionText;
    private ListView<String> testingDataList;
    private TestingData testingData;
    private Button buttonTesting;

    private static final String TITLE = "SVM";
    private XYChart.Series testPositiveSeries;
    private XYChart.Series testNegativeSeries;

    public void setStage(Stage stage) {
        stage.setTitle(TITLE);

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.sc = new LineChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel("X features");
        yAxis.setLabel("Y features");
        sc.setTitle(TITLE);

        this.pane = this.createPane();
        pane.setCenter(sc);
        pane.setTop(this.createTopPanel());
        pane.setLeft(this.createLeftPanel());

        this.stackPane = new StackPane();
        this.stackPane.getChildren().add(pane);

        Scene main = new Scene(this.stackPane);
        main.getStylesheets().add("/css/graph.css");
        stage.setScene(main);
        stage.setMaximized(true);
        stage.show();

        this.stateInit();
    }

    public Controller() {
        this.dataFileFactory = new DataFileFactory();
        this.svm = new SupportVectorMachine();
    }

    private BorderPane createPane() {
        BorderPane pane = new BorderPane();
        return pane;
    }

    private HBox createLeftPanel() {
        HBox hbox = new HBox();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("Training data");
        this.trainingDataList = new ListView<String>();
        this.trainingDataList.setPrefHeight(910);
        vbox.getChildren().addAll(title, this.trainingDataList);

        VBox vbox2 = new VBox();
        vbox2.setPadding(new Insets(10));
        vbox2.setSpacing(8);

        Text title2 = new Text("Testing data");
        this.testingDataList = new ListView<String>();
        this.testingDataList.setPrefHeight(910);
        vbox2.getChildren().addAll(title2, this.testingDataList);

        hbox.getChildren().addAll(vbox, vbox2);


        return hbox;
    }

    private HBox createTopPanel() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        Button buttonCurrent = new Button("Load training data");
        buttonCurrent.setPrefSize(150, 20);
        buttonCurrent.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                try {
                    this.stateInit();
                    this.loadTrainingFile(file);
                } catch (IOException | UnknownDataFileException e1) {
                    this.showError(e1.getMessage());
                }
            }
        });

        this.trainButton = new Button("Train");
        this.trainButton.setOnAction((e) -> {
            this.train(this.trainingData, Integer.parseInt(precisionText.getText()));
        });
        this.trainButton.setPrefSize(100, 20);
        this.precisionText = new TextField("4");
        precisionText.setPrefColumnCount(2);
        Text text = new Text("Steps: ");
        text.setTranslateY(5);
        text.setTranslateX(10);

        this.buttonTesting = new Button("Load testing data");
        buttonTesting.setPrefSize(150, 20);
        buttonTesting.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                try {
                    this.loadTestingFile(file);
                } catch (IOException | UnknownDataFileException e1) {
                    this.showError(e1.getMessage());
                }
            }
        });

        hbox.getChildren().addAll(buttonCurrent, text, precisionText, this.trainButton, buttonTesting);

        return hbox;
    }

    private void clearPlot() {
        this.testPositiveSeries = new XYChart.Series();
        this.testPositiveSeries.setName("Test Positive");

        this.testNegativeSeries = new XYChart.Series();
        this.testNegativeSeries.setName("Test Negative");

        this.sc.getData().clear();
    }

    private void loadTestingFile(File file) throws IOException, UnknownDataFileException {
        this.testingData = this.dataFileFactory.loadTestingData(file);
        this.processTestingData(testingData);
    }

    private void loadTrainingFile(File file) throws IOException, UnknownDataFileException {
        this.trainingData = this.dataFileFactory.loadTrainingData(file);
        this.processTrainingData(trainingData);
    }

    private void processTrainingData(TrainingData trainingData) throws IOException {
        List<List<Double>> positive = trainingData.getPositive();
        List<List<Double>> negative = trainingData.getNegative();

        ObservableList<String> items = FXCollections.observableArrayList();

        for (List<Double> pos : positive) {
            String str = String.format("+\t%f; %f", pos.get(0), pos.get(1));
            items.add(str);
        }

        for (List<Double> neg : negative) {
            String str = String.format("-\t%f; %f", neg.get(0), neg.get(1));
            items.add(str);
        }

        this.trainingDataList.setItems(items);
        this.plotTrainingData(trainingData);
        this.stateTrainingLoaded();
    }

    private void plotTrainingData(TrainingData trainingData) throws IOException {
        this.sc.setAnimated(false);
        this.sc.setCreateSymbols(false);
        this.sc.getData().clear();

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Positive");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Negative");

        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        for (List<Double> array : trainingData.getPositive()) {
            series1.getData().add(new XYChart.Data(array.get(0), array.get(1)));
        }

        for (List<Double> array : trainingData.getNegative()) {
            series2.getData().add(new XYChart.Data(array.get(0), array.get(1)));
        }

        sc.setAnimated(true);
        sc.setCreateSymbols(true);
        sc.getData().addAll(series1, series2);
    }


    private void processTestingData(TestingData testingData) throws IOException {
        List<List<Double>> test = testingData.getTest();
        ObservableList<String> items = FXCollections.observableArrayList();

        if(!this.sc.getData().contains(this.testPositiveSeries)) {
            sc.getData().addAll(this.testPositiveSeries, this.testNegativeSeries);
        }

        for (List<Double> pos : test) {
            boolean positive = this.plotTestingData(pos);
            String str = String.format("%s\t%f; %f", positive ? "+" : "-", pos.get(0), pos.get(1));
            items.add(str);
        }

        this.testingDataList.setItems(items);
    }

    private boolean plotTestingData(List<Double> data) throws IOException {
        RealVector t = this.singleDoubleToRealVector(data);
        Classification classification = svm.classify(t);

        if (classification.equals(Classification.POSITIVE)) {
            this.testPositiveSeries.getData().add(new XYChart.Data(t.toArray()[0], t.toArray()[1]));
            return true;
        } else {
            this.testNegativeSeries.getData().add(new XYChart.Data(t.toArray()[0], t.toArray()[1]));
            return false;
        }
    }


    private void train(TrainingData trainingData, int precision) {
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);
        // Grey Background
        this.pane.setDisable(true);
        this.stackPane.getChildren().add(box);

        Thread one = new Thread(() -> {
            boolean found = false;
            List<RealVector> negative = this.doubleToRealVector(trainingData.getNegative());
            List<RealVector> positive = this.doubleToRealVector(trainingData.getPositive());
            try {
                this.svm.train(negative, positive, precision);
                found = true;
            } catch (SolutionNotFoundException e) {
            }

            boolean finalFound = found;
            Platform.runLater(() -> {
                if (finalFound) {
                    this.plotSvm();
                    this.stateTrained();
                } else {
                    this.showError("Solution not found");
                    this.stateTrainingLoaded();
                }

                this.pane.setDisable(false);
                this.stackPane.getChildren().remove(box);
            });

        });

        one.start();
    }

    private void plotSvm() {
        PointPair mainVector = this.svm.getMainVector();
        PointPair positiveVector = this.svm.getPositiveVector();
        PointPair negativeVector = this.svm.getNegativeVector();

        XYChart.Series series5 = new XYChart.Series();
        series5.setName("Vector");

        XYChart.Series series6 = new XYChart.Series();
        series6.setName("Vector+1");

        XYChart.Series series7 = new XYChart.Series();
        series7.setName("Vector-1");

        series5.getData().add(new XYChart.Data<>(mainVector.getFromX(), mainVector.getFromY()));
        series5.getData().add(new XYChart.Data<>(mainVector.getToX(), mainVector.getToY()));

        series6.getData().add(new XYChart.Data<>(positiveVector.getFromX(), positiveVector.getFromY()));
        series6.getData().add(new XYChart.Data<>(positiveVector.getToX(), positiveVector.getToY()));

        series7.getData().add(new XYChart.Data<>(negativeVector.getFromX(), negativeVector.getFromY()));
        series7.getData().add(new XYChart.Data<>(negativeVector.getToX(), negativeVector.getToY()));

        sc.getData().addAll(series5, series6, series7);
    }

    private List<RealVector> doubleToRealVector(List<List<Double>> list) {
        List<RealVector> ret = new ArrayList<>();

        for (List<Double> arr : list) {
            ret.add(this.singleDoubleToRealVector(arr));
        }

        return ret;
    }

    private RealVector singleDoubleToRealVector(List<Double> list) {
        double[] doubles = new double[list.size()];

        for (int i = 0; i < list.size(); i++) {
            doubles[i] = list.get(i);
        }

        return new ArrayRealVector(doubles);

    }

    private void stateInit() {
        this.trainButton.setDisable(true);
        this.buttonTesting.setDisable(true);

        this.clearPlot();
        this.testingDataList.getItems().clear();
        this.trainingDataList.getItems().clear();
    }

    private void stateTrainingLoaded() {
        this.trainButton.setDisable(false);
        this.buttonTesting.setDisable(true);
    }

    private void stateTrained() {
        this.trainButton.setDisable(true);
        this.buttonTesting.setDisable(false);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
