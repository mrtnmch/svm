package cz.martinmach;

import com.google.gson.Gson;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private SupportVectorMachine svm;
    private LineChart<Number, Number> sc;
    private BorderPane pane;
    private ListView<String> traningDataList;
    private TrainingData trainingData;
    private StackPane stackPane;
    private Button trainButton;
    private TextField precisionText;

    public void setStage(Stage stage) {
        stage.setTitle("SVM");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.sc = new
                LineChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("X features");
        yAxis.setLabel("Y features");
        sc.setTitle("SVM");

        this.pane = this.createPane();
        pane.setCenter(sc);
        pane.setTop(this.addHBox());
        pane.setLeft(this.addVBox());

        this.stackPane = new StackPane();
        this.stackPane.getChildren().add(pane);

        Scene main = new Scene(this.stackPane, 500, 400);
        main.getStylesheets().add("/css/graph.css");
        stage.setScene(main);
        stage.setMaximized(true);
        stage.show();
    }

    public Controller() {
        /*List<RealVector> negative = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{1, 7}));
            add(new ArrayRealVector(new double[]{2, 8}));
            add(new ArrayRealVector(new double[]{3, 8}));
        }};

        List<RealVector> positive = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{5, 1}));
            add(new ArrayRealVector(new double[]{6, -1}));
            add(new ArrayRealVector(new double[]{7, 3}));
        }};

        List<RealVector> test = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{0, 10}));
            add(new ArrayRealVector(new double[]{1, 3}));
            add(new ArrayRealVector(new double[]{3, 4}));
            add(new ArrayRealVector(new double[]{3, 5}));
            add(new ArrayRealVector(new double[]{5, 5}));
            add(new ArrayRealVector(new double[]{5, 6}));
            add(new ArrayRealVector(new double[]{6, -5}));
            add(new ArrayRealVector(new double[]{5, 8}));
        }};*/

        this.svm = new SupportVectorMachine();
       /* svm.train(negative, positive);
        this.supportVectors = svm.getSupportVectors();
        for (RealVector t : test) {
            SupportVectorMachine.Classification classification = svm.classify(t);
            if(classification.equals(SupportVectorMachine.Classification.POSITIVE)) {
                this.testPositive.add(t);
            } else {
                this.testNegative.add(t);
            }
        }*/
    }

    private void plot() throws IOException {
       /* XYChart.Series series1 = new XYChart.Series();
        series1.setName("Positive");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Negative");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Test Positive");

        XYChart.Series series4 = new XYChart.Series();
        series4.setName("Test negative");

        XYChart.Series series5 = new XYChart.Series();
        series5.setName("Vector");

        XYChart.Series series6 = new XYChart.Series();
        series6.setName("Vector+1");

        XYChart.Series series7 = new XYChart.Series();
        series7.setName("Vector-1");

        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        for(RealVector vector : controller.getPositive()) {
            double[] array = vector.toArray();
            series1.getData().add(new XYChart.Data(array[0], array[1]));
        }

        for(RealVector vector : controller.getNegative()) {
            double[] array = vector.toArray();
            series2.getData().add(new XYChart.Data(array[0], array[1]));
        }

        for(RealVector vector : controller.getTestPositive()) {
            double[] array = vector.toArray();
            series3.getData().add(new XYChart.Data(array[0], array[1]));
        }

        for(RealVector vector : controller.getTestNegative()) {
            double[] array = vector.toArray();
            series4.getData().add(new XYChart.Data(array[0], array[1]));
        }

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple = controller.getSupportVectors().get(2);
        series5.getData().add(new XYChart.Data<>(tuple.first.first, tuple.second.first));
        series5.getData().add(new XYChart.Data<>(tuple.first.second, tuple.second.second));

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple0 = controller.getSupportVectors().get(0);
        series6.getData().add(new XYChart.Data<>(tuple0.first.first, tuple0.second.first));
        series6.getData().add(new XYChart.Data<>(tuple0.first.second, tuple0.second.second));

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple1 = controller.getSupportVectors().get(1);
        series7.getData().add(new XYChart.Data<>(tuple1.first.first, tuple1.second.first));
        series7.getData().add(new XYChart.Data<>(tuple1.first.second, tuple1.second.second));

        sc.setAnimated(true);
        sc.setCreateSymbols(true);
        sc.getData().addAll(series1, series2, series3, series4, series5, series6, series7);*/
    }

    private void clearPlot() {
        this.sc.getData().clear();
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

        for(List<Double> array : trainingData.getPositive()) {
            series1.getData().add(new XYChart.Data(array.get(0), array.get(1)));
        }

        for(List<Double> array : trainingData.getNegative()) {
            series2.getData().add(new XYChart.Data(array.get(0), array.get(1)));
        }

        sc.setAnimated(true);
        sc.setCreateSymbols(true);
        sc.getData().addAll(series1, series2);
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
                if(finalFound) {
                    this.plotSvm();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Solution not found");
                    alert.showAndWait();
                }

                this.pane.setDisable(false);
                this.stackPane.getChildren().remove(box);
                this.trainButton.setDisable(true);
            });

        });

        one.start();
    }

    private void plotSvm() {
        List<SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>>> sv = this.svm.getSupportVectors();

        XYChart.Series series5 = new XYChart.Series();
        series5.setName("Vector");

        XYChart.Series series6 = new XYChart.Series();
        series6.setName("Vector+1");

        XYChart.Series series7 = new XYChart.Series();
        series7.setName("Vector-1");

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple = sv.get(2);
        series5.getData().add(new XYChart.Data<>(tuple.first.first, tuple.second.first));
        series5.getData().add(new XYChart.Data<>(tuple.first.second, tuple.second.second));

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple0 = sv.get(0);
        series6.getData().add(new XYChart.Data<>(tuple0.first.first, tuple0.second.first));
        series6.getData().add(new XYChart.Data<>(tuple0.first.second, tuple0.second.second));

        SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>> tuple1 = sv.get(1);
        series7.getData().add(new XYChart.Data<>(tuple1.first.first, tuple1.second.first));
        series7.getData().add(new XYChart.Data<>(tuple1.first.second, tuple1.second.second));

        sc.getData().addAll(series5, series6, series7);
    }

    private List<RealVector> doubleToRealVector(List<List<Double>> list) {
        List<RealVector> ret = new ArrayList<>();

        for (List<Double> arr : list) {
            double[] doubles = new double[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                doubles[i] = arr.get(i);
            }

            ret.add(new ArrayRealVector(doubles));
        }

        return ret;
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        Button buttonCurrent = new Button("Load training data");
        buttonCurrent.setPrefSize(150, 20);
        buttonCurrent.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(null);

            if(file != null) {
                try {
                    this.processFile(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        this.trainButton = new Button("Train");
        this.trainButton.setDisable(true);
        this.trainButton.setOnAction((e) -> {
            this.train(this.trainingData, Integer.parseInt(precisionText.getText()));
        });
        this.trainButton.setPrefSize(100, 20);
        this.precisionText = new TextField("4");
        precisionText.setPrefColumnCount(2);
        Text text = new Text("Steps: ");
        text.setTranslateY(5);
        text.setTranslateX(10);

        hbox.getChildren().addAll(buttonCurrent, this.trainButton, text, precisionText);

        return hbox;
    }

    private void processFile(File file) throws IOException {
        final String EoL = System.getProperty("line.separator");
        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()),
                Charset.defaultCharset());

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(EoL);
        }
        final String content = sb.toString();
        Gson g = new Gson();

        this.trainingData = g.fromJson(content, TrainingData.class);
        this.processTrainingData(trainingData);
    }

    private void processTrainingData(TrainingData trainingData) throws IOException {
        List<List<Double>> positive = trainingData.getPositive();
        List<List<Double>> negative = trainingData.getNegative();

        ObservableList<String> items = FXCollections.observableArrayList();

        for (List<Double> pos : positive) {
            String str = String.format("%f; %f", pos.get(0), pos.get(1));
            items.add(str);
        }

        for (List<Double> neg : negative) {
            String str = String.format("%f; %f", neg.get(0), neg.get(1));
            items.add(str);
        }

        this.traningDataList.setItems(items);
        this.plotTrainingData(trainingData);
        this.trainButton.setDisable(false);
    }

    public VBox addVBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("Training data");
        vbox.getChildren().add(title);

        this.traningDataList = new ListView<String>();

        vbox.getChildren().add(this.traningDataList);

        return vbox;
    }

    private BorderPane createPane() {
        BorderPane pane = new BorderPane();
        return pane;
    }
}
