package co.bttrfly.auth;

import android.os.Bundle;

import co.bttrfly.BaseActivity;
import co.bttrfly.R;

public class SignUpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SignUpFragment())
                    .commit();
        }
    }


}
