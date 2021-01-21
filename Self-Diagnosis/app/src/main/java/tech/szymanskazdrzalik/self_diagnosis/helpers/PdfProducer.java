package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatMessage;

public class PdfProducer {

    public static void createPdfFile(Context context, List<ChatMessage> messages) {
        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Paint myPaint = new Paint();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getDiagnose(context));
        stringBuilder.append("\n\n\n");
        stringBuilder.append(getAllNames(context));
        stringBuilder.append("\n\n\n");
        stringBuilder.append(getAllMessages(context, messages));

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
        Toast.makeText(context, R.string.exported_to_pdf_file, Toast.LENGTH_SHORT).show();
    }

    private static String getAllMessages(Context context, List<ChatMessage> messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).getIsUserMessage()) {
                stringBuilder.append(context.getString(R.string.user)).append(messages.get(i).getMessage()).append("\n\n");
            } else {
                stringBuilder.append(context.getString(R.string.doctor)).append(messages.get(i).getMessage()).append("\n\n");
            }
        }
        stringBuilder.append(context.getString(R.string.doctor_your_diagnosis)).append(messages.get(messages.size() - 1).getMessage()).append("\n\n");
        return stringBuilder.toString();
    }

    private static String getDiagnose(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.diagnose_with_white_space));
        stringBuilder.append("\n");
        try {
            JSONArray conditions = new JSONArray(GlobalVariables.getInstance().getCurrentChat().get().getConditionsArray());
            for (int i = 0; i < conditions.length(); i++) {
                stringBuilder.append(context.getString(R.string.name)).append(conditions.getJSONObject(i).getString("common_name")).append("\n");
                stringBuilder.append(context.getString(R.string.probability)).append(conditions.getJSONObject(i).getString("probability")).append("\n\n");
                stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private static String getAllNames(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.symptoms));
        stringBuilder.append(", \n");
        JSONArray jsonArray = RequestUtil.getInstance().getEvidenceArray();
        StringBuilder stringBuilderPresent = new StringBuilder();
        stringBuilderPresent.append(context.getString(R.string.present));
        StringBuilder stringBuilderAbsent = new StringBuilder();
        stringBuilderAbsent.append(context.getString(R.string.absent));
        StringBuilder stringBuilderNotKnow = new StringBuilder();
        stringBuilderNotKnow.append(context.getString(R.string.unknown));
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
