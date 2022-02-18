package org.tensorflow.lite.examples.detection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.LinkedList;
import java.util.Locale;
import java.util.List;

public class TTS extends BroadcastReceiver {

    private static Context context;
    TextToSpeech textToSpeech;

    public TTS() {
    }

    public static void giveContext(Context con) {
        context = con;
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    public void speakText(String toSpeak) {

        textToSpeech = new TextToSpeech(context,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.US);
                            int rs = textToSpeech.setLanguage(Locale.US);
                            List list = new LinkedList();

                            if (list.contains(toSpeak) == false) {
                                list.add(toSpeak);
                                textToSpeech.speak(list.get(list.size() - 1).toString(), TextToSpeech.QUEUE_FLUSH, null);
                            }

                            if (rs == textToSpeech.LANG_MISSING_DATA || rs == textToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Language not Supported");
                            }
                        } else {
                            Log.e("TTS", "Engine Fail");
                        }

                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                    }
                });

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        //if (bundle != null)
        //{
        //---retrieve the SMS message received---
        Object[] pdus = (Object[]) bundle.get("pdus");
        msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            str += "SMS from " + msgs[i].getOriginatingAddress();
            str += " :";
            str += msgs[i].getMessageBody().toString();
            str += "\n";
            //}
            //---display the new SMS message---
            speakText(str);
        }
    }
}
