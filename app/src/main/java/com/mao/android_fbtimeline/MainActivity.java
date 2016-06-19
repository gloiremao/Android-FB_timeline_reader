package com.mao.android_fbtimeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // callback manager for login process
    CallbackManager callbackManager;
    // access token may be used for other opera@ons axer login
    private AccessToken accessToken;
    private LoginButton FBLogin;
    private AccessTokenTracker accessTokenTracker;
    private Profile profile;
    private ProfileTracker profileTracker;
    private TextView welcome_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome_msg = (TextView) findViewById(R.id.textView);
        FBLogin = (LoginButton) findViewById(R.id.login_btn);

        //track accessToken
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                //API call for fb profile
                Profile.fetchProfileForCurrentAccessToken();
                accessToken = AccessToken.getCurrentAccessToken();
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile != null)welcome_msg.setText("You are now loging with:"+ currentProfile.getName());
                profile = currentProfile;
            }
        };

        accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null){
            Toast.makeText(getApplicationContext(), "Already Login" , Toast.LENGTH_LONG).show();
            if(profile != null)welcome_msg.setText("You are now loging with:"+ profile.getName());
            Log.d("FB", "Access token got:" + accessToken.getToken());
            Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
            intent.putExtra("token", accessToken);
            startActivity(intent);

        } else {
            List<String> permissions = new ArrayList<>();
            permissions.add("public_profile");
            permissions.add("user_posts");

            FBLogin.setReadPermissions(permissions);

            FBLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();
                    accessToken = loginResult.getAccessToken();
                    profile = Profile.getCurrentProfile();
                    Log.d("FB", "Access token got:" + accessToken.getToken());
                    Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
                    intent.putExtra("token", accessToken);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d("FB", "Cancel");
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.d("FB", "Fail" + exception.toString());
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
