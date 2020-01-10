package com.VegaSolutions.lpptransit.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
public class FirebaseManager {

    private static String TAG = "FirebaseManager";

    /**
     * @return Signed in user (null if not signed in)
     */
    public static FirebaseUser getSignedUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * check if user is signed in
     * @return boolean true if signed in
     */
    public static boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    /**
     * Returns token via callback
     * @param callback to return token
     */
    public static void getFirebaseToken(FirebaseCallback callback){


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            callback.onComplete("", null, false);
            return;
        }
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        callback.onComplete(token, null, true);
                    } else {
                        callback.onComplete(null, task.getException(), false);
                        Log.e(TAG, task.getException().getMessage());
                    }
                });
    }

    /**
     * sign out firebase user
     */

    public static void signOut(){

        FirebaseAuth.getInstance().signOut();

    }

}
