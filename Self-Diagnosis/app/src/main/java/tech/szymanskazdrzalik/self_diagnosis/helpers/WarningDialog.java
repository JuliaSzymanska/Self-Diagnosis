package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.binding = WarningDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
