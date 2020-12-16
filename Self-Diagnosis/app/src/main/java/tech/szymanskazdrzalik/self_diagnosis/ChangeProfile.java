package tech.szymanskazdrzalik.self_diagnosis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentChangeProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.UsersAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeProfile extends Fragment {

    // TODO: 18.11.2020 Zanim się kliknie trzeba dodać nowego usera
    //  Nie działa dokładnie, nie pokazuja się wszyscy userzy
    //  Trzeba dodać przyciskiwalne

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final View.OnClickListener onBackArrowClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };
    private String mParam1;
    private String mParam2;
    private FragmentChangeProfileBinding binding;

    public ChangeProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeProfile newInstance(String param1, String param2) {
        ChangeProfile fragment = new ChangeProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        ArrayList<User> usersList = (ArrayList<User>) SampleSQLiteDBHelper.getAllUsersFromDB(getContext());
        UsersAdapter usersAdapter = new UsersAdapter(getContext(), usersList);
        binding.usersList.setAdapter(usersAdapter);
    }

}