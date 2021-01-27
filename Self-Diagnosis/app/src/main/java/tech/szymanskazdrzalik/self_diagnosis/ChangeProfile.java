package tech.szymanskazdrzalik.self_diagnosis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentChangeProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.UsersAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class ChangeProfile extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final View.OnClickListener onBackArrowClicked = v -> getActivity().onBackPressed();
    private String mParam1;
    private String mParam2;
    private FragmentChangeProfileBinding binding;

    public ChangeProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeProfileBinding.inflate(inflater, container, false);
        binding.changeProfileTopBar.backArrow.setOnClickListener(onBackArrowClicked);
        loadUsers();
        return binding.getRoot();
    }

    private void loadUsers() {
        ArrayList<User> usersList = (ArrayList<User>) ChatSQLiteDBHelper.getAllUsersFromDB(getContext());
        UsersAdapter usersAdapter = new UsersAdapter(getContext(), usersList);
        binding.usersList.setAdapter(usersAdapter);
    }

}