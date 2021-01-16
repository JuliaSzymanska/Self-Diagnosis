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

import java.util.Date;
import java.util.List;
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
// TODO: 13.01.2021 usuwanie starszych nieukonczonych diagnoz
// TODO: 16.01.2021 po zakonczeniiu diagnozy i wczytaniu jej maja nie pojawiac sie przyciski

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

    Animation slide_out_messbox;
    String lastDoctorMessage = "";
    private ActivityChatBinding binding;
    // TODO: 14.01.2021 Wykorzystać do wczytywania odpowiedzi
    private final View.OnClickListener onEndDiagnoseClick = v -> {
        // TODO: 16.12.2020 lokalizacja
        saveOrUpdateChatToDB(true);
        addUserMessage(getResources().getString(R.string.finish));
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
                setAllMessages(SampleSQLiteDBHelper.getAllMessagesForChat(this, chat.get().getId()));
                this.onDoctorQuestionReceived(chat.get().getLastDoctorQuestionId(), new JSONArray(chat.get().getLastDoctorQuestion()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            this.setNameInChat();
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

    private void setAllMessages(List<ChatMessage> messages) {
        for (ChatMessage message : messages) {
            if (message.getIsUserMessage()) {
                generateNewUserMessageFromStringWithoutSaving(message.getMessage());
            } else {
                generateNewDoctorMessageFromStringWithoutSaving(message.getMessage());
            }
        }
    }

    private void setNameInChat() {
        if (GlobalVariables.getInstance().getCurrentUser().isPresent())
            generateNewDoctorMessageFromString(getString(R.string.hallo_only) + GlobalVariables.getInstance().getCurrentUser().get().getName()
                    + "! " + getString(R.string.how_can_i_help_you));
        else {
            generateNewDoctorMessageFromString(getString(R.string.hello_with_exclamation_mark) + getString(R.string.how_can_i_help_you));
        }
    }

    private void generateNewUserMessageFromString(String text) {
        generateNewUserMessageFromStringWithoutSaving(text);
        saveMessageToDB(text, true);
    }

    private void generateNewDoctorMessageFromString(String text) {
        generateNewDoctorMessageFromStringWithoutSaving(text);
        saveMessageToDB(text, false);
    }

    private void generateNewDoctorMessageFromStringWithoutSaving(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.doctor_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.doctorMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);
        this.lastDoctorMessage = text;
    }

    private void generateNewUserMessageFromStringWithoutSaving(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.user_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.userMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);
    }

    private void saveMessageToDB(String text, boolean isUserMessage) {
        int chatId = saveOrUpdateChatToDB(false);
        int id = SampleSQLiteDBHelper.getNextMessageIdAvailable(this, chatId);
        ChatMessage message = new ChatMessage(id, chatId, text, isUserMessage);
        SampleSQLiteDBHelper.saveMessageDataToDB(this, message);
    }

    private int saveOrUpdateChatToDB(boolean isFinished) {
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (!chat.isPresent()) {
            createNewChatAndSaveToDB(isFinished);
            chat = GlobalVariables.getInstance().getCurrentChat();
        }
        int chatId = GlobalVariables.getInstance().getCurrentChat().get().getId();
        chat.get().setLastRequest(RequestUtil.getInstance().getStringFromEvidenceArray());
        SampleSQLiteDBHelper.saveChatDataToDB(this, chat.get());
        return chatId;
    }

    private void createNewChatAndSaveToDB(boolean isFinished) {
        Chat currentChat = Chat.builder(SampleSQLiteDBHelper.getNextChatIdAvailable(this), GlobalVariables.getInstance().getCurrentUser().get().getId())
                .isFinished(isFinished)
                .date(new Date())
                .lastRequest(RequestUtil.getInstance().getStringFromEvidenceArray())
                .build();
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
            Toast.makeText(this, getString(R.string.input_can_not_be_empty), Toast.LENGTH_LONG).show();
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
        GlobalVariables.getInstance().getCurrentChat().get().setLastDoctorQuestionId(id);
        GlobalVariables.getInstance().getCurrentChat().get().setLastDoctorQuestion(msg.toString());
        System.out.println(GlobalVariables.getInstance().getCurrentChat().get().toString());
        saveOrUpdateChatToDB(false);
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
            button.setText(getString(R.string.finish));
            button.setOnClickListener(onEndDiagnoseClick);
            binding.inputLayout.inputsContainer.addView(button);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean finishDiagnose() {
        if (didAskForEndDiagnose) {
            return false;
        }
        this.didAskForEndDiagnose = true;
        onDoctorMessage(getString(R.string.finish_diagnose_question));
        Button buttonYes = (Button) View.inflate(this, R.layout.answer_button, null);
        buttonYes.setText(R.string.yes);
        buttonYes.setOnClickListener(onEndDiagnoseClick);

        Button buttonNo = (Button) View.inflate(this, R.layout.answer_button, null);
        buttonNo.setText(R.string.no);
        buttonNo.setOnClickListener(v -> {
            new MakeDiagnoseRequest(this, getString(R.string.no));
        });

        binding.inputLayout.inputsContainer.addView(buttonYes);
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(12, 8));
        binding.inputLayout.inputsContainer.addView(space);
        binding.inputLayout.inputsContainer.addView(buttonNo);
        return true;
    }

    @Override
    public void onRequestFailure() {
        this.generateNewDoctorMessageFromString(getString(R.string.error_messsage_response_doctor));
        if (GlobalVariables.getInstance().getCurrentChat().isPresent()) {
            String id = GlobalVariables.getInstance().getCurrentChat().get().getLastDoctorQuestionId();
            String msg = GlobalVariables.getInstance().getCurrentChat().get().getLastDoctorQuestion();
            if (id != null && msg != null) {
                try {
                    this.onDoctorQuestionReceived(GlobalVariables.getInstance().getCurrentChat().get().getLastDoctorQuestionId(),
                            new JSONArray(GlobalVariables.getInstance().getCurrentChat().get().getLastDoctorQuestion())
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO: 14.01.2021 sprawdzic czemu animacja nie dziala
                View view = View.inflate(this, R.layout.msg_input_bar_inner, null);
                binding.inputLayout.inputsContainer.removeAllViews();
                binding.inputLayout.inputsContainer.addView(view);
            }
        }
    }
}