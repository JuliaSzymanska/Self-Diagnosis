package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.Objects;

import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.databinding.WarningDialogBinding;

public class WarningDialog extends Dialog {

    private ConfirmationListener confirmationListener;
    private WarningDialogBinding binding;

    public WarningDialog(@NonNull Context context) {
        super(context);
    }


    public void setConfirmationListener(ConfirmationListener confirmationListener) {
        this.confirmationListener = confirmationListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = WarningDialogBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.binding.confirmButton.setOnClickListener(this::confirmButtonOnClick);
    }

    public void confirmButtonOnClick(View v) {
        this.confirmationListener.onUserAccept();
        this.dismiss();
    }

    public interface ConfirmationListener {
        void onUserAccept();
    }
}
