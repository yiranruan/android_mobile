package com.example.mobileproject.login.data;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.mobileproject.MainActivity;
import com.example.mobileproject.group.ShowGroupActivity;
import com.example.mobileproject.login.data.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource{
    String userID;
    String token;

    LoggedInUser User;

    public Result<LoggedInUser> login(String userID, String token){
        this.userID = userID;
        Log.d("fake11", "userID: "+userID);
        this.token = token;
        Log.d("fake11", "token: "+token);
        User = new LoggedInUser(userID, token);
        Log.d("fake11", "call: "+User.getUserId());
        return new Result.Success<>(User);
//        return new Result.Error(new IOException("Error logging in", e));
    }

    public void logout() {
        // TODO: revoke authentication
    }

//    @Override
//    public Result<LoggedInUser> call(){
//        try {
//            Log.d("login_r", "login_userID: "+userID);
//            Log.d("login_r", "login_token: "+token);
//            // TODO: handle loggedInUser authentication
//
//            fakeUser = new LoggedInUser(userID, token);
////            return new Result.Success<>(fakeUser);
//        } catch (Exception e) {
//            return new Result.Error(new IOException("Error logging in", e));
//        }
//        Log.d("fake", "call: "+fakeUser.getUserId());
//        Log.d("fake", "call: "+fakeUser.getUserId());
//        return new Result.Success<>(fakeUser);
////        return null;
//    }
}
