package com.example.huanglisa.nightynight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huanglisa on 11/4/16.
 */

public class SessionManager {
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_BUILDINGS = "buildings";
    public static final String KEY_PASSWORD = "password";
    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String TAG = "SessionManager";
    //shared preferences
    SharedPreferences pref;
    //editor for shared preference
    SharedPreferences.Editor editor;
    //context
    Context _context;
    //shared pref mode
    int PRIVATE_MODE = 0;
    private UserApiInterface userApiInterface;
    private BuildingApiInterface buildingApiInterface;

    public SessionManager(Context context){
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);
        buildingApiInterface = ApiClient.getClient().create(BuildingApiInterface.class);
    }

    public String getToken(){
        return pref.getString(KEY_TOKEN, "");
    }

    //create loginNative session
    public void createLoginSession(String name, String email, String token, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_PASSWORD, password);
        //commit change
        editor.commit();
    }


    //create new loginNative page if not log in
    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        } else {

            //update token and get buildingList
            String input_email = pref.getString(KEY_EMAIL, null);
            String input_password = pref.getString(KEY_PASSWORD, null);
            Log.d(TAG, "start request token and buildingList， email:"+ input_email + " password:"+input_password);
            Call<User> call = userApiInterface.userLogIn(input_email, input_password);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(!response.isSuccessful()){
                        try{
                            Log.e(TAG, response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        } finally {
                            return;
                        }
                    }

                    Call<List<ReceivedBuilding>> buildingCall  = buildingApiInterface.getBuildings(response.body().token);
                    buildingCall.enqueue(new Callback<List<ReceivedBuilding>>() {
                        @Override
                        public void onResponse(Call<List<ReceivedBuilding>> call, Response<List<ReceivedBuilding>> response) {
                            if(!response.isSuccessful()){
                                Log.e(TAG, "failed to get building list");
                                try{
                                    Log.e(TAG, response.errorBody().string());
                                }catch(IOException e){
                                    Log.e(TAG, e.getMessage());
                                }
                                return;
                            }
                            addBuildingList(response.body());
                            System.out.format("building list size %d%n", response.body().size());
                        }

                        @Override
                        public void onFailure(Call<List<ReceivedBuilding>> call, Throwable t) {
                            Log.e(TAG, "failed to get building list (onFailure)");
                        }
                    });

                    createLoginSession(response.body().email, response.body().name, response.body().token, response.body().password);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        System.out.format("log out!!%n");
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for loginNative
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        System.out.format("check log in:%b%n", pref.getBoolean(IS_LOGIN, false));
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void addBuildingList(List<ReceivedBuilding> list){
        //Set<String> buildingList = new HashSet<>();
        String buildingList = "";

        for(ReceivedBuilding building : list){
            //buildingList.add(building.id);
            if(buildingList == ""){
                buildingList = buildingList + building.id+","+building.name+","+building.index;
            } else {
                buildingList = buildingList + "|" + building.id + "," + building.name+","+building.index;
            }
        }

        editor.putString(KEY_BUILDINGS, buildingList);
        System.out.format("list length: %d%n", list.size());
        System.out.format("just created buildingList %s%n", buildingList);
        editor.commit();

    }

    public void updateBuilding(ReceivedBuilding rb){
        String buildingString = pref.getString(KEY_BUILDINGS, null);
        buildingString = buildingString.concat("|" + rb.id + "," + rb.name + "," + rb.index);
        System.out.format("updated buildingString %s%n", buildingString);
        editor.putString(KEY_BUILDINGS, buildingString);
        editor.commit();
    }

    public List<ReceivedBuilding> getBuildingList(){
        List<ReceivedBuilding> idList = new ArrayList<>();
        String buildingString = pref.getString(KEY_BUILDINGS, null);
        if(buildingString == null){
            return idList;
        }
        String[] list = buildingString.split("\\|");
        for(String pair : list){
            String[] pairString = pair.split(",");
            if(pairString.length != 3){
                continue;
            }
            String id = pairString[0];
            String name =pairString[1];
            int index = Integer.parseInt(pairString[2]);
            ReceivedBuilding rb = new ReceivedBuilding(id, name, index, pref.getString(KEY_TOKEN, null));
            idList.add(rb);
        }
        return idList;

    }



}
