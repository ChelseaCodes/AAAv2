package com.example.android.aaav2;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.android.aaav2.adapter.CompositionBuilderAdapter;
import com.example.android.aaav2.adapter.UserCompositionsAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private FirebaseFirestore mFirestore;
    private RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.fab)
    FloatingActionButton launchCompositionActivity;

    @BindView(R.id.rv_user_compositions)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();

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

        initRecyclerView();
    }

    void initRecyclerView(){
        final String uid = FirebaseAuth.getInstance().getUid();

        Query mQuery = FirebaseFirestore.getInstance().collection("compositions")
                    .whereEqualTo("userID", uid);

        FirestoreRecyclerOptions<AudioComposition> options = new FirestoreRecyclerOptions.Builder<AudioComposition>()
                //.setQuery(mQuery, config, AudioClip.class)
                .setQuery(mQuery, new SnapshotParser<AudioComposition>() {
                    @NonNull
                    @Override
                    public AudioComposition parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        AudioComposition c = new AudioComposition();

                        c.setCompositionTitle(snapshot.get("title").toString());
                        c.setLength(Double.valueOf(snapshot.get("length").toString()));
                        c.setUserId(uid);

                        return null;
                    }
                })
                .build();

        UserCompositionsAdapter mAdapter = new UserCompositionsAdapter(options);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

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
        startActivity(intent);

    }
}
