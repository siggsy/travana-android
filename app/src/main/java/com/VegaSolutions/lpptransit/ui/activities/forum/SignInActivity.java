package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.TagsBox;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
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

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private View[] views = new View[36];

    private FirebaseAuth mAuth;

    private GoogleSignInOptions gso;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    CallbackManager mCallbackManager;

    private static String uidToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
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

    //Sign in firebase with google navbar_btn.

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

    private void getwarnings(){


        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;


        /*
        TravanaAPI.messages(new TravanaApiCallback<LiveUpdateMessage[]>() {
            @Override
            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                Log.e(TAG, Arrays.toString(apiResponse));

            }
        });

         */


        /*
        List<String> list = new ArrayList<>();

        list.add("E6EA4090-33CB-4772-9611-FD62585945CF");
        list.add("1F481B30-0C45-4A6D-9224-7407F6467AD9");

        TravanaAPI.calculatedBusesInfo(list, new TravanaApiCallback<CalBusInfo[]>() {
            @Override
            public void onComplete(@Nullable CalBusInfo[] apiResponse, int statusCode, boolean success) {

                if(success){
                    Log.e(TAG, Arrays.toString(apiResponse));
                }else{
                    Log.e(TAG, "error" + statusCode);
                }

            }
        });

         */


        /*
        TravanaAPI.tags((data, statusCode, success) -> {
            if(success){

                System.out.println(data);

            }else{
                Log.e(TAG, "error" + statusCode);
            }

        })

         */

        FirebaseManager.getFirebaseToken((token, statusCode, success) -> {
                    if(success){




                        TravanaAPI.tags(token, new TravanaApiCallback<TagsBox>() {
                            @Override
                            public void onComplete(@Nullable TagsBox apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, apiResponse + "");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }


                            }
                        });





                        TravanaAPI.followTag(token, "Main#121111", new TravanaApiCallback<String>() {
                            @Override
                            public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, apiResponse + "");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }

                            }
                        });



                        /*
                        TravanaAPI.followedMessagesMeta(token, new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, Arrays.toString(apiResponse));
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }


                            }
                        });

                         */

                        /*
                        MessageTag[] tags = new MessageTag[1];
                        tags[0] = new MessageTag("xyz#115678", "xyz", "#123456");


                        String[] links = new String[1];
                        links[0] = "/blaadasdolsadpsa.com";

                        FirebaseUser basic_user_firebase_data = FirebaseManager.getSignedUser();

                        LiveUpdateMessage message = new LiveUpdateMessage(basic_user_firebase_data.getUid(), "new massage", tags, links);

                        TravanaAPI.addMessage(token, message, (data, statusCode1, success1) -> {

                            if(success1){
                                Log.e(TAG, data + " " + statusCode);
                            }else{
                                Log.e(TAG, "error" + statusCode1);
                            }

                        });*/








                        /*
                        TravanaAPI.messagesMarkSeen(token, "mess_gen2CPPj3qcIsPOOyHueenh6WZsX9222019-12-07_18:55:17", true, new TravanaApiCallback<String>() {
                            @Override
                            public void onComplete(@Nullable String response, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, response + " ");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }

                            }
                        });*/




                        /*
                        TravanaAPI.messages(new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                Log.e("SIZE", apiResponse.length + "");
                                Log.e(TAG + "all", Arrays.toString(apiResponse) + " ");

                            }
                        });*/

                        /*
                        TravanaAPI.messagesFollowedUnseenMeta(token, new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e("SIZE", apiResponse.length + "");
                                    Log.e(TAG, Arrays.toString(apiResponse) + " ");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }

                            }
                        });

                         */

                        /*
                        TravanaAPI.messagesByTag("xyz#115678", new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e("SIZE", apiResponse.length + "");
                                    Log.e(TAG, Arrays.toString(apiResponse) + " ");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }

                            }
                        });

                        TravanaAPI.messagesByTag(token, "xyz#115678", new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e("SIZE", apiResponse.length + "");
                                    Log.e(TAG, Arrays.toString(apiResponse) + " ");
                                }else{
                                    Log.e(TAG, "error" + statusCode);
                                }

                            }
                        });*/


                        /*
                        LiveUpdateComment comment = new LiveUpdateComment(FirebaseManager.getSignedUser().getUid(), "komentar teglavič");

                        TravanaAPI.addComment(token, "mess_gen2CPPj3qcIsPOOyHueenh6WZsX9222019-12-06_17:31:40", comment, new TravanaApiCallback<String>() {
                            @Override
                            public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, "add comment" + apiResponse);
                                }else{
                                    Log.e(TAG, statusCode + "error");
                                }

                            }

                    });

                         */


                        /*
                         TravanaAPI.commentLike(token, "comm_gen2CPPj3qcIsPOOyHueenh6WZsX922FriDec0617:32:44GMT01:002019", true, new TravanaApiCallback<String>() {
                             @Override
                             public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {


                                 if(success){
                                     Log.e(TAG, "like comment" + apiResponse);
                                 }else{
                                     Log.e(TAG, "like comment " + statusCode + "error");
                                 }


                             }
                         });*/

                        /*
                        LiveUpdateComment comment = new LiveUpdateComment("basdsadasdasd bl asa s ");

                         TravanaAPI.addCommentComment(token, "comm_gen2CPPj3qcIsPOOyHueenh6WZsX922FriDec0617:32:44GMT01:002019", comment, new TravanaApiCallback<String>() {
                             @Override
                             public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {

                                 if(success){
                                     Log.e(TAG, "add subcomment" + apiResponse);
                                 }else{
                                     Log.e(TAG, statusCode + "error");
                                 }

                             }
                         });
                           */


                        /*
                        TravanaAPI.likeCommentComment(token, "comm_gen2CPPj3qcIsPOOyHueenh6WZsX922FriDec0617:58:27GMT01:002019", true, new TravanaApiCallback<String>() {
                            @Override
                            public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {

                                if(success){
                                    Log.e(TAG, "like subcomment" + apiResponse);
                                }else{
                                    Log.e(TAG, statusCode + "error");
                                }

                            }
                        });

                         */






                        /*
                        TravanaAPI.followTag(token, "Admin#111111", (data, statusCode1, success1) -> {

                            if(success1){
                                Log.e(TAG, data + " ");
                            }else{
                                Log.e(TAG, "error" + statusCode1);
                            }

                        });*/



                        /*
                        TravanaAPI.followedMessagesMeta(token, new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                Log.e(TAG, Arrays.toString(apiResponse) + "");

                            }
                        });

                         */

                        /*
                        TravanaAPI.messagesMarkSeen(token, "mess_gen2CPPj3qcIsPOOyHueenh6WZsX9222019-12-07_17:01:42", false, new TravanaApiCallback<String>() {
                            @Override
                            public void onComplete(@Nullable String apiResponse, int statusCode, boolean success) {

                                if(success)

                                Log.e(TAG, apiResponse + "sa");

                                else
                                    Log.e(TAG, apiResponse + "error");
                            }
                        });

                        TravanaAPI.messagesFollowedUnseenMeta(token, new TravanaApiCallback<LiveUpdateMessage[]>() {
                            @Override
                            public void onComplete(@Nullable LiveUpdateMessage[] apiResponse, int statusCode, boolean success) {

                                Log.e(TAG + "ALL", Arrays.toString(apiResponse) + "");

                            }
                        });

                         */



                        /*

                        TravanaAPI.followedMessagesMeta(token, (data, statusCode1, success1) -> {

                            if(success1){
                                Log.e(TAG, data + "");
                            }else{
                                Log.e(TAG, "error1" + statusCode1);
                            }

                        });

                         */

                        /*
                        TravanaAPI.messages(new TravanaApiCallback() {
                            @Override
                            public void onComplete(@Nullable Object apiResponse, int statusCode, boolean success) {

                                if(success){

                                    Log.e(TAG, apiResponse.toString());
                                }else {
                                    Log.e(TAG, statusCode + "");
                                }

                            }
                        });

                         */


                    }else{
                        Log.e(TAG, "error" + statusCode);
                    }}
                    );

    }

    //--------------------------------------Animations

    private void initializeCircleTextViews(){

        views[0] = findViewById(R.id.circle1);
        views[1] = findViewById(R.id.textView3);
        views[2] = findViewById(R.id.textView9);
        views[3] = findViewById(R.id.textView10);
        views[4] = findViewById(R.id.textView11);
        views[5] = findViewById(R.id.textView12);
        views[6] = findViewById(R.id.textView13);
        views[7] = findViewById(R.id.textView14);
        views[8] = findViewById(R.id.textView15);
        views[9] = findViewById(R.id.textView16);
        views[10] = findViewById(R.id.textView20);
        views[11] = findViewById(R.id.textView21);
        views[12] = findViewById(R.id.textView22);
        views[13] = findViewById(R.id.textView23);
        views[14] = findViewById(R.id.textView24);
        views[15] = findViewById(R.id.textView25);
        views[16] = findViewById(R.id.textView26);
        views[17] = findViewById(R.id.textView19);

        views[18] = findViewById(R.id.textView27);
        views[19] = findViewById(R.id.textView28);
        views[20] = findViewById(R.id.textView29);
        views[21] = findViewById(R.id.textView30);
        views[22] = findViewById(R.id.textView31);
        views[23] = findViewById(R.id.textView32);
        views[24] = findViewById(R.id.textView33);
        views[25] = findViewById(R.id.textView34);
        views[26] = findViewById(R.id.textView35);
        views[27] = findViewById(R.id.textView36);
        views[28] = findViewById(R.id.textView37);
        views[29] = findViewById(R.id.textView38);
        views[30] = findViewById(R.id.textView39);
        views[31] = findViewById(R.id.textView40);
        views[32] = findViewById(R.id.textView45);
        views[33] = findViewById(R.id.textView46);
        views[34] = findViewById(R.id.textView47);
        views[35] = findViewById(R.id.textView48);
    }

    private void createCircleAnimation(){

        if(views[0] == null){
            initializeCircleTextViews();
        }

        int anim_speed = 1;
        for (View view : views){

            if(anim_speed == 0){
                view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_fast));
            }else if(anim_speed == 1){
                view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_slow));
            }else{
                view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popout_medium));
            }

            anim_speed = (anim_speed + 1) % 3;

        }
    }
}
