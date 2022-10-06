package com.okstate.VisualComputingandImageProcessingLab.HouseRec.VoiceProcessing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.CameraActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.CameraActivityBackup1;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.getContext;

public class SpeechRecognition {
    int video = 0;
    SpeechRecognizer mRecognizer;
    private static final String[] keywords_1 = new String[]{"hello"};
    private static final String[] keywords_2 = new String[]{"location", "where", "position"};
    private static final String[] keywords_3 = new String[]{"yes", "no"};
    private List<String> labels;
    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
            mRecognizer.destroy();

        }

        @Override
        public void onError(int i) {
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && matches.size() > 0) {
                System.out.println(matches);
                for (int i = 0; i < matches.size(); i++) {
                    // if (video == 0) {
                    for (String k : keywords_1) {
                        if (matches.get(i).contains(k)) {
                            TTS.setTextToSpeech1("Hello welcome to the House Rec App");
                            mRecognizer.destroy();
                            break;
                        }


                    }
                    for (String k : keywords_2) {
                        if (matches.get(i).contains(k)) {

                            TTS.setTextToSpeech1("The place in front of you is " + CameraActivityBackup1.getLocationName());
                            mRecognizer.destroy();
                            break;
                        }
                    }
                    for (String k : labels) {
                        //Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
                        if (matches.get(i).contains(k)) {
                            //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();

                            TTS.setTextToSpeech1("Would you like to play the video for " + k);
                            video = 1;
                            //CameraActivityBackup1.label_found(k);
                            mRecognizer.destroy();
                            break;
                        }

                    }
                    if (video == 1) {

                        //Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
                        if (matches.get(i).equals("yes")) {
                            //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();

                            TTS.setTextToSpeech1("Playing Video");
                            video = 0;
                            boolean start = true;
                            //CameraActivityBackup1.video_check(start);
                            mRecognizer.destroy();
                            break;
                        } else if (matches.get(i).equals("No")) {
                            TTS.setTextToSpeech1("Not Playing Video");
                            video = 0;

                            mRecognizer.destroy();
                            break;

                        }


                    }

                }
            }

            mRecognizer.destroy();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }

    };

    public void reset(Context context) {
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mRecognizer.setRecognitionListener(recognitionListener);
        initializeUI();
        Intent intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent_voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().toString());
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        intent_voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,4);
        intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
        intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1500);
        intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000);

        mRecognizer.startListening(intent_voice);

    }

    private void initializeUI() {



        LayoutDbHelper db = new LayoutDbHelper(getContext());


        labels = db.getAllLabels();

        db.close();

    }

}


