package cz.martinmach;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by mmx on 17.2.17.
 */
public class JsonDataFileInterpreter implements DataFileInterpreter {
    private Gson g;

    public JsonDataFileInterpreter() {
        this.g = new Gson();
    }

    @Override
    public TestingData loadTestingData(String content) throws UnknownDataFileException {
        try {
            TestingData data =this.g.fromJson(content, TestingData.class);
            if(data.getTest() == null) {
                this.throwUnknownDataFileException();
                return null;
            }

            return data;
        }
        catch (Exception e) {
            this.throwUnknownDataFileException();
            return null;
        }
    }

    @Override
    public TrainingData loadTrainingData(String content) throws UnknownDataFileException {
        try {
            TrainingData data =this.g.fromJson(content, TrainingData.class);
            if(data.getNegative() == null || data.getPositive() == null) {
                this.throwUnknownDataFileException();
                return null;
            }

            return data;
        }
        catch (Exception e) {
            this.throwUnknownDataFileException();
            return null;
        }
    }

    private void throwUnknownDataFileException() throws UnknownDataFileException {
        throw new UnknownDataFileException("Invalid json data.");
    }
}
