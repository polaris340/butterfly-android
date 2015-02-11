package me.jiho.butterfly.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import me.jiho.butterfly.MainActivity;
import me.jiho.butterfly.R;
import me.jiho.butterfly.util.DialogUtil;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 1/9/15.
 */
public class SignInFragment extends Fragment implements View.OnClickListener{

    private EditText emailInput;
    private EditText passwordInput;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);


        emailInput = (EditText) rootView.findViewById(R.id.auth_et_email);
        passwordInput = (EditText) rootView.findViewById(R.id.auth_et_password);
        rootView.findViewById(R.id.auth_btn_submit).setOnClickListener(this);
        rootView.findViewById(R.id.auth_btn_cancel).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auth_btn_sign_up:
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.auth_btn_cancel:
                ((AuthActivity) getActivity()).setCurrentItem(0);
                break;
            case R.id.auth_btn_submit:
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email.length() == 0) {
                    emailInput.requestFocus();
                    return;
                }
                if (password.length() == 0) {
                    passwordInput.requestFocus();
                    return;
                }
                try {
                    final Dialog dialog = DialogUtil.getDefaultProgressDialog(getActivity());
                    dialog.show();
                    JSONObject jsonObject = new JSONObject()
                            .put(Auth.KEY_EMAIL, email)
                            .put(Auth.KEY_PASSWORD, password);
                    Auth.getInstance().login(
                            jsonObject,
                            new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    |Intent.FLAG_ACTIVITY_NEW_TASK
                                    );
                                    dialog.dismiss();
                                    getActivity().startActivity(intent);
                                    return null;
                                }
                            },
                            new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    dialog.dismiss();
                                    return null;
                                }
                            }

                    );
                } catch (JSONException e) {
                    MessageUtil.showDefaultErrorMessage();
                    e.printStackTrace();
                    return;
                }


                break;


        }
    }
}
