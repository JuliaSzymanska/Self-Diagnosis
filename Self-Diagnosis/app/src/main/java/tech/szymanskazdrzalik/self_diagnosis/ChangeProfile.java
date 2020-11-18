package tech.szymanskazdrzalik.self_diagnosis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentChangeProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeProfile extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private FragmentChangeProfileBinding binding;
    private ListView listView;

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
        loadUsers();
        return binding.getRoot();
    }

    private void loadUsers() {
        List<User> usersList = SampleSQLiteDBHelper.getAllUsersFromDB(getContext());
        String[] names = new String[usersList.size()];
        for (int i = 0; i < usersList.size(); i++) {
            names[i] = usersList.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, names);
        binding.usersList.setAdapter(adapter);
    }
}