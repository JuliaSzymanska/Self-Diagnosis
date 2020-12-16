package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tech.szymanskazdrzalik.self_diagnosis.AddProfileFragment;
import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.db.User;

public class UsersAdapter extends ArrayAdapter<User> {

    Context context;
    View.OnClickListener changeUserOnClickListener = v -> {
        User user = (User) v.getTag();
        GlobalVariables.getInstance().setCurrentUser(user);
        SharedPreferencesHelper.saveUserId(getContext(), user.getId());
        AddProfileFragment.AddProfileFragmentListener mListener = (AddProfileFragment.AddProfileFragmentListener) context;
        if (mListener != null) {
            mListener.callback(context.getString(R.string.reload));
        }
        ((Activity) this.context).onBackPressed();
    };

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        if (user != null) {
            TextView tvName = convertView.findViewById(R.id.userName);
            CircleImageView tvImage = convertView.findViewById(R.id.userImage);
            tvName.setText(user.getName());
            tvName.setTag(user);
            tvName.setOnClickListener(changeUserOnClickListener);
            tvImage.setImageBitmap(user.getPicture());
            tvImage.setTag(user);
            tvImage.setOnClickListener(changeUserOnClickListener);
        }
        return convertView;
    }

}
