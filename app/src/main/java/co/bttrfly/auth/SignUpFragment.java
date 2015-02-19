package co.bttrfly.auth;

import android.app.Dialog;
import android.content.Intent;
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

import co.bttrfly.MainActivity;
import co.bttrfly.R;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.util.InputUtil;
import co.bttrfly.util.MessageUtil;

/**
 * Created by jiho on 1/9/15.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener{

    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailInput = (EditText) rootView.findViewById(R.id.signup_et_email);
        passwordInput = (EditText) rootView.findViewById(R.id.signup_et_password);
        passwordConfirmInput = (EditText) rootView.findViewById(R.id.signup_et_password_confirm);
        ImageButton submitButton = (ImageButton) rootView.findViewById(R.id.signup_btn_submit);
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
        Dialog dialog = DialogUtil.getDefaultProgressDialog(getActivity());
        dialog.show();
        try {
            Auth.getInstance().signup(
                email,
                password,
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                |Intent.FLAG_ACTIVITY_NEW_TASK
                        );
                        startActivity(intent);
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
