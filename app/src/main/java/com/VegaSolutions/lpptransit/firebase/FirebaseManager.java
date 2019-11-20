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

    private static String token = null;

    public static FirebaseUser getSignedUser(){

        return FirebaseAuth.getInstance().getCurrentUser();

    }

    public static void getFirebaseToken(FirebaseCallback callback){

        if(token == null) {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser == null) {
                callback.onComplete("NotSignedIn", null, false);
                return;
            }


            mUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            callback.onComplete(idToken, null, true);

                        } else {
                            callback.onComplete(null, task.getException(), false);
                            Log.e(TAG, task.getException().getMessage());
                        }
                    });
        }

    }

}
