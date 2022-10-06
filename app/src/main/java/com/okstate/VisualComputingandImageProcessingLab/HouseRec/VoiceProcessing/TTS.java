package com.okstate.VisualComputingandImageProcessingLab.HouseRec.VoiceProcessing;

import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.getContext;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.CameraActivityBackupRT;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainFragment;

import java.util.Locale;

//The class to run the textToSpeech. You can run it in anywhere you like to speech based on the text.
public class TTS {
    private static TextToSpeech textToSpeech = new TextToSpeech(ItemPhotosActivity.getContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = textToSpeech.setLanguage(Locale.US);

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!");
                } else {
                    Log.i("TTS", "Language Supported.");
                }
                Log.i("TTS", "Initialization success.");
            } else {
                Toast.makeText(getContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        }
    });



    public static void setTextToSpeech1(String s){
        int speechStatus = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }
}
