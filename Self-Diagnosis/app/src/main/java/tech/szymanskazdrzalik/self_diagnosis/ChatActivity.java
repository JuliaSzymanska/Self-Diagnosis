package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import tech.szymanskazdrzalik.self_diagnosis.api.MakeDiagnoseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.MakeParseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityChatBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatMessage;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

// TODO: 16.12.2020 Jesli nie po angielsku to uzywamy https://medium.com/@yeksancansu/how-to-use-google-translate-api-in-android-studio-projects-7f09cae320c7 XD ZROBIC

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

    Animation slide_out_messbox;

    private ActivityChatBinding binding;
    private final View.OnClickListener onEndDiagnoseClick = v -> {
        // TODO: 16.12.2020 lokalizacja
        addUserMessage("Finish");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Diagnosis:\n");
        JSONArray conditions = RequestUtil.getInstance().getConditionsArray();
        try {
            for (int i = 0; i < conditions.length(); i++) {

                stringBuilder.append("Name: ").append(conditions.getJSONObject(i).getString("common_name")).append("\n");
                stringBuilder.append("Probability: ").append(conditions.getJSONObject(i).getString("probability")).append("\n\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length() - 1);
        binding.inputLayout.inputsContainer.removeAllViews();
        onDoctorMessage(stringBuilder.toString());


    };
    private boolean didAskForEndDiagnose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (chat.isPresent()) {
            try {
                RequestUtil.getInstance().setEvidenceArrayFromString(chat.get().getLastRequest());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            setNameInChat();
        }
        slide_out_messbox = AnimationUtils.loadAnimation(this, R.anim.slide_out_messbox);
        binding.chatLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                binding.scrollViewChat.post(() -> binding.scrollViewChat.fullScroll(View.FOCUS_DOWN));
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
    }

    private void setNameInChat() {
        if (GlobalVariables.getInstance().getCurrentUser().isPresent())
            generateNewDoctorMessageFromString("Hello " + GlobalVariables.getInstance().getCurrentUser().get().getName()
                    + "! \nHow can I help you today?");
        else {
            generateNewDoctorMessageFromString("Hello! \nHow can I help you today?");
        }
    }

    private void generateNewUserMessageFromString(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.user_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.userMessage);
        valueTV.setText(text);
        saveMessageToDB(text, true);
        binding.chatLayout.addView(linearLayout);
    }

    private void generateNewDoctorMessageFromString(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.doctor_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.doctorMessage);
        valueTV.setText(text);
        saveMessageToDB(text, false);
        binding.chatLayout.addView(linearLayout);
    }

    private void saveMessageToDB(String text, boolean isUserMessage) {
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (!chat.isPresent()) {
            saveChatToDB();
        }
        int chatId = GlobalVariables.getInstance().getCurrentChat().get().getId();
        int id = SampleSQLiteDBHelper.getNextMessageIdAvailable(this, chatId);
        Date date = new Date();
        ChatMessage message = new ChatMessage(id, chatId, date, text, isUserMessage);
        SampleSQLiteDBHelper.saveMessageDataToDB(this, message);
    }

    private void saveChatToDB() {
        Chat currentChat = new Chat(SampleSQLiteDBHelper.getNextChatIdAvailable(this),
                GlobalVariables.getInstance().getCurrentUser().get().getId(), "");
        GlobalVariables.getInstance().setCurrentChat(currentChat);
        SampleSQLiteDBHelper.saveChatDataToDB(this, currentChat);
    }

    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    public void sendSymptomsOnClick(View v) {
        if (!binding.inputLayout.inputSymptoms.getText().toString().equals("")) {
            new MakeParseRequest(this, binding.inputLayout.inputSymptoms.getText().toString());
            this.hideMessageBox();
        } else {
            // TODO: 17.12.2020 poprawić to
            Toast.makeText(this, "Input can not be empty.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDoctorMessage(String msg) {
        generateNewDoctorMessageFromString(msg);
    }

    @Override
    public void addUserMessage(String msg) {
        generateNewUserMessageFromString(msg);
    }

    @Override
    public void hideMessageBox() {
        slide_out_messbox.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.inputLayout.inputsContainer.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.inputLayout.inputsContainer.setAnimation(slide_out_messbox);
    }

    @Override
    public void addErrorMessageFromDoctor(String msg) {
        // TODO: 16.12.2020
    }

    private void questionButtonOnClick(String id, String choice, String userMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("choice_id", choice);
            RequestUtil.getInstance().addToEvidenceArray(jsonObject);
            new MakeDiagnoseRequest(this, userMessage);
            binding.inputLayout.inputsContainer.removeAllViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDoctorQuestionReceived(String id, JSONArray msg) {
        binding.inputLayout.inputsContainer.removeAllViews();
        binding.inputLayout.inputsContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_buttons));
        try {
            for (int i = 0; i < msg.length(); i++) {
                Button button = (Button) View.inflate(this, R.layout.answer_button, null);
                button.setText(msg.getJSONObject(i).getString("label"));
                int finalI = i;
                button.setOnClickListener(v -> {
                    try {
                        questionButtonOnClick(id, msg.getJSONObject(finalI).getString("id"), msg.getJSONObject(finalI).getString("label"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                binding.inputLayout.inputsContainer.addView(button);
                Space space = new Space(this);
                space.setLayoutParams(new LinearLayout.LayoutParams(12, 8));
                binding.inputLayout.inputsContainer.addView(space);
            }
            Button button = (Button) View.inflate(this, R.layout.answer_button, null);
            button.setText("Finish");
            button.setOnClickListener(onEndDiagnoseClick);
            binding.inputLayout.inputsContainer.addView(button);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(msg);
    }

    @Override
    public boolean finishDiagnose() {
        if (didAskForEndDiagnose) {
            return false;
        }
        this.didAskForEndDiagnose = true;

        // TODO: 16.12.2020 Lokalizacja
        onDoctorMessage("I believe I know your diagnose. \nDo you want to finish?");
        Button buttonYes = (Button) View.inflate(this, R.layout.answer_button, null);
        buttonYes.setText("Yes");
        buttonYes.setOnClickListener(onEndDiagnoseClick);

        Button buttonNo = (Button) View.inflate(this, R.layout.answer_button, null);
        buttonNo.setText("No");
        buttonNo.setOnClickListener(v -> {
            new MakeDiagnoseRequest(this, "No");
        });

        binding.inputLayout.inputsContainer.addView(buttonYes);
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(12, 8));
        binding.inputLayout.inputsContainer.addView(space);
        binding.inputLayout.inputsContainer.addView(buttonNo);
        return true;
    }
}