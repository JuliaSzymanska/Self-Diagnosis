package tech.szymanskazdrzalik.self_diagnosis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import tech.szymanskazdrzalik.self_diagnosis.api.MakeCovidRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.MakeDiagnoseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.MakeParseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityChatBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatMessage;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;
import tech.szymanskazdrzalik.self_diagnosis.helpers.PdfProducer;

// TODO: 16.12.2020 Jesli nie po angielsku to uzywamy https://medium.com/@yeksancansu/how-to-use-google-translate-api-in-android-studio-projects-7f09cae320c7 XD ZROBIC
// TODO: 13.01.2021 usuwanie starszych nieukonczonych diagnoz
// TODO: 17.01.2021 nie zapisywac pierwszej wiadomosci
// TODO: 20.01.2021 zapytanie o zezwolenie na dostep rpzy odczycie obrazka
// TODO: 21.01.2021 nie zapisywac diagnozy w ktorej nie zostalo wyslane zadne zapytanie

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

    Animation slide_out_messbox;
    String lastDoctorMessage = "";
    Boolean isCovid;
    private ActivityChatBinding binding;
    // TODO: 14.01.2021 Wykorzystać do wczytywania odpowiedzi
    private final View.OnClickListener onEndDiagnoseClick = v -> {
        JSONArray conditions = RequestUtil.getInstance().getConditionsArray();
        System.out.println(RequestUtil.getInstance().getEvidenceArray());
        GlobalVariables.getInstance().getCurrentChat().get().setConditionsArray(conditions.toString());
        saveOrUpdateChatToDB();
        addUserMessage(getResources().getString(R.string.finish));
        binding.inputLayout.inputsContainer.removeAllViews();
        try {
            onDoctorMessage(conditions.getJSONObject(0).getString("common_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private boolean didAskForEndDiagnose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCovid = getIntent().getBooleanExtra(getString(R.string.is_covid), false);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setChatOnCreate();
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

    private void setChatOnCreate() {
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (chat.isPresent()) {
            try {
                RequestUtil.getInstance().setEvidenceArrayFromString(chat.get().getLastRequest());
                setAllMessages(SampleSQLiteDBHelper.getAllMessagesForChat(this, chat.get().getId()));
                if (chat.get().getConditionsArray() == null) {
                    // FIXME: 20.01.2021 empty string
                    this.onDoctorQuestionReceived(chat.get().getLastDoctorQuestionId(), new JSONArray(chat.get().getLastDoctorQuestion()), "");
                } else {
                    binding.inputLayout.inputsContainer.removeAllViews();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            this.createFirstMessageFromDoctor();
        }
    }

    private void setAllMessages(List<ChatMessage> messages) {
        if (GlobalVariables.getInstance().getCurrentChat().get().getConditionsArray() == null) {
            for (ChatMessage message : messages) {
                if (message.getIsUserMessage()) {
                    generateNewUserMessageFromStringWithoutSaving(message.getMessage());
                } else {
                    generateNewDoctorMessageFromStringWithoutSaving(message.getMessage());
                }
            }
        } else {
            for (int i = 0; i < messages.size() - 1; i++) {
                if (messages.get(i).getIsUserMessage()) {
                    generateNewUserMessageFromStringWithoutSaving(messages.get(i).getMessage());
                } else {
                    generateNewDoctorMessageFromStringWithoutSaving(messages.get(i).getMessage());
                }
            }
            generateDiagnosisMessageFromStringWithoutSaving(messages.get(messages.size() - 1).getMessage());
        }
    }

    private void createFirstMessageFromDoctor() {
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
        if (!GlobalVariables.getInstance().getCurrentChat().isPresent() || GlobalVariables.getInstance().getCurrentChat().get().getConditionsArray() == null) {
            generateNewDoctorMessageFromStringWithoutSaving(text);
        } else {
            generateDiagnosisMessageFromStringWithoutSaving(text);
        }
        saveMessageToDB(text, false);
    }

    private void generateDiagnosisMessageFromStringWithoutSaving(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.diagnose_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.standard_info);
        valueTV.setText(text);
        TextView advancedTV = linearLayout.findViewById(R.id.advanced_info);
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat df2 = new DecimalFormat("##.##");
        try {
            JSONArray conditions = new JSONArray(GlobalVariables.getInstance().getCurrentChat().get().getConditionsArray());
            for (int i = 0; i < conditions.length(); i++) {
                stringBuilder.append(getString(R.string.name)).append(conditions.getJSONObject(i).getString("common_name")).append("\n");
                stringBuilder.append(getString(R.string.probability)).append(df2.format(conditions.getJSONObject(i).getDouble("probability") * 100)).append("\n\n\n");
                stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length() - 1);
            }
            advancedTV.setText(stringBuilder.toString());
            Button advanced_info_button = linearLayout.findViewById(R.id.advanced_info_button);
            linearLayout.findViewById(R.id.advanced_info_button).setOnClickListener(v -> {
                if (advancedTV.getVisibility() == View.GONE) {
                    advancedTV.setVisibility(View.VISIBLE);
                    advanced_info_button.setText(R.string.show_less);
                } else {
                    advancedTV.setVisibility(View.GONE);
                    advanced_info_button.setText(R.string.show_more);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.chatLayout.addView(linearLayout);
        this.lastDoctorMessage = text;
    }

    public void onExportButtonClick(View v) {
        requestPermission();
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 300) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (GlobalVariables.getInstance().getCurrentChat().isPresent()) {
                    PdfProducer.createPdfFile(this, SampleSQLiteDBHelper.getAllMessagesForChat(this,
                            GlobalVariables.getInstance().getCurrentChat().get().getId()));
                }
            }
        }
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
        int chatId = saveOrUpdateChatToDB();
        int id = SampleSQLiteDBHelper.getNextMessageIdAvailable(this, chatId);
        ChatMessage message = new ChatMessage(id, chatId, text, isUserMessage);
        SampleSQLiteDBHelper.saveMessageDataToDB(this, message);
    }

    private int saveOrUpdateChatToDB() {
        Optional<Chat> chat = GlobalVariables.getInstance().getCurrentChat();
        if (!chat.isPresent()) {
            createNewChatAndSaveToDB();
            chat = GlobalVariables.getInstance().getCurrentChat();
        }
        int chatId = GlobalVariables.getInstance().getCurrentChat().get().getId();
        chat.get().setLastRequest(RequestUtil.getInstance().getStringFromEvidenceArray());
        SampleSQLiteDBHelper.saveChatDataToDB(this, chat.get());
        return chatId;
    }

    private void createNewChatAndSaveToDB() {
        Chat currentChat = Chat.builder(SampleSQLiteDBHelper.getNextChatIdAvailable(this), GlobalVariables.getInstance().getCurrentUser().get().getId())
                .conditionArray(null)
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
        if (binding.inputLayout.inputSymptoms.getText().toString().trim().length() > 0) {
            new MakeParseRequest(this, binding.inputLayout.inputSymptoms.getText().toString());
            this.hideMessageBox();
        } else {
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

    private void questionButtonOnClick(String id, String choice, String userMessage, String name) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("choice_id", choice);
            jsonObject.put("name", name);
            RequestUtil.getInstance().addToEvidenceArray(jsonObject);
            if (this.isCovid) {
                new MakeCovidRequest(this, userMessage);
            } else {
                new MakeDiagnoseRequest(this, userMessage);
            }
            binding.inputLayout.inputsContainer.removeAllViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDoctorQuestionReceived(String id, JSONArray msg, String name) {
        GlobalVariables.getInstance().getCurrentChat().get().setLastDoctorQuestionId(id);
        GlobalVariables.getInstance().getCurrentChat().get().setLastDoctorQuestion(msg.toString());
        System.out.println(name);
        saveOrUpdateChatToDB();
        binding.inputLayout.inputsContainer.removeAllViews();
        binding.inputLayout.inputsContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_buttons));

        try {
            for (int i = 0; i < msg.length(); i++) {
                Button button = (Button) View.inflate(this, R.layout.answer_button, null);
                button.setText(msg.getJSONObject(i).getString("label"));
                int finalI = i;
                button.setOnClickListener(v -> {
                    try {
                        questionButtonOnClick(id, msg.getJSONObject(finalI).getString("id"),
                                msg.getJSONObject(finalI).getString("label"),
                                name);
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
            if (this.isCovid) {
                new MakeCovidRequest(this, getString(R.string.no));
            } else {
                new MakeDiagnoseRequest(this, getString(R.string.no));
            }
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
                            new JSONArray(GlobalVariables.getInstance().getCurrentChat().get().getLastDoctorQuestion()),
                            ""
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