package me.jiho.butterfly.auth;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.Callable;

import me.jiho.butterfly.MainActivity;
import me.jiho.butterfly.R;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.DialogUtil;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 1/9/15.
 */
public class SignInFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SignInFragment";

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            // Respond to session state changes, ex: updating the view
        }
    };


    private View loginForm;
    private View signinButtonset;

    private EditText emailInput;
    private EditText passwordInput;

    private ValueAnimator toEmailSignIn;
    private ValueAnimator toAuthMain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        createValueAnimators();

        /* create key hash
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "me.jiho.butterfly",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {

        }
        //*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        LoginButton facebookLoginButton = (LoginButton) rootView.findViewById(R.id.auth_btn_facebook_login);
        facebookLoginButton.setFragment(this);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        rootView.findViewById(R.id.auth_btn_sign_up).setOnClickListener(this);

        emailInput = (EditText) rootView.findViewById(R.id.auth_et_email);
        passwordInput = (EditText) rootView.findViewById(R.id.auth_et_password);
        rootView.findViewById(R.id.auth_btn_email_login).setOnClickListener(this);
        rootView.findViewById(R.id.auth_btn_submit).setOnClickListener(this);
        rootView.findViewById(R.id.auth_btn_cancel).setOnClickListener(this);

        loginForm = rootView.findViewById(R.id.auth_ll_login_form);
        signinButtonset = rootView.findViewById(R.id.auth_ll_login_buttons);

        return rootView;
    }



    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response

            final Dialog dialog = DialogUtil.getDefaultProgressDialog(getActivity());
            dialog.show();
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {

                            String fbId = user.getId();//facebook id
                            String name = user.getName();//user's profile name
                            String email = (String)user.asMap().get("email");

                            // do login
                            try {
                                JSONObject loginData = new JSONObject()
                                        .put(Auth.KEY_FB_ID, fbId)
                                        .put(Auth.KEY_NAME, name)
                                        .put(Auth.KEY_EMAIL, email);

                                Auth.getInstance()
                                        .login(
                                                loginData,
                                                new Callable() {
                                                    @Override
                                                    public Object call() throws Exception {

                                                        Intent intent = new Intent(
                                                                getActivity(),
                                                                MainActivity.class
                                                        );

                                                        intent.addFlags(
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                |Intent.FLAG_ACTIVITY_NEW_TASK
                                                        );

                                                        getActivity().startActivity(intent);
                                                        dialog.dismiss();
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
                                // do error handling
                                dialog.dismiss();
                                MessageUtil.showDefaultErrorMessage();
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });

            Request.executeBatchAsync(request);

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
//        Session session = Session.getActiveSession();
//        if (session != null &&
//                (session.isOpened() || session.isClosed()) ) {
//            onSessionStateChange(session, session.getState(), null);
//        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                /* not required..
            case R.id.auth_btn_facebook_login:
                Session session = Session.getActiveSession();
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(this)
                            .setPermissions(Arrays.asList("public_profile", "email"))
                            .setCallback(callback));
                } else {
                    Session.openActiveSession(getActivity(), this, true, callback);
                }
                break;
                //*/

            case R.id.auth_btn_sign_up:
                ((AuthActivity) getActivity()).setCurrentItem(AuthActivity.POSITION_SIGN_UP);
                break;
            case R.id.auth_btn_email_login:
                //signinButtonset.setVisibility(View.GONE);
                //loginForm.setVisibility(View.VISIBLE);
                toEmailSignIn.start();
                break;
            case R.id.auth_btn_cancel:
                //loginForm.setVisibility(View.GONE);
                //signinButtonset.setVisibility(View.VISIBLE);
                toAuthMain.start();
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

    private void createValueAnimators() {
        toEmailSignIn = ValueAnimator.ofFloat(0f, 1f);
        toEmailSignIn.setDuration(Constants.Integers.ANIMATION_DURATION * 2);
        toEmailSignIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                loginForm.setAlpha(progress < .5f ? 0f : (progress-.5f)*2);
                signinButtonset.setAlpha(progress > .5f ? 0f : 1f - (progress)*2);
            }
        });
        toEmailSignIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                loginForm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                signinButtonset.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        toAuthMain= ValueAnimator.ofFloat(0f, 1f);
        toAuthMain.setDuration(Constants.Integers.ANIMATION_DURATION * 2);
        toAuthMain.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                signinButtonset.setAlpha(progress < .5f ? 0f : (progress-.5f)*2);
                loginForm.setAlpha(progress > .5f ? 0f : 1f - (progress)*2);
            }
        });
        toAuthMain.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                signinButtonset.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loginForm.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
