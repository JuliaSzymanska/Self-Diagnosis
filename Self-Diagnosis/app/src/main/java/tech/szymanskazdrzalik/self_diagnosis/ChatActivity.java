package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.szymanskazdrzalik.self_diagnosis.api.MakeDiagnoseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.MakeParseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityChatBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

// TODO: 16.12.2020 Jesli nie po angielsku to uzywamy https://medium.com/@yeksancansu/how-to-use-google-translate-api-in-android-studio-projects-7f09cae320c7 XD ZROBIC

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

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
        setNameInChat();
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
            generateNewDoctorMessageFromString("Hello " + GlobalVariables.getInstance().getCurrentUser().get().getName() + "!");
        else {
            generateNewDoctorMessageFromString("Hello !");
        }
    }

    private void generateNewUserMessageFromString(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.user_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.userMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);

    }

    private void generateNewDoctorMessageFromString(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.doctor_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.doctorMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);
    }

    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void sendSymptomsOnClick(View v) {
        new MakeParseRequest(this, binding.inputLayout.inputSymptoms.getText().toString());
        this.hideMessageBox();
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
        // TODO: 16.12.2020 add animation
        binding.inputLayout.inputsContainer.removeAllViews();
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