package me.jiho.butterfly.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;

import java.util.concurrent.Callable;

import me.jiho.butterfly.R;
import me.jiho.butterfly.util.DialogManager;
import me.jiho.butterfly.util.InputUtil;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 1/9/15.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener{

    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private ImageButton submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailInput = (EditText) rootView.findViewById(R.id.signup_et_email);
        passwordInput = (EditText) rootView.findViewById(R.id.signup_et_password);
        passwordConfirmInput = (EditText) rootView.findViewById(R.id.signup_et_password_confirm);
        submitButton = (ImageButton) rootView.findViewById(R.id.signup_btn_submit);
        submitButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordConfirm = passwordConfirmInput.getText().toString();
        if (!InputUtil.isValidEmail(email)) {
            MessageUtil.showMessage(R.string.error_email_format);
            emailInput.requestFocus();
            return;
        }

        if (!InputUtil.isValidPasswordLength(password)) {
            MessageUtil.showMessage(
                    String.format(getString(R.string.error_password_length),
                            InputUtil.MIN_PASSWORD_LENGTH,
                            InputUtil.MAX_PASSWORD_LENGTH)
            );
            passwordInput.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            MessageUtil.showMessage(R.string.error_password_confirm);
            passwordConfirmInput.requestFocus();
            return;
        }

        // do login
        Dialog dialog = DialogManager.getDefaultProgressDialog(getActivity());
        dialog.show();
        try {
            Auth.getInstance().signup(
                email,
                password,
                new Callable() {
                    @Override
                    public Object call() throws Exception {

                        return null;
                    }
                },
                dialog);
        } catch (JSONException e) {
            MessageUtil.showDefaultErrorMessage();
            dialog.dismiss();
            e.printStackTrace();
        }

    }

}
