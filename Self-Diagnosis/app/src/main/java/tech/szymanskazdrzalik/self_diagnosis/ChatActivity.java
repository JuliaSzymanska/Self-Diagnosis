package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tech.szymanskazdrzalik.self_diagnosis.api.MakeParseRequest;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityChatBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setNameInChat();
    }

    private void setNameInChat() {
        if (GlobalVariables.getInstance().getCurrentUser().isPresent())
            addDoctorMessageToChat("Hello " + GlobalVariables.getInstance().getCurrentUser().get().getName() + "!");
        else {
            addDoctorMessageToChat("Hello !");
        }
    }

    private void addUserMessageToChat(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.user_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.userMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);
//        binding.scrollViewChat.scrollTo(0, binding.scrollViewChat.getBottom());
        binding.scrollViewChat.fullScroll(View.FOCUS_DOWN);

    }

    private void addDoctorMessageToChat(String text) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.doctor_message, null);
        TextView valueTV = linearLayout.findViewById(R.id.doctorMessage);
        valueTV.setText(text);
        binding.chatLayout.addView(linearLayout);
//        binding.scrollViewChat.scrollTo(0, binding.scrollViewChat.getBottom());
        binding.scrollViewChat.fullScroll(View.FOCUS_DOWN);
    }

    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
    private int id = 0;

    public void sendSymptomsOnClick(View v) {
        // TODO: 16.12.2020 ODKOMENTOWAC
//        new MakeParseRequest(this,  binding.inputSymptoms.getText().toString());
        addUserMessageToChat(binding.inputSymptoms.getText().toString() + " " + id);
        addDoctorMessageToChat("Hejka naklejka siemka tu lenka " + id);
        id++;
    }

}