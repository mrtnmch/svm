package cz.martinmach;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by mmx on 17.2.17.
 */
public class DataFileFactory {
    private final static HashMap<String, DataFileInterpreter> INTERPRETERS = new LinkedHashMap<String, DataFileInterpreter>() {{
        put("json", new JsonDataFileInterpreter());
        put("csv", new CsvDataFileInterpreter());
    }};

    public TestingData loadTestingData(File file) throws UnknownDataFileException, IOException {
        return this.getInterpreter(file).loadTestingData(this.loadFileContent(file));
    }

    public TrainingData loadTrainingData(File file) throws UnknownDataFileException, IOException {
        return this.getInterpreter(file).loadTrainingData(this.loadFileContent(file));
    }

    private DataFileInterpreter getInterpreter(File file) throws UnknownDataFileException {
        String name = file.getName();
        String[] split = name.split("\\.");
        String extension = split[split.length - 1];

        if (INTERPRETERS.containsKey(extension)) {
            return INTERPRETERS.get(extension);
        }

        this.throwUnknownDataFileException();
        return null;
    }

    private void throwUnknownDataFileException() throws UnknownDataFileException {
        throw new UnknownDataFileException("Unknown data file type. Known types: " + String.join(", ", INTERPRETERS.keySet()));
    }

    private String loadFileContent(File file) throws IOException {
        final String EoL = System.getProperty("line.separator");
        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset());

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(EoL);
        }

        return sb.toString();
    }
}
