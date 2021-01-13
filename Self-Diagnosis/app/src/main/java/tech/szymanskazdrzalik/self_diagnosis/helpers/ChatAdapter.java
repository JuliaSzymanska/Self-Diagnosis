package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;

public class ChatAdapter extends ArrayAdapter<Chat> {

    Context context;
    View.OnClickListener openChatOnClickListener = v -> {
        Chat chat = (Chat) v.getTag();
        GlobalVariables.getInstance().setCurrentChat(chat);

    };

    public ChatAdapter(Context context, ArrayList<Chat> chats) {
        super(context, 0, chats);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
        }
        if (chat != null) {
            try {
                String date = SampleSQLiteDBHelper.getStringDateForChat(this.context, chat.getId());
                TextView tvChatDate = convertView.findViewById(R.id.chatDate);
                TextView tvChatTime = convertView.findViewById(R.id.chatTime);
                tvChatDate.setText(date.substring(0, 9));
                tvChatTime.setText(date.substring(10));
                tvChatDate.setTag(chat);
                tvChatDate.setOnClickListener(openChatOnClickListener);
                tvChatTime.setTag(chat);
                tvChatTime.setOnClickListener(openChatOnClickListener);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return convertView;
    }

}
