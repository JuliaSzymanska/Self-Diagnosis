package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
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

    private static final int PAGE_WIDTH = 630;
    private static final int PAGE_HEIGHT = 891;
    private static final int LOGO_SIZE = 120;
    private static final int SPACE = 15;
    private static final int GENERAL_TEXT_WIDTH = SPACE * 4;
    private static final int GENERAL_TEXT_HEIGHT = LOGO_SIZE + (4 * SPACE);

    public static void createPdfFile(Context context, List<ChatMessage> messages) {

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        Paint myPaint = new Paint();
        myPaint.setTextSize(15);

        Paint titlePaint = new Paint();
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(35);

        setPageStyle(context, myPage, titlePaint, myPaint);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getDiagnose(context));
        stringBuilder.append("\n\n\n");
        stringBuilder.append(getAllNames(context));
        stringBuilder.append("\n\n\n");

        int i = 0;
        int x = GENERAL_TEXT_WIDTH, y = GENERAL_TEXT_HEIGHT;
        for (String line : stringBuilder.toString().split("\n")) {
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y += myPaint.descent() - myPaint.ascent();
            if (y > PAGE_HEIGHT - (SPACE * 2)) {
                myPdfDocument.finishPage(myPage);
                myPage = myPdfDocument.startPage(myPageInfo);
                setPageStyle(context, myPage, titlePaint, myPaint);
                myPage.getCanvas().drawText(line, x, y, myPaint);
                y += myPaint.descent() - myPaint.ascent();
            } else {
                myPage.getCanvas().drawText(line, x, y, myPaint);
                y += myPaint.descent() - myPaint.ascent();
            }
        }

        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getAllMessages(context, messages));

//        y = GENERAL_TEXT_HEIGHT;
        i = 0;
        for (String line : stringBuilder2.toString().split("\n")) {
            if (y > PAGE_HEIGHT - (SPACE * 2)) {
                myPdfDocument.finishPage(myPage);
                myPage = myPdfDocument.startPage(myPageInfo2);
                myPage.getCanvas().drawText(line, x, y, myPaint);
                y += myPaint.descent() - myPaint.ascent();
                if (i++ == stringBuilder2.length() - 1)
                    myPdfDocument.finishPage(myPage);
            } else {
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y += myPaint.descent() - myPaint.ascent();
            }
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

    private static void setPageStyle(Context context, PdfDocument.Page page, Paint titlePaint, Paint myPaint){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.doctor_bigger);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, LOGO_SIZE, LOGO_SIZE, false);

        Paint paint = new Paint();
        paint.setColor(context.getColor(R.color.blue_header_calendar_trasparent));

//        page.getCanvas().drawColor(context.getColor(R.color.light_blue_transparent));
        page.getCanvas().drawRoundRect(0, 0, PAGE_WIDTH, LOGO_SIZE + SPACE, SPACE / 2, SPACE / 2, paint);
        page.getCanvas().drawText(context.getString(R.string.app_name), (PAGE_WIDTH / 4) + (SPACE * 2), LOGO_SIZE - (SPACE), titlePaint);
        page.getCanvas().drawBitmap(scaledBitmap, (PAGE_WIDTH / 4) * 3, SPACE / 2, myPaint);
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
        stringBuilder.append("\n");
        JSONArray jsonArray = RequestUtil.getInstance().getEvidenceArray();
        StringBuilder stringBuilderPresent = new StringBuilder();
        stringBuilderPresent.append(context.getString(R.string.present));
        stringBuilderPresent.append("\n");
        StringBuilder stringBuilderAbsent = new StringBuilder();
        stringBuilderAbsent.append(context.getString(R.string.absent));
        stringBuilderAbsent.append("\n");
        StringBuilder stringBuilderNotKnow = new StringBuilder();
        stringBuilderNotKnow.append(context.getString(R.string.unknown));
        stringBuilderNotKnow.append("\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getString("choice_id").equals("present")) {
                    stringBuilderPresent.append("   * ");
                    stringBuilderPresent.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderPresent.append("\n");
                } else if (jsonArray.getJSONObject(i).getString("choice_id").equals("absent")) {
                    stringBuilderAbsent.append("    * ");
                    stringBuilderAbsent.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderAbsent.append("\n");
                } else {
                    stringBuilderNotKnow.append("    * ");
                    stringBuilderNotKnow.append(jsonArray.getJSONObject(i).getString("name"));
                    stringBuilderNotKnow.append("\n");
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
