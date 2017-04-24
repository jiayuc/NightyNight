package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedBuilding;
import com.example.huanglisa.nightynight.models.User;
import com.example.huanglisa.nightynight.dialogs.UserInfoEditingDialog;
import com.example.huanglisa.nightynight.rest.ApiClient;
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

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SettingActivity";
    // constants for google login in
    private static final String GOOGLE_CLIENT_ID = "803304289559-atpfnn6je36a6qv1q60boe1qds492j8m.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 1;
    public boolean[] signInButtonsClicked = {false, false};
    CallbackManager callbackManager;
    private TextView name, status, address, phone;
    private String token;
    private Switch statusSwitch;
    private UserApiInterface userApiInterface;
    private ImageView editName, editPhone, editAddress;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButtonGoogle;

    /**
     * On create setting Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.activity_setting);

        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);

        editName = (ImageView) findViewById(R.id.editName);
        editPhone = (ImageView) findViewById(R.id.editPhone);
        editAddress = (ImageView) findViewById(R.id.editAddress);

        name = (TextView) findViewById(R.id.userName);
        status = (TextView) findViewById(R.id.userStatus);
        address = (TextView) findViewById(R.id.userAddress);
        phone = (TextView) findViewById(R.id.userPhone);
        statusSwitch = (Switch) findViewById(R.id.statusSwitch);
        Intent intent = getIntent();
        token = intent.getExtras().getString("token");
        getUserInfo(token);


        editName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, " click name edit");
                generateEditingDialog("name");
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, " click phone edit");
                generateEditingDialog("phone");
            }
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, " click address edit");
                generateEditingDialog("address");
            }
        });


        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeUserStatus(isChecked);
            }
        });

        /** START: register onclick for social media login buttons **/
        LoginButton loginButtonFB = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButtonFB.setReadPermissions("email");
        // register facebook loginNative cb
        callbackManager = CallbackManager.Factory.create();
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                signInButtonsClicked[1] = true;
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
        signInButtonGoogle = (SignInButton) findViewById(R.id.google_signin_button);
        signInButtonGoogle.setSize(SignInButton.SIZE_WIDE);
        signInButtonGoogle.setOnClickListener(this);
        /** END: register onclick for social media login buttons **/

        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
    }


    public void getUserInfo(String token) {
        Call<User> call = userApiInterface.getUserInfo(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    } finally {
                        return;
                    }
                }
                name.setText(response.body().name);
                status.setText(convertStatus(response.body().status));
                address.setText(response.body().address);
                phone.setText(response.body().phone);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void generateEditingDialog(String type) {
        DialogFragment dialog = UserInfoEditingDialog.newInstance(type);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void changeUserStatus(boolean curStatus) {
        Call<User> call = userApiInterface.userUpdateStatus(token, curStatus);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to change user status");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }
                Log.d(TAG, "get user new status: " + response.body().status);
                status.setText(convertStatus(response.body().status));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void onUserAPIResult(Call<User> call) {
        call.enqueue(new Callback<User>() {
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
                onLoginSucess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onUserAPIResult: onFailure");
                Log.e(TAG, t.toString());
                onLoginFail(null);
            }
        });
    }

    public String convertStatus(boolean isAwake) {
        statusSwitch.setChecked(isAwake);
        if (isAwake) {
            return "awake";
        } else {
            return "sleep";
        }
    }

    private void onLoginFail(String string) {
        Log.e(TAG, "failed to link to social media " + string);
        Toast.makeText(getBaseContext(), "failed to link to social media", Toast.LENGTH_LONG).show();
    }

    /**
     * Called when login succeeds
     */
    private void onLoginSucess() {
        if (signInButtonsClicked[0] == true) {
            setGooglePlusButtonText(signInButtonGoogle, "Associated with google");
            Toast.makeText(getBaseContext(), "added google account", Toast.LENGTH_LONG).show();
            signInButtonsClicked[0] = false;
        } else if (signInButtonsClicked[1] == true) {
//            setGooglePlusButtonText(signInButtonGoogle, "Associated with google");
            Toast.makeText(getBaseContext(), "added fb account", Toast.LENGTH_LONG).show();
            signInButtonsClicked[1] = false;
        }
    }

    /**
     * Modify text on google SigninButton
     *
     * @param signInButton Google SigninButton instance
     * @param buttonText   text to set for the button
     */
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void onUserInfoEditingDialogPositiveClick(String type, String value) {
        switch (type) {
            case "name":
                changeName(value);
                break;
            case "phone":
                changePhone(value);
                break;
            case "address":
                changeAddress(value);
                break;
        }

    }

    public void changeName(String curName) {
        Call<User> call = userApiInterface.userUpdateName(token, curName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to change user name");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }
                Log.d(TAG, "get user new name: " + response.body().name);
                name.setText(response.body().name);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_signin_button:
                signInButtonsClicked[0] = true;
                googleSignIn();
                break;
        }
    }

    public void changePhone(String curPhone) {
        Call<User> call = userApiInterface.userUpdatePhone(token, curPhone);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to change user phone");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }
                Log.d(TAG, "get user new phone: " + response.body().phone);
                phone.setText(response.body().phone);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void changeAddress(String curAddress) {
        Call<User> call = userApiInterface.userUpdateAddress(token, curAddress);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to change user address");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }
                Log.d(TAG, "get user new address: " + response.body().address);
                address.setText(response.body().address);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GGLogin", connectionResult.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, data.toString());
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
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





}

