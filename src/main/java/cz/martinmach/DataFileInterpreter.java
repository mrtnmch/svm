package cz.martinmach;

/**
 * Created by mmx on 17.2.17.
 */
public interface DataFileInterpreter {
    public TestingData loadTestingData(String content) throws UnknownDataFileException;
    public TrainingData loadTrainingData(String content) throws UnknownDataFileException;
}
