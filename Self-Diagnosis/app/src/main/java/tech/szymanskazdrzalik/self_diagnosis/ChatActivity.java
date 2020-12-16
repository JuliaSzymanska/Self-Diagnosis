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
            addUserMessageToChat("Hello " + GlobalVariables.getInstance().getCurrentUser().get().getName() + "!");
        else {
            addUserMessageToChat("Hello !");
        }
    }

    private void addUserMessageToChat(String text) {
        TextView valueTV = (TextView) View.inflate(this, R.layout.user_message, null);
        valueTV.setText(text);
        binding.chatLayout.addView(valueTV);
    }

    private void addDoctorMessageToChat(String text) {
        TextView valueTV = (TextView) View.inflate(this, R.layout.doctor_message, null);
        valueTV.setText(text);
        binding.chatLayout.addView(valueTV);
    }

    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void sendSymptomsOnClick(View v) {
        new MakeParseRequest(this, "it hurts when I pee, also, stomach ache");
    }

}