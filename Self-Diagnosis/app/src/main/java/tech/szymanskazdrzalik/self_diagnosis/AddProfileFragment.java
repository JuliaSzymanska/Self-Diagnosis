package tech.szymanskazdrzalik.self_diagnosis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import tech.szymanskazdrzalik.self_diagnosis.databinding.FragmentAddProfileBinding;
import tech.szymanskazdrzalik.self_diagnosis.db.ChatSQLiteDBHelper;
import tech.szymanskazdrzalik.self_diagnosis.db.DbBitmapUtility;
import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;
import tech.szymanskazdrzalik.self_diagnosis.helpers.SharedPreferencesHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProfileFragment extends Fragment {
    // TODO: 16.01.2021
    //  zrobic pytanie o pozwolenie na dostep do danych przy wybieraniu obrazka

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final long DEFAULT_DATE = System.currentTimeMillis() - 410280000000L;
    private final Calendar myCalendar = Calendar.getInstance();
    private final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        setCalendarDate(year, monthOfYear, dayOfMonth);
        updateLabel();
    };
    private DatePickerDialog datePickerDialog;
    private final View.OnClickListener dateEditTextFragmentAddProfileOnClick =
            v -> datePickerDialog.show();
    GlobalVariables globalVariables = GlobalVariables.getInstance();
    private FragmentAddProfileBinding binding;
    private boolean isNewUser = false;
    private String userGender;
    private Bitmap userPicture;
    private final View.OnClickListener genderFemaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = getString(R.string.female_sign);
            binding.female.clearColorFilter();
            binding.male.setColorFilter(getBlackAndWhiteFilter());
            userPicture = getDefaultImage(userGender);
            binding.addUserImage.setImageBitmap(userPicture);
            binding.beforeAddUserImage.setBackgroundColor(Color.TRANSPARENT);
        }
    };
    private final View.OnClickListener genderMaleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userGender = getString(R.string.male_sign);
            binding.male.clearColorFilter();
            binding.female.setColorFilter(getBlackAndWhiteFilter());
            userPicture = getDefaultImage(userGender);
            binding.addUserImage.setImageBitmap(userPicture);
            binding.beforeAddUserImage.setBackgroundColor(Color.TRANSPARENT);
        }
    };
    private AddProfileFragmentListener mListener;
    private final View.OnClickListener addButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (areInputsEmpty()) {
                return;
            }

            String userName = binding.editProfileName.getText().toString();
            Date userBirthDate = myCalendar.getTime();

            int currentID;

            if (isNewUser) {
                currentID = ChatSQLiteDBHelper.getNextUserIdAvailable(getContext());
            } else {
                currentID = globalVariables.getCurrentUser().get().getId();
            }

            User user = new User(currentID, userName, userBirthDate, userGender, userPicture);
            GlobalVariables.getInstance().setCurrentUser(user);
            ChatSQLiteDBHelper.saveUserDataToDB(getContext(), user);
            SharedPreferencesHelper.saveUserId(getContext(), currentID);

            // TODO: 04.11.2020 SWITCH to  getActivity().getFragmentManager().popBackStack(); (doesnt work for now)
            if (mListener != null) {
                mListener.callback(getString(R.string.reload));
            }

            System.out.println(user.getAge());
            getActivity().onBackPressed();
        }
    };

    public AddProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddProfile.
     */
    public static AddProfileFragment newInstance() {
        AddProfileFragment fragment = new AddProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setCalendarDate(int year, int monthOfYear, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    private boolean areInputsEmpty() {
        String userName = binding.editProfileName.getText().toString();
        Date userBirthDate = myCalendar.getTime();
        if (userName == null || userBirthDate == null || userGender == null) {
            Toast.makeText(getContext(), getString(R.string.fill_all_inputs), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private Bitmap getDefaultImage(String gender) {
        if (gender.equals(getString(R.string.female_sign))) {
            return DbBitmapUtility.getBitmapFromDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.female));
        }
        return DbBitmapUtility.getBitmapFromDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.male));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddProfileFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ColorMatrixColorFilter getBlackAndWhiteFilter() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        return new ColorMatrixColorFilter(matrix);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProfileBinding.inflate(inflater, container, false);
        binding.female.setColorFilter(getBlackAndWhiteFilter());
        binding.male.setColorFilter(getBlackAndWhiteFilter());

        getArgumentsFromBundle();


        if (!this.isNewUser) {
            setInputsToCurrentUser();
        }

        setListeners();
        datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerDialogStyle, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(DEFAULT_DATE);

        return binding.getRoot();
    }

    private void setListeners() {
        binding.male.setOnClickListener(genderMaleOnClick);
        binding.female.setOnClickListener(genderFemaleOnClick);
        binding.dateEditTextFragmentAddProfile.setOnClickListener(this.dateEditTextFragmentAddProfileOnClick);
        binding.fgAddButton.setOnClickListener(addButtonOnClick);
    }

    private void getArgumentsFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.isNewUser = bundle.getBoolean(getString(R.string.is_new_user));
            if (!globalVariables.getCurrentUser().isPresent()) {
                this.isNewUser = true;
            }
        }
    }

    private void setInputsToCurrentUser() {
        if (globalVariables.getCurrentUser().isPresent()) {
            setFieldsFromGlobalVariable();

            binding.editProfileName.setText(globalVariables.getCurrentUser().get().getName());


            this.updateLabel();

            if (userGender.equals(getString(R.string.male_sign))) {
                binding.male.clearColorFilter();
            } else if (userGender.equals(getString(R.string.female_sign))) {
                binding.female.clearColorFilter();
            }
            binding.addUserImage.setImageBitmap(globalVariables.getCurrentUser().get().getPicture());
            binding.beforeAddUserImage.setBackgroundColor(Color.TRANSPARENT);
            binding.fgAddButton.setText(getString(R.string.update_string));
        }
    }

    private void setFieldsFromGlobalVariable() {
        // TODO: 16.12.2020 Test
        if (!globalVariables.getCurrentUser().isPresent()) {
            return;
        }
        this.userGender = globalVariables.getCurrentUser().get().getGender();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(globalVariables.getCurrentUser().get().getBirthDate());
        setCalendarDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void updateLabel() {
        binding.dateEditTextFragmentAddProfile.setText(ChatSQLiteDBHelper.DB_DATE_USER_FORMAT.format(myCalendar.getTime()));
    }

    public interface AddProfileFragmentListener {
        void callback(String result);
    }
}