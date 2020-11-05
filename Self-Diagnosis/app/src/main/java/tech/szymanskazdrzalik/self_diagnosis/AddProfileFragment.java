package tech.szymanskazdrzalik.self_diagnosis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentAddProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.User;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProfileFragment extends Fragment {

    // TODO: 02.11.2020 - koniecznie przed prezentacją
    //  Menu po cofnięciu się w czacie z botem - Zmiana użytkownika, dodanie nowego użytkownika, historia leczenia użytkownika
    //  Dodać pokazowe przyciski do odpowiedzi do bota (nie powiązane z api, na rzecz prezentacji)

    // TODO: 02.11.2020 Mieszane odczucia co do kiedy
    //  Pierwsze uruchomienie aplikacj - utworzenie uzytkownika ewentualnie pokaz możliwości aplikacji

    // TODO: 02.11.2020 - raczej po prezentacji
    //  Baza danych - dodać tabelę z czatami, powiązane z id użytkownika
    //  Baza danych - zapisywać rozmowę - diagnoza, zapisujemy jednynie ukonczone diagnozy
    //  Interakcja z api
    //  Dodawanie zdj profilowego (dodać do bazy danych)


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private final Calendar myCalendar = Calendar.getInstance();
    private final View.OnClickListener addProfileImageListener = v -> openImagePicker();
    private ImageButton addProfileImage;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentAddProfileBinding binding;
    private boolean isNewUser = false;
    private String userName;
    private Date userBirthDate;
    private String userGender;
    private Bitmap userPicture;
    private final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };
    private final View.OnClickListener dateEditTextFragmentAddProfileOnClick =
            v -> new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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
    // TODO: Rename and change types and number of parameters
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

    private final View.OnClickListener genderFemaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = "F";
            binding.female.clearColorFilter();
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            binding.male.setColorFilter(filter);

        }
    };

    private final View.OnClickListener genderMaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = "M";
            binding.male.clearColorFilter();
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            binding.female.setColorFilter(filter);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddProfileBinding.inflate(inflater, container, false);
        binding.addUserImage.setOnClickListener(this.addProfileImageListener);
        binding.male.setOnClickListener(genderMaleOnClick);
        binding.female.setOnClickListener(genderFemaleOnClick);
        binding.dateEditTextFragmentAddProfile.setOnClickListener(this.dateEditTextFragmentAddProfileOnClick);
        binding.fgAddButton.setOnClickListener(addButtonOnClick);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.isNewUser = bundle.getBoolean("is_new_user");
        }
        return binding.getRoot();
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
            addProfileImage.setImageURI(selected);
        }
    }

    private void updateLabel() {
        binding.dateEditTextFragmentAddProfile.setText(SampleSQLiteDBHelper.DB_DATE_FORMAT.format(myCalendar.getTime()));
        this.userBirthDate = myCalendar.getTime();
    }

    View.OnClickListener addButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: 04.11.2020 ustawiać ID
            // TODO: 04.11.2020 sprawdzieć czy username jest pusty, czy gender byl ustawiony itp itd
            userName = binding.editProfileName.getText().toString();
            int id = 0;
            User user = new User(id, userName, userBirthDate, userGender, userPicture);
            // TODO: 04.11.2020 update database
            GlobalVariables.getInstance().setCurrentUser(user);
            // TODO: 04.11.2020 SWITCH to  getActivity().getFragmentManager().popBackStack(); (doesnt work for now)
            getActivity().onBackPressed();
        }
    };


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