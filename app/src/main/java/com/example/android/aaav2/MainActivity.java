package com.example.android.aaav2;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    private String TAG = "MainActivity";

    @BindView(R.id.fab)
    FloatingActionButton launchCompositionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*
        *   HomeActivity
        *       displays the users compositions
        *
        *   CompositionActivity
        *       Cant have mediaplayerfragment running.
        *       populate view with library and soundpool
        *
        *
        * */

        BottomAppBar navigation = findViewById(R.id.navigation);
        setSupportActionBar(navigation);

        launchCompositionActivity = findViewById(R.id.fab);

        navigation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing a
                Snackbar.make(v, "CLICK", Snackbar.LENGTH_SHORT).setAction("UNDO", null).show();
            }
        });

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
    }

    /*
     * This uses Firebase's API to log in users with their own
     * */
    void startSignIn(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    private boolean shouldStartSignIn() {
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;

    }

    public void launchCompositionActivity(View view) {
        Log.d(TAG, "launching Composition Activity");

        Intent intent = new Intent(this, CompositionActivity.class);
        //Intent intent = new Intent(this, AudioClipPickerActivity.class);
        startActivity(intent);

    }
}
