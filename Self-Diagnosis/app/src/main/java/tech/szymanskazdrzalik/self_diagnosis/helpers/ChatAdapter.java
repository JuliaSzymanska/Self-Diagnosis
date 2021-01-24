package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tech.szymanskazdrzalik.self_diagnosis.AddProfileFragment;
import tech.szymanskazdrzalik.self_diagnosis.Menu;
import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatSQLiteDBHelper;

public class ChatAdapter extends ArrayAdapter<Chat> {

    Context context;
    AddProfileFragment.AddProfileFragmentListener mListener;
    List<Chat> chatList;

    View.OnClickListener openChatOnClickListener = v -> {
        Chat chat = (Chat) v.getTag();
        GlobalVariables.getInstance().setCurrentChat(chat);
        if (mListener != null) {
            mListener.callback(context.getString(R.string.openChat));
        }
    };

    public ChatAdapter(Menu activity, ArrayList<Chat> chats) {
        super(activity, 0, chats);
        this.context = activity;
        this.chatList = chats;
        setmListener(activity);
    }

    @Nullable
    @Override
    public Chat getItem(int position) {
        return chatList.get(position);
    }

    public void setmListener(AddProfileFragment.AddProfileFragmentListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
        }
        if (chat != null) {
            String date = ChatSQLiteDBHelper.DB_DATE_MESSAGE_FORMAT.format(chat.getDate());
            TextView tvChatDate = convertView.findViewById(R.id.chatDate);
            TextView tvChatTime = convertView.findViewById(R.id.chatTime);
            tvChatDate.setText(date.substring(0, 10));
            tvChatTime.setText(date.substring(11));
            convertView.setTag(chat);
            convertView.setOnClickListener(openChatOnClickListener);
        }
        return convertView;
    }

}
