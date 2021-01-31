package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import tech.szymanskazdrzalik.self_diagnosis.R;
import tech.szymanskazdrzalik.self_diagnosis.databinding.WarningDialogBinding;

public class WarningDialog extends Dialog {

    private ConfirmationListener confirmationListener;
    private WarningDialogBinding binding;

    public WarningDialog(@NonNull Context context) {
        super(context, R.style.myDialog);
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
        this.binding.confirmButton.setOnClickListener(this::confirmButtonOnClick);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.warning_dialog);
//        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.dialog_layout_root);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int widthLcl = (int) (displayMetrics.widthPixels * 0.9f);
//        int heightLcl = (int) (displayMetrics.heightPixels * 0.9f);
//        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams)
//                relativeLayout.getLayoutParams();
//        paramsLcl.width = widthLcl;
//        paramsLcl.height = heightLcl;
//        paramsLcl.gravity = Gravity.CENTER;
//        show();
//        relativeLayout.setLayoutParams(paramsLcl);
    }

    public void confirmButtonOnClick(View v) {
        this.confirmationListener.onUserAccept();
        this.dismiss();
    }

    public interface ConfirmationListener {
        void onUserAccept();
    }
}
