package com.example.huanglisa.nightynight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText _emailText;
    private EditText _nameText;
    private EditText _addressText;
    private EditText _mobileText;
    private EditText _passwordText;
    private EditText _reEnterPasswordText;
    private TextView _loginLink;
    private Button _signupButton;
    private UserApiInterface userApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //API
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);

        _emailText = (EditText)findViewById(R.id.input_email);
        _nameText = (EditText)findViewById(R.id.input_name);
        _addressText = (EditText)findViewById(R.id.input_address);
        _mobileText = (EditText)findViewById(R.id.input_mobile);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText)findViewById(R.id.input_reEnterPassword);
        _loginLink = (TextView)findViewById(R.id.link_login);
        _signupButton = (Button)findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");


        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String address = _addressText.getText().toString();
        String phone = _mobileText.getText().toString();
        String name = _nameText.getText().toString();

        if(!checkPasswordCorrectness(password, reEnterPassword)){
            return;
        }
        User user = new User(email, password, name, address, phone, true);
        Call<User> call = userApiInterface.userSignUp(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    try{
                        onSignupFailed(response.errorBody().string());
                    } catch (Exception e){
                        onSignupFailed(null);
                    } finally {
                        return;
                    }
                }

                onSignupSuccess(response.body().email, response.body().name, response.body().token, response.body().password);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.toString());
                onSignupFailed(null);
            }
        });


    }

    public boolean checkPasswordCorrectness(String ps, String reEnterPs){
        System.out.format("%s,%s%n", ps, reEnterPs);
        if(ps.equals(reEnterPs)){
            return true;
        }

        Toast.makeText(getBaseContext(), "reentered password is not the same as the first enter", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        return false;
    }

    public void onSignupSuccess(String email, String name, String token, String password) {
        _signupButton.setEnabled(true);

        //!should be return from server
        Intent data = new Intent();
        data.putExtra("email", email);
        data.putExtra("name", name);
        data.putExtra("token", token);
        data.putExtra("password", password);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onSignupFailed(String message) {
        String text = "signup failed";
        if(message != null){
            text = message;
        }
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }
}
