package com.okstate.VisualComputingandImageProcessingLab.HouseRec.customview;

import java.util.List;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Recognition;


public interface ResultsView {
    public void setResults(final List<Recognition> results);
}