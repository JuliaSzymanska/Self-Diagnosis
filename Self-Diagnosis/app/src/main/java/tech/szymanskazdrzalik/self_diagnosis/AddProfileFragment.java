package tech.szymanskazdrzalik.self_diagnosis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentAddProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;
import tech.szymanskazdrzalik.self_diagnosis.helpers.SharedPreferencesHelper;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class AddProfileFragment extends Fragment {

    // TODO: 02.11.2020 https://developer.android.com/topic/libraries/view-binding#java

    // TODO: 02.11.2020 - koniecznie przed prezentacją
    //  Dodać pokazowe przyciski do odpowiedzi do bota (nie powiązane z api, na rzecz prezentacji)

    // TODO: 02.11.2020 - raczej po prezentacji
    //  Baza danych - dodać tabelę z czatami, powiązane z id użytkownika
    //  Baza danych - zapisywać rozmowę - diagnoza, zapisujemy jednynie ukonczone diagnozy
    //  Interakcja z api
    //  zrobic pytanie o pozwolenie na dostep do danych przy wybieraniu obrazka
    //  wybór użytkownika - nowy fragment ze zdjeciami i nazwami urzytkownika

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private final Calendar myCalendar = Calendar.getInstance();
    private final View.OnClickListener addProfileImageListener = v -> openImagePicker();
    private String mParam1;
    private String mParam2;
    private FragmentAddProfileBinding binding;
    private boolean isNewUser = false;
    private String userName;
    private Date userBirthDate;
    private final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };
    private final View.OnClickListener dateEditTextFragmentAddProfileOnClick =
            v -> new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    private String userGender;
    private Bitmap userPicture;
    private static int nextAvailableId = 1000;
    GlobalVariables globalVariables;


    private final View.OnClickListener addButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userName = binding.editProfileName.getText().toString();
            if(areInputsEmpty()) return;
            User user = new User(nextAvailableId, userName, userBirthDate, userGender, userPicture);
            GlobalVariables.getInstance().setCurrentUser(user);
            if (isNewUser) {
                // TODO: 04.11.2020 sprawdzieć czy username jest pusty, czy gender byl ustawiony itp itd
                SampleSQLiteDBHelper.saveUserDataToDB(getContext(), user);
                SharedPreferencesHelper.saveUserId(getContext(), nextAvailableId);
                nextAvailableId += 1;
            } else {
                SampleSQLiteDBHelper.updateUserDataToDB(getContext(), user);
            }
            // TODO: 04.11.2020 SWITCH to  getActivity().getFragmentManager().popBackStack(); (doesnt work for now)
            if(mListener != null){
                mListener.reload();
            }
            getActivity().onBackPressed();
        }
    };

    private boolean areInputsEmpty(){
        System.out.println(userName + userBirthDate + userGender + userPicture);
        if(userName == null || userBirthDate == null || userGender == null || userPicture == null){
            Toast.makeText(getContext(), "Fill all the inputs", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public interface ReloadInterface{
        void reload();
    }
    private ReloadInterface mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ReloadInterface) context;
    }

    public AddProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProfile.
     */
    public static AddProfileFragment newInstance(String param1, String param2) {
        AddProfileFragment fragment = new AddProfileFragment();
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

    private ColorMatrixColorFilter getBlackAndWhiteFilter() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        return new ColorMatrixColorFilter(matrix);
    }

    private final View.OnClickListener genderFemaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = "F";
            binding.female.clearColorFilter();
            binding.male.setColorFilter(getBlackAndWhiteFilter());
        }
    };

    private final View.OnClickListener genderMaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = "M";
            binding.male.clearColorFilter();
            binding.female.setColorFilter(getBlackAndWhiteFilter());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProfileBinding.inflate(inflater, container, false);
        binding.addUserImage.setOnClickListener(this.addProfileImageListener);
        binding.male.setOnClickListener(genderMaleOnClick);
        binding.female.setOnClickListener(genderFemaleOnClick);
        binding.dateEditTextFragmentAddProfile.setOnClickListener(this.dateEditTextFragmentAddProfileOnClick);
        binding.fgAddButton.setOnClickListener(addButtonOnClick);
        Bundle bundle = this.getArguments();
        binding.female.setColorFilter(getBlackAndWhiteFilter());
        binding.male.setColorFilter(getBlackAndWhiteFilter());
        if (bundle != null) {
            this.isNewUser = bundle.getBoolean("is_new_user");
        }
        globalVariables = GlobalVariables.getInstance();
        if (!this.isNewUser) {
            setCurrentUser();
        }
        return binding.getRoot();
    }

    private void setCurrentUser() {
        if (globalVariables.getCurrentUser() != null) {
            userName = globalVariables.getCurrentUser().getName();
            binding.editProfileName.setText(userName);
            userBirthDate = globalVariables.getCurrentUser().getBirthDate();
            String birthString = new SimpleDateFormat("yyyy-MM-dd").format(userBirthDate);
            binding.dateEditTextFragmentAddProfile.setText(birthString);
            userGender = globalVariables.getCurrentUser().getGender();
            if (userGender.equals("M")) {
                binding.male.clearColorFilter();
            } else if (userGender.equals("F")) {
                binding.female.clearColorFilter();
            }
            binding.fgAddButton.setText(getString(R.string.update_string));
        }
    }

    private void openImagePicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri selected = Objects.requireNonNull(data).getData();
            try {
                userPicture = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selected);
            } catch (IOException e) {
                // TODO: 04.11.2020
                e.printStackTrace();
            }
            binding.addUserImage.setImageURI(selected);
            binding.beforeAddUserImage.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void updateLabel() {
        binding.dateEditTextFragmentAddProfile.setText(SampleSQLiteDBHelper.DB_DATE_FORMAT.format(myCalendar.getTime()));
        this.userBirthDate = myCalendar.getTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}