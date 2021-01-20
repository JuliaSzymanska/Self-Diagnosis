package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatMessage;

public class PdfProducer {

    public static void createPdfFile(List<ChatMessage> messages) {
        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Paint myPaint = new Paint();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getAllNames());
        stringBuilder.append("\n\n\n");
        stringBuilder.append(getAllMessages(messages));

        int x = 10, y = 25;
        for (String line : stringBuilder.toString().split("\n")) {
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y += myPaint.descent() - myPaint.ascent();
        }

        myPdfDocument.finishPage(myPage);

        String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/Consultation_" + new Date() + ".pdf";
        File myFile = new File(myFilePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
    }

    private static String getAllMessages(List<ChatMessage> messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).getIsUserMessage()) {
                stringBuilder.append("User: ").append(messages.get(i).getMessage()).append("\n\n");
            } else {
                stringBuilder.append("Doctor: ").append(messages.get(i).getMessage()).append("\n\n");
            }
        }
        stringBuilder.append("Doctor: Your diagnosis: ").append(messages.get(messages.size() - 1).getMessage()).append("\n\n");
        return stringBuilder.toString();
    }

    private static String getAllNames() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Symptoms: \n");
        JSONArray jsonArray = RequestUtil.getInstance().getEvidenceArray();
        StringBuilder stringBuilderPresent = new StringBuilder();
        stringBuilderPresent.append("Present: \n");
        StringBuilder stringBuilderAbsent = new StringBuilder();
        stringBuilderAbsent.append("Absent: \n");
        StringBuilder stringBuilderNotKnow = new StringBuilder();
        stringBuilderNotKnow.append("Not know: \n");
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getString("choice_id").equals("present")) {
                    stringBuilderPresent.append("   *");
                    stringBuilderPresent.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderPresent.append(", \n");
                } else if (jsonArray.getJSONObject(i).getString("choice_id").equals("absent")) {
                    stringBuilderAbsent.append("    *");
                    stringBuilderAbsent.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderAbsent.append(", \n");
                } else {
                    stringBuilderNotKnow.append("    *");
                    stringBuilderNotKnow.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderNotKnow.append(", \n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        stringBuilder.append(stringBuilderPresent.toString()).append("\n");
        stringBuilder.append(stringBuilderAbsent.toString()).append("\n");
        stringBuilder.append(stringBuilderNotKnow.toString()).append("\n");
        return stringBuilder.toString();
    }

}
