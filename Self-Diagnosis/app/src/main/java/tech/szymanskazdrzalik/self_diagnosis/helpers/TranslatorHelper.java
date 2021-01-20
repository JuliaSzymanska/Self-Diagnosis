package tech.szymanskazdrzalik.self_diagnosis.helpers;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;

public class TranslatorHelper {

    public static void TranslateText(Context context, String text) {
        try {
            Translate translate = (Translate) TranslateOptions.getDefaultInstance().getService();
            Translation translation = translate("¡Hola Mundo!");
            System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());
            Translate t = new Translate.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("My First Project")
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

    public static void translateText(String text) {
        String projectId = "affable-beaker-302222";
        String targetLanguage = "pl";
        translateText(projectId, targetLanguage, text);
    }

    public static void translateText(String projectId, String targetLanguage, String text) {
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(text)
                            .build();
            TranslateTextResponse response = client.translateText(request);

            for (Translation translation : response.getTranslationsList()) {
                System.out.printf("Translated text: %s\n", translation.getTranslatedText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
