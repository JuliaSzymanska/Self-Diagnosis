package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.content.Context;

import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

import java.util.Arrays;
import java.util.Locale;

import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;

public class TranslatorHelper {

    public static void TranslateText(Context context, String text) {
        try {
            Translate t = new Translate.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport()
                    , com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("Stackoverflow-Example")
                    .build();
            Translate.Translations.List list = t.new Translations().list(
                    Arrays.asList(
                            text),
                    Locale.getDefault().getLanguage());
            list.setKey(RequestUtil.getTranslatorApiKey(context));
            TranslationsListResponse response = list.execute();
            System.out.println("Translator: !!!!!!!!!!!!");
            for (TranslationsResource tr : response.getTranslations()) {
                System.out.println(tr.getTranslatedText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
