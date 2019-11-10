package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessagesApprovalRequest;
import com.VegaSolutions.lpptransit.travanaserver.Objects.UserData;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private TextView c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18,
    c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35, c36;

    private FirebaseAuth mAuth;

    private GoogleSignInOptions gso;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    CallbackManager mCallbackManager;

    private static String uidToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("settings", MODE_PRIVATE);
        boolean dark_theme = sharedPreferences.getBoolean("app_theme", false);
        setTheme(dark_theme ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_sign_in);

        getwarnings();

        createCircleAnimation();
        ImageButton back_btn = (ImageButton)findViewById(R.id.sign_in_activity_back_btn);
        back_btn.setOnClickListener(e1 -> {
            finish();
        });

        TextView help_contact_btn = (TextView) findViewById(R.id.textView5);
        help_contact_btn.setOnClickListener(e2 ->{

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", this.getString(R.string.developer_help_contact), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        });

        TextView help_contact_btn1 = (TextView) findViewById(R.id.textView4);
        help_contact_btn.setOnClickListener(e3 ->{

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", this.getString(R.string.developer_help_contact), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        });

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        Button google_sing_in_btn = (Button) findViewById(R.id.google_btn);
        google_sing_in_btn.setOnClickListener(e5 -> {
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            signInWithGoogle();
        });

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton fb_sign_in_btn = (LoginButton) findViewById(R.id.facebook_btn1);
        fb_sign_in_btn.setPermissions("email", "public_profile");
        fb_sign_in_btn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getApplicationContext(), getString(R.string.you_canceled_the_proccess), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        });

        Button fb_sign_in_btn1 = (Button)findViewById(R.id.facebook_btn);
        fb_sign_in_btn1.setOnClickListener(e6 -> {
            fb_sign_in_btn.performClick();
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            Toast.makeText(getApplicationContext(),  this.getString(R.string.you_are_already_logined), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createCircleAnimation();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Google sign in
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        }else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);                       //Facebook sign in
        }
    }

    //Sign in firebase with google account.

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), getString(R.string.you_have_successfuly_logged_in), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //Handle Facebook access Token.

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(getApplicationContext(), getString(R.string.you_have_successfuly_logged_in), Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    //after updates user is added in database by server

    private void addTravanaUser(){

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();

                            uidToken = idToken;

                            UserData user = new UserData(mUser.getUid(), mUser.getDisplayName(), mUser.getEmail(), mUser.getPhotoUrl().toString());

                            RequestBody rbody = RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(user));

                            TravanaAPI.addUser(idToken, rbody, (data, statusCode, success) ->{

                                if(success){
                                    Log.e(TAG, data + "");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }
                            });

                        } else {

                            Log.e(TAG, task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                            mAuth.signOut();

                        }
                    }
                });
    }

    private void getwarnings(){

        /*
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();

                            TravanaAPI.warnings(idToken, (data, statusCode, success) ->{

                                if(success){
                                    Log.e(TAG, data + "");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }
                            });

                        } else {

                            Log.e(TAG, task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                            mAuth.signOut();

                        }
                    }
                });
                */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String[] tags = {"bus", "lpp", "3g"};

        LiveUpdateMessage message = new LiveUpdateMessage(user.getDisplayName(), new Date(), "spremenjena5555", tags, 10);

        System.out.println(message);

        LiveUpdateComment comment = new LiveUpdateComment(user.getDisplayName(), "vsebinavTHEKING50444444444");

        /*
        TravanaAPI.editComment("token","mess_genVegaSolutionsSunNov1018:46:15GMT01:002019", "comm_genVegaSolutionsSunNov1019:04:20GMT01:002019", comment, (data, statusCode, success) -> {

            if(success){
                Log.e(TAG, data + "");
            }else{
                Log.e(TAG, "error" + statusCode);
            }
        });

         */

        ArrayList<MessagesApprovalRequest> list = new ArrayList<>();
        list.add(new MessagesApprovalRequest("mess_genVegaSolutionsSunNov1018:46:15GMT01:002019", false));

        TravanaAPI.approveMessages("token",list, (data, statusCode, success) -> {

            if(success){
                Log.e(TAG, data + "");
            }else{
                Log.e(TAG, "error" + statusCode);
            }
        });

        /*
        TravanaAPI.removeComment("token","mess_genVegaSolutionsSunNov1018:46:15GMT01:002019", "comm_genVegaSolutionsSunNov1019:02:33GMT01:002019", (data, statusCode, success) -> {

            if(success){
                Log.e(TAG, data + "");
            }else{
                Log.e(TAG, "error" + statusCode);
            }
        });
        */
        /*
        LiveUpdateComment comment = new LiveUpdateComment(user.getDisplayName(), "vsebinavKRALJ");

        TravanaAPI.addComment("token","mess_genVegaSolutionsSunNov1018:46:15GMT01:002019", comment ,(data, statusCode, success) -> {

            if(success){
                Log.e(TAG, data + "");
            }else{
                Log.e(TAG, "error" + statusCode);
            }
        });
        */
        /*
        TravanaAPI.addMessage("token", message, (data, statusCode, success) -> {

            if(success){
                Log.e(TAG, data + "");
            }else{
                Log.e(TAG, "error" + statusCode);
            }
        });
        */
    }

    //--------------------------------------Animations

    private void initializeCircleTextViews(){

        c1 = findViewById(R.id.circle1);
        c2 = findViewById(R.id.textView3);
        c3 = findViewById(R.id.textView9);
        c4 = findViewById(R.id.textView10);
        c5 = findViewById(R.id.textView11);
        c6 = findViewById(R.id.textView12);
        c7 = findViewById(R.id.textView13);
        c8 = findViewById(R.id.textView14);
        c9 = findViewById(R.id.textView15);
        c10 = findViewById(R.id.textView16);
        c11 = findViewById(R.id.textView20);
        c12 = findViewById(R.id.textView21);
        c13 = findViewById(R.id.textView22);
        c14 = findViewById(R.id.textView23);
        c15 = findViewById(R.id.textView24);
        c16 = findViewById(R.id.textView25);
        c17 = findViewById(R.id.textView26);
        c18 = findViewById(R.id.textView19);

        c19 = findViewById(R.id.textView27);
        c20 = findViewById(R.id.textView28);
        c21 = findViewById(R.id.textView29);
        c22 = findViewById(R.id.textView30);
        c23 = findViewById(R.id.textView31);
        c24 = findViewById(R.id.textView32);
        c25 = findViewById(R.id.textView33);
        c26 = findViewById(R.id.textView34);
        c27 = findViewById(R.id.textView35);
        c28 = findViewById(R.id.textView36);
        c29 = findViewById(R.id.textView37);
        c30 = findViewById(R.id.textView38);
        c31 = findViewById(R.id.textView39);
        c32 = findViewById(R.id.textView40);
        c33 = findViewById(R.id.textView45);
        c34 = findViewById(R.id.textView46);
        c35 = findViewById(R.id.textView47);
        c36 = findViewById(R.id.textView48);
    }

    private void createCircleAnimation(){

        if(c1 == null){
            initializeCircleTextViews();
        }

        c1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c3.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c4.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c5.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c6.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c7.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c8.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c9.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c10.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c11.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c12.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c13.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c14.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c15.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c16.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c17.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c18.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));

        c19.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c20.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c21.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c22.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c23.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c24.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c25.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c26.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c27.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c28.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c29.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c30.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c31.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c32.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c33.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
        c34.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
        c35.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
        c36.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));

    }
}
