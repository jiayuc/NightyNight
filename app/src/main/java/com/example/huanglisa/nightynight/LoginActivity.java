package com.example.huanglisa.nightynight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SessionManager session;
    CallbackManager callbackManager;
    //ui component
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private UserApiInterface userApiInterface;
    private BuildingApiInterface buildingApiInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //set theme, transition from splash page
        super.onCreate(savedInstanceState);
        // facebook loginNative, logging
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        // must be after two fb lines
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);

        //API
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);
        buildingApiInterface = ApiClient.getClient().create(BuildingApiInterface.class);

        // Session Manager for user management
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginNative();
            }
        });

        //when user click signup, go to signup page
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        // register facebook loginNative cb
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButtonFB = (LoginButton) findViewById(R.id.login_button);
        loginButtonFB.setReadPermissions("email");
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Log.d(TAG, "Login success!!" + loginResult.toString()); // + "\n\n token" + accessToken.getToken());
                // request user access from API
                Call<User> call = userApiInterface.userLogInViaFacebook(accessToken.getUserId(), accessToken.getToken());
                onUserAPIResult(call);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(LoginActivity.class.getCanonicalName(), error.getMessage());
            }
        });
    }

    //log in when user text input
    public void loginNative() {
        Log.d(TAG, "loginNative: user typed password");
        _loginButton.setEnabled(false);

        String input_email = _emailText.getText().toString();
        String input_password = _passwordText.getText().toString();

        Call<User> call = userApiInterface.userLogIn(input_email, input_password);
        onUserAPIResult(call);
    }


    private void onUserAPIResult(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "onResponse");
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to loginNative, from userApiInterface");
                    try {
                        System.out.print(response);
                        onLoginFail(response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to loginNative: exception, from userApiInterface");
                        onLoginFail(null);
                    } finally {
                        return;
                    }
                }
                System.out.format("token: %s%n", response.body().token);
                Call<List<ReceivedBuilding>> buildingCall = buildingApiInterface.getBuildings(response.body().token);
                buildingCall.enqueue(new Callback<List<ReceivedBuilding>>() {
                    @Override
                    public void onResponse(Call<List<ReceivedBuilding>> call, Response<List<ReceivedBuilding>> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "failed to get building list");
                            try {
                                Log.e(TAG, response.errorBody().string());
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            return;
                        }
                        session.addBuildingList(response.body());
                        System.out.format("building list size %d%n", response.body().size());
                    }

                    @Override
                    public void onFailure(Call<List<ReceivedBuilding>> call, Throwable t) {
                        Log.e(TAG, "failed to get building list (onFailure)");
                        System.out.format(t.getMessage());
                    }
                });
                onLoginSucess(response.body().email, response.body().name, response.body().token, response.body().password);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onUserAPIResult: onFailure");
                Log.e(TAG, t.toString());
                onLoginFail(null);
            }
        });        
    }

    //used when email and password input
    //e.g. run after sign up
    public void loginNative(String signupEmail, String signupName, String token, String password) {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        //find existed account with signupEmail and password
        onLoginSucess(signupEmail, signupName, token, password);

    }

    //store user info
    public void onLoginSucess(String name, String email, String token, String password) {
        System.out.print("Rest API successfully return login token: " + token);
        _loginButton.setEnabled(true);
        //!email and name will be actually returned from server

        session.createLoginSession(name, email, token, password);

        setResult(RESULT_OK, null);
        finish();
    }

    //indicate user log in failed
    public void onLoginFail(String message) {
        Log.i(TAG, "onLoginFail: " + message);
        String text = "Login failed";
        if (message != null) {
            text = message;
        }
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //catch user info when sign up
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                loginNative(data.getExtras().getString("email"), data.getExtras().getString("password"), data.getExtras().getString("token"), data.getExtras().getString("password"));
            }
            if (resultCode == RESULT_CANCELED) {
                System.out.format("get here");
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

}
