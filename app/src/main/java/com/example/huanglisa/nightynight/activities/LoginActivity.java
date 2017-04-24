package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedBuilding;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.models.User;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login activity, extends the fragmentActivity
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    // constants
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String GOOGLE_CLIENT_ID = "803304289559-atpfnn6je36a6qv1q60boe1qds492j8m.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 1;
    SessionManager session;
    CallbackManager callbackManager;

    // ui component vars
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private UserApiInterface userApiInterface;
    private BuildingApiInterface buildingApiInterface;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState - bundle containing  saved instance stats
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //set theme, transition from splash page
        super.onCreate(savedInstanceState);
        // facebook login
        AppEventsLogger.activateApp(getApplication());
        // google login
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken(GOOGLE_CLIENT_ID) // pass the OAuth 2.0 client ID that was created for server
                .build();

        // must be after two fb lines
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);

        // API
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
                Log.d(TAG, "Front end fb Login success, " + loginResult.toString()); // + "\n\n token" + accessToken.getToken());
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

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // Edit google sign in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase token: " + firebaseToken);

    } // end of onCreate

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.print("onConnectionFailed: " + result.toString());
    }

    /**
     * Called after user sign up succeeds, login user with native login method
     */
    public void loginNative() {
        Log.d(TAG, "loginNative: user typed password");
        _loginButton.setEnabled(false);

        String input_email = _emailText.getText().toString();
        String input_password = _passwordText.getText().toString();

        Call<User> call = userApiInterface.userLogIn(input_email, input_password);
        onUserAPIResult(call);
    }

    /**
     * Handler when activity result is returned
     *
     * @param requestCode - code that represents action requested
     * @param resultCode  - code that represents result
     * @param data        - Intent that contains activity data
     */
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

        } else if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, data.toString());
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when user input password to login
     *
     * @param signupEmail user email used to sign in
     * @param signupName
     * @param token
     * @param password
     */
    public void loginNative(String signupEmail, String signupName, String token, String password) {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        //find existed account with signupEmail and password
        onLoginSucess(signupEmail, signupName, token, password);

    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess() + result.getStatus());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Google front-end login success, get info: " + acct.getDisplayName() + acct.getId());
            Call<User> call = userApiInterface.userLogInViaGoogle(acct.getId(), acct.getEmail(), acct.getDisplayName());
            onUserAPIResult(call);
        } else {
            Log.w(TAG, "google login failed" + result.getStatus().getStatusMessage());
            onLoginFail("google login failed: " + result.getStatus().getStatusMessage());
        }
    }

    //store user info
    public void onLoginSucess(String name, String email, String token, String password) {
        Log.d(TAG, "onLoginSucess: Rest API successfully return login token: " + token);
        _loginButton.setEnabled(true);
        //!email and name will be actually returned from server

        session.createLoginSession(name, email, token, password);

        setResult(RESULT_OK, null);
        finish();
    }

    private void onUserAPIResult(Call<User> call) {
        call.enqueue(new Callback<User > () {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "onResponse");
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to login, from userApiInterface");
                    try {
                        Log.e(TAG, response.toString());
                        onLoginFail(response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to login caused by exception, from userApiInterface");
                        onLoginFail(null);
                    } finally {
                        return;
                    }
                }
                System.out.format("token: %s%n", response.body().token);
                Call<List<ReceivedBuilding>> buildingCall = buildingApiInterface.getBuildings(response.body().token);
                buildingCall.enqueue(new Callback<List<ReceivedBuilding>> () {
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
                onLoginSucess(response.body().name, response.body().email, response.body().token, response.body().password);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onUserAPIResult: onFailure");
                Log.e(TAG, t.toString());
                onLoginFail(null);
            }
        });
    }

    /**
     * Called when activity view is clicked
     *
     * @param v view that's clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
//            TODO: not working and why
//            case R.id.button:
//                Log.d(TAG, "skip!!!");
//                Intent intent = new Intent(getApplicationContext(), ScreenSlidePagerActivity.class);
//                startActivity(intent);
//                break;
        }
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
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void jumpToSlider(View v) {
        Intent intent = new Intent(getApplicationContext(), ScreenSlidePagerActivity.class);
        startActivity(intent);
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }








}