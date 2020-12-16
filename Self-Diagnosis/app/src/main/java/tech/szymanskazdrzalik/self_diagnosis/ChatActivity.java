package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tech.szymanskazdrzalik.self_diagnosis.api.MakeParseRequest;
import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityChatBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class ChatActivity extends AppCompatActivity implements RequestUtil.ChatRequestListener {

    private ActivityChatBinding binding;

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
        new MakeParseRequest(this,  binding.inputSymptoms.getText().toString());
        addUserMessage(binding.inputSymptoms.getText().toString());
    }

    @Override
    public void addDoctorMessage(String msg) {
        generateNewDoctorMessageFromString(msg);
    }

    @Override
    public void addUserMessage(String msg) {
        generateNewUserMessageFromString(msg);
    }

    @Override
    public void hideChat() {
        // TODO: 16.12.2020
    }

    @Override
    public void addErrorMessageFromDoctor(String msg) {
        // TODO: 16.12.2020
    }
}