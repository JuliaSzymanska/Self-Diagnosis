package tech.szymanskazdrzalik.self_diagnosis.helpers;

import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

import java.util.Arrays;

public class TranslatorHelper {

    public static void translateText(){
        try {
            Translate t = new Translate.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport()
                    , com.google.cloud.gson.GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("Stackoverflow-Example")
                    .build();
            Translate.Translations.List list = t.new Translations().list(
                    Arrays.asList(
                            //Pass in list of strings to be translated
                            "Hello World",
                            "How to use Google Translate from Java"),
                    //Target language
                    "ES");
            //Set your API-Key from https://console.developers.google.com/
            list.setKey("you-need-your-own-api-key");
            TranslationsListResponse response = list.execute();
            for(TranslationsResource tr : response.getTranslations()) {
                System.out.println(tr.getTranslatedText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
