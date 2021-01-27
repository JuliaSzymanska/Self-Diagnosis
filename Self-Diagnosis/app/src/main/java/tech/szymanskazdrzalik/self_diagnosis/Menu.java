package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import tech.szymanskazdrzalik.self_diagnosis.api.RequestUtil;
import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityMenuBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.helpers.ChatAdapter;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;
import tech.szymanskazdrzalik.self_diagnosis.helpers.SharedPreferencesHelper;

public class Menu extends AppCompatActivity implements AddProfileFragment.AddProfileFragmentListener {

    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setPicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }

    public void setPicture() {
        if (!GlobalVariables.getInstance().getCurrentUser().isPresent()) {
            SharedPreferencesHelper.loadUser(this);
        }

        if (GlobalVariables.getInstance().getCurrentUser().isPresent()) {
            binding.menuTop1Bar.profileImage.setImageBitmap(GlobalVariables.getInstance().getCurrentUser().get().getPicture());
        }
    }

    public void onProfilePictureClick(View v) {
        runAddProfileFragment(false);
    }

    public void onCreateNewProfileClick(View v) {
        runAddProfileFragment(true);
    }

    private void runAddProfileFragment(boolean isNewUser) {
        Fragment fragment = new AddProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.is_new_user), isNewUser);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!isNewUser) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        }
        transaction.replace(R.id.layoutToBeReplacedWithFragmentInMenu, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void openCovid(View v) {
        GlobalVariables.getInstance().setCurrentChat(null);
        Intent intent = new Intent(Menu.this, ChatActivity.class);
        intent.putExtra(getString(R.string.is_covid), true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void onChangeProfileClick(View v) {
        Fragment fragment = new ChangeProfile();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutToBeReplacedWithFragmentInMenu, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

    private void loadChats() {
        ArrayList<Chat> chatArrayList = (ArrayList<Chat>) ChatSQLiteDBHelper.getAllChatsForUserFromDB(this,
                GlobalVariables.getInstance().getCurrentUser().get().getId());
        ChatAdapter chatAdapter = new ChatAdapter(this, chatArrayList);
        binding.chatList.setAdapter(chatAdapter);

    }

    public void goToChatActivity() {
        Intent intent = new Intent(Menu.this, ChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void newChatButtonOnClick(View v) {
        GlobalVariables.getInstance().setCurrentChat(null);
        RequestUtil.getInstance().resetEvidenceArray();
        RequestUtil.getInstance().resetConditionsArray();
        goToChatActivity();
    }

    @Override
    public void callback(String result) {
        if (result.equals(getString(R.string.reload))) {
            setPicture();
            loadChats();
        } else if (result.equals(getString(R.string.openChat))) {
            goToChatActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }
}