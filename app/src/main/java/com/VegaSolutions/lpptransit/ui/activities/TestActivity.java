package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallbackSpecial;
import com.VegaSolutions.lpptransit.travanaserver.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.spec.ECField;


public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private static final int SELECT_PICTURE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        Button b = findViewById(R.id.button2);


            ImageView i = (ImageView)findViewById(R.id.imageView5);


        TravanaAPI.getImage("image2CPPj3qcIsPOOyHueenh6WZsX922WedNov20175144CET2019", new TravanaApiCallbackSpecial() {
            @Override
            public void onComplete(@Nullable Bitmap bitmap, int statusCode, boolean success) {

                Log.e(TAG, success + "");
                Log.e(TAG, success + ""+ statusCode);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        i.setImageBitmap(bitmap);
                    }
                });
            }
        });



        b.setOnClickListener(e -> {

                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted

                    ActivityCompat.requestPermissions(TestActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                //File write logic here

            }else {
                    showFileChooser();
                }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    showFileChooser();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(TestActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), SELECT_PICTURE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PICTURE:

                if (resultCode == RESULT_OK) {


                    FirebaseManager.getFirebaseToken((token, statusCode1, success1) -> {

                        if(success1){

                            Uri selectedImageUri = data.getData();

                            TravanaAPI.uploadImage(token, selectedImageUri,  getApplicationContext(), (data1, statusCode, success) -> {

                                if(success){
                                    Log.e(TAG, data1 + "");
                                }else {
                                    Log.e(TAG, "error" + statusCode);
                                }
                            });


                        }else{

                        }

                    });


                }

/*
                        if (success1) {

                            Uri selectedImageUri = data.getData();

                            TravanaAPI.uploadFile(token, selectedImageUri,  getApplicationContext(), (data1, statusCode, success) -> {

                                if(success){

                                    Log.e(TAG, data1 + "");

                                }else {
                                    Log.e(TAG, "error" + statusCode);
                                }
                            });


                        } else {
                            Log.e(TAG, "error" + statusCode1);
                        }

                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();

                    TravanaAPI.uploadFile("token", selectedImageUri,  getApplicationContext(), (data1, statusCode, success) -> {

                        if(success){
                            Log.e(TAG, data1 + "");
                        }else {
                            Log.e(TAG, "error" + statusCode);
                        }
                    });fr
                }

 */

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
