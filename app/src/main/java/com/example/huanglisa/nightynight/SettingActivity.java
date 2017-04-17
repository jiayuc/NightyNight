package com.example.huanglisa.nightynight;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private TextView name, status, address, phone;
    private String token;
    private Switch statusSwitch;
    private UserApiInterface userApiInterface;
    private ImageView editName, editPhone, editAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public String convertStatus(boolean isAwake) {
        statusSwitch.setChecked(isAwake);
        if (isAwake) {
            return "awake";
        } else {
            return "sleep";
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


}
