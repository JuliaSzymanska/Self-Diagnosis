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
// TODO: 13.01.2021 jesli uzytkownik nie ma zadnych chatow to trzeba naprawic
// TODO: 13.01.2021 usuwanie starszych nieukonczonych diagnoz
// TODO: 13.01.2021 Wiadomosci maja znikać po wyjsciu z czatu
// TODO: 13.01.2021 Przyciski do rozmowy po wczytaniu też muszą zostać wczytane

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

    Animation slide_out_messbox;
    String lastDoctorMessage = "";
    private ActivityChatBinding binding;
    private final View.OnClickListener onEndDiagnoseClick = v -> {
        // TODO: 16.12.2020 lokalizacja
        saveChatToDB(true);
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
    // TODO: 14.01.2021 Wykorzystać do wczytywania odpowiedzi
    private String previousQuestionId;
    private JSONArray previousDoctorMsgForButtons;

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
                System.out.println("Evidence: " + RequestUtil.getInstance().getStringFromEvidenceArray());
                setAllMessages(SampleSQLiteDBHelper.getAllMessagesForChat(this, chat.get().getId()));
                new MakeDiagnoseRequest(this);
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
            generateNewDoctorMessageFromStringWithoutSaving("Hello " + GlobalVariables.getInstance().getCurrentUser().get().getName()
                    + "! \nHow can I help you today?");
        else {
            generateNewDoctorMessageFromStringWithoutSaving("Hello! \nHow can I help you today?");
        }
    }

    private void generateNewUserMessageFromString(String text) {
        generateNewUserMessageFromStringWithoutSaving(text);
        saveMessageToDB(this.lastDoctorMessage, false);
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
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (!chat.isPresent()) {
            saveChatToDB(false);
            chat = GlobalVariables.getInstance().getCurrentChat();
        }
        int chatId = GlobalVariables.getInstance().getCurrentChat().get().getId();
        chat.get().setLastRequest(RequestUtil.getInstance().getStringFromEvidenceArray());
        SampleSQLiteDBHelper.saveChatDataToDB(this, chat.get());
        int id = SampleSQLiteDBHelper.getNextMessageIdAvailable(this, chatId);
        ChatMessage message = new ChatMessage(id, chatId, text, isUserMessage);
        SampleSQLiteDBHelper.saveMessageDataToDB(this, message);
    }

    private void saveChatToDB(boolean isFinished) {
        Chat currentChat = new Chat(SampleSQLiteDBHelper.getNextChatIdAvailable(this),
                GlobalVariables.getInstance().getCurrentUser().get().getId(), "", isFinished);
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
        generateNewDoctorMessageFromStringWithoutSaving(msg);
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
        previousQuestionId = id;
        previousDoctorMsgForButtons = msg;
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

    @Override
    public void onRequestFailure() {
        // FIXME: 14.01.2021 FIX MESSAGE
        this.generateNewDoctorMessageFromStringWithoutSaving("ERROR FIX ME D :");
        if (this.previousQuestionId != null && this.previousDoctorMsgForButtons != null) {
            this.onDoctorQuestionReceived(previousQuestionId, previousDoctorMsgForButtons);
        } else {
            // TODO: 14.01.2021 przeniesc edittext, button do innego xmla, a tutaj to inflatowac
            // TODO: 14.01.2021 sprawdzic czemu animacja nie dziala
            View view = View.inflate(this, R.layout.msg_input_bar, null);
            binding.inputLayout.inputsContainer.removeAllViews();
            binding.inputLayout.inputsContainer.addView(view);
        }
    }
}