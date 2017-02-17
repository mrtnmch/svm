package cz.martinmach;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmx on 17.2.17.
 */
public class CsvDataFileInterpreter implements DataFileInterpreter {

    private static final String DELIMITER = ";";

    @Override
    public TestingData loadTestingData(String content) throws UnknownDataFileException {
        List<List<Double>> test = new ArrayList<>();
        String[] lines = this.getLines(content);

        if(lines.length <= 1 || !lines[0].equals("test" + DELIMITER)) {
            this.throwUnknownDataFileException();
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            test.add(this.parseLine(line));
        }

        return new TestingData(test);
    }

    @Override
    public TrainingData loadTrainingData(String content) throws UnknownDataFileException {
        List<List<Double>> pos = new ArrayList<>();
        List<List<Double>> neg = new ArrayList<>();
        String[] lines = this.getLines(content);

        if(lines.length <= 1 || !lines[0].equals("negative" + DELIMITER)) {
            this.throwUnknownDataFileException();
        }

        int i;

        for (i = 1; i < lines.length && !lines[i].equals("positive" + DELIMITER); i++) {
            String line = lines[i];
            List<Double> add = this.parseLine(line);
            neg.add(add);
        }

        if(i + 1 >= lines.length) {
            this.throwUnknownDataFileException();
        }

        for (int j = i + 1; j < lines.length; j++) {
            String line = lines[j];
            List<Double> add = this.parseLine(line);
            pos.add(add);
        }

        return new TrainingData(pos, neg);
    }


    private List<Double> parseLine(String line) throws UnknownDataFileException {
        String[] split = line.split(DELIMITER);

        if(split.length != 2) {
            this.throwUnknownDataFileException();
        }

        return new ArrayList<Double>() {{
            add(Double.parseDouble(split[0]));
            add(Double.parseDouble(split[1]));
        }};
    }

    private String[] getLines(String content) {
        return content.split(System.getProperty("line.separator"));
    }

    private void throwUnknownDataFileException() throws UnknownDataFileException {
        throw new UnknownDataFileException("Invalid CSV data.");
    }
}
