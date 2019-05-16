package com.example.android.aaav2;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.android.aaav2.adapter.CompositionBuilderAdapter;
import com.example.android.aaav2.adapter.EditCompositionDialogAdapter;
import com.example.android.aaav2.adapter.UserCompositionsAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.example.android.aaav2.viewmodel.HomeViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.service.media.MediaBrowserService;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements UserCompositionsAdapter.OnCompositionClickedListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 123;

    private String TAG = "MainActivity";

    private FirebaseFirestore mFirestore;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserCompositionsAdapter mAdapter;
    private MaterialCardView mPreviousView;

    @BindView(R.id.fab)
    FloatingActionButton launchCompositionActivity;

    @BindView(R.id.rv_user_compositions)
    RecyclerView recyclerView;

    @BindView(R.id.navigation)
    BottomAppBar navigation;

    @BindView(R.id.tv_no_items_here)
    TextView noItems;

    @BindView(R.id.tv_no_items_here2)
    TextView noItems2;

    //@BindView(R.id.cm_count_down)
    //Chronometer countDownPlayback;

    @BindView(R.id.ib_playback_pause_play)
    ImageButton playPausePlayback;

    @BindView(R.id.ib_playback_stop)
    ImageButton stopPlayback;

    private boolean mIsPlaying;

    private HomeViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        // Start sign in if necessary
        if (shouldStartSignIn()) {
            Log.d(TAG, "Signing user in" );
            startSignIn();
        }

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

        //BottomAppBar navigation = findViewById(R.id.navigation);
        setSupportActionBar(navigation);

        launchCompositionActivity = findViewById(R.id.fab);

        navigation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing a
                Snackbar.make(v, "CLICK", Snackbar.LENGTH_SHORT).setAction("UNDO", null).show();
            }
        });

        mMainViewModel = new HomeViewModel(getApplication());

        initRecyclerView();
    }

    void initRecyclerView(){

        final String uid = FirebaseAuth.getInstance().getUid();
        Log.d(TAG, "Init RV Main Activity UID: " + uid);

        Query mQuery = FirebaseFirestore.getInstance().collection("compositions")
                    .whereEqualTo("userID", uid);

        FirestoreRecyclerOptions<AudioComposition> options = new FirestoreRecyclerOptions.Builder<AudioComposition>()
                //.setQuery(mQuery, config, AudioClip.class)
                .setQuery(mQuery, new SnapshotParser<AudioComposition>() {
                    @NonNull
                    @Override
                    public AudioComposition parseSnapshot(@NonNull DocumentSnapshot snapshot) {


                        AudioComposition c = new AudioComposition();
                        ArrayList<AudioClip> clips = new ArrayList<>();
                        //ArrayList<String> tags = new ArrayList<>();

                        c.setCompositionTitle(snapshot.get("title").toString());
                        c.setLength(Double.valueOf(snapshot.get("length").toString()));
                        c.setUserId(uid);

                        Log.d("PARSESNAPSHOT", " parsing AudioComp " + c.getCompositionTitle());

                        String [] filenames = snapshot.get("ac_filenames").toString().split(",");
                        String [] volumes = snapshot.get("ac_volumes").toString().split(",");
                        String [] titles = snapshot.get("ac_titles").toString().split(",");
                        String [] tags = snapshot.get("tags").toString().split(",");

                        Log.d(TAG, "Files used in " + c.getCompositionTitle());
                        //inflate AudioClip obj from data
                        for (int i =0; i < filenames.length; i++) {
                            AudioClip clip = new AudioClip();

                            clip.setFile_Name(filenames[i]
                                    .replace("[", "")
                                    .replace("]","").trim());
                            Log.d(TAG, clip.getFile_Name());

                            clip.setTitle(titles[i]
                                    .replace("[", "")
                                    .replace("]","").trim());

                            clip.setVolume(volumes[i]
                                    .replace("[", "")
                                    .replace("]","").trim());

                            //Super important to save the composition document ID
                            //for easy lookup by firebase when selected
                            //this id will be needed for editing and deleting
                            clip.setDocumentID(snapshot.getId());

                            clips.add(clip);
                        }

                        //format the Tags, remove [] around each string
                        for(int i = 0; i < tags.length; i++){
                            tags[i].replace("[", "")
                                    .replace("]","");
                        }

                        c.setAudioClips(clips); //set audio clip list
                        c.setTags(tags); //set the tags

                        return c; //return composition fully inflated
                    }
                })
                .build();

        mAdapter = new UserCompositionsAdapter(options, this);

        mLayoutManager = new LinearLayoutManager(getParent()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                Log.d(TAG, "onLayoutCompleted: COMPLETED LAYING");
                if( mAdapter.getItemCount() <= 0){
                    noItems.setVisibility(View.VISIBLE);
                    noItems2.setVisibility(View.VISIBLE);
                }
                else{
                    noItems.setVisibility(View.GONE);
                    noItems2.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        //An attempt to add a swipe-to-delete functionality
        ItemTouchHelper compositionTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                //can drag up and down, can swipe left
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Log.d(TAG, "Item on Move");
                //final int fromPos = viewHolder.getAdapterPosition();
                //final int toPos = target.getAdapterPosition();
                //move list item in "from" to "to" in adapter
                //return true;//true if moved
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "Item Swiped");
                //remove from adapter
                mAdapter.removeItemOnSwiped(viewHolder.getAdapterPosition());
            }
        });

        compositionTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.startListening();

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

    @Override
    protected void onStart() {
        super.onStart();

        //countDownPlayback.setBase(0);
        playPausePlayback.setOnClickListener(this);
        stopPlayback.setOnClickListener(this);

        mAdapter.startListening(); //to receive updates from firebase


    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mIsPlaying)
            mMainViewModel.StopPlayback();

        mAdapter.stopListening();
        mIsPlaying = false;

        //user can open compositionActivity and return to mainActivity
        //when nothing is playing and that means the playback
        //controls should be hidden
        playPausePlayback.setVisibility(View.INVISIBLE);
        stopPlayback.setVisibility(View.INVISIBLE);

        if(mPreviousView!= null)
            mPreviousView.setCardBackgroundColor(getColor(R.color.compositionDisplay));

        mPreviousView = null;
    }

    //Called when the user clicks the FAB to create a new composition
    public void launchCompositionActivity(View view) {
        Log.d(TAG, "launching Composition Activity");

        Intent intent = new Intent(this, CompositionActivity.class);
        startActivity(intent);
    }

    /*
    * Called when the user clicks on a composition
    * plays the composition through the mMainViewModel
    */
    @Override
    public void onCompositionClickedListener(AudioComposition ac, MaterialCardView v) {
        //spin up the media player pool to prepare
        //user clicked on a comp when they are listening to a comp
        //unset the background on the previous view, set the background of the new view
        //update previous view to the new view
        //stop current playback
        //load selected composition. if its the same composition, it will start it over
        if(mIsPlaying) {
            Log.d(TAG, "Something is Playing, stopping playback for new comp");
            //set the previous selcted view to regular color
            if(mPreviousView != null)
                mPreviousView.setCardBackgroundColor(getColor(R.color.compositionDisplay));
                //mPreviousView.setBackgroundResource(R.color.compositionDisplay);
            //set new view to selected color
            v.setCardBackgroundColor(getColor(R.color.colorBackgroundPlaying));
            //update the new view
            mPreviousView = v;

            //stop current playing audio and load new one
            mMainViewModel.StopPlayback();
            mMainViewModel.LoadComposition(ac);

            //countDownPlayback.stop();
            //countDownPlayback.setBase(0);

        }
        //user is not playing anything
        //set bg of new view
        //load comp, set mIsPlaying flag, show the invisible playback controls
        else
        {
            Log.d(TAG, "Lets play something");
            //set new view to selected color
            v.setCardBackgroundColor(getColor(R.color.colorBackgroundPlaying));


            mMainViewModel.LoadComposition(ac);
            mIsPlaying = true;

            //countDownPlayback.setCountDown(true);
            //countDownPlayback.setBase((long)ac.getLength());
            //Log.d(TAG, "Setting Chonometer to " + String.valueOf((long)ac.getLength()));
            //countDownPlayback.setVisibility(View.VISIBLE);
            //countDownPlayback.setOnChronometerTickListener(this);
            //countDownPlayback.start();

            playPausePlayback.setBackgroundResource(R.drawable.ic_pause_black_45dp);
            playPausePlayback.setVisibility(View.VISIBLE);

            stopPlayback.setVisibility(View.VISIBLE);
            mPreviousView = v;
        }

    }

    //On CLick handler for the playback control buttons, stop and play
    @OnClick(R.id.ib_playback_pause_play)
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_playback_pause_play:
                if(mIsPlaying){
                    //something is playing so user wants to pause
                    playPausePlayback.setBackgroundResource(R.drawable.ic_play_arrow_black_45dp);


                    mIsPlaying = false;
                }
                else{
                    //something is paused so user wants to continue playing
                    playPausePlayback.setBackgroundResource(R.drawable.ic_pause_black_45dp);

                    //countDownPlayback.start();

                    mIsPlaying = true;
                }

                //tell player to pause/play
                mMainViewModel.PausePlayComposition();
                break;
            case R.id.ib_playback_stop:
                if(mIsPlaying)
                    mMainViewModel.StopPlayback();

                //countDownPlayback.stop();
                //countDownPlayback.setBase(0);
                mIsPlaying = false;

                //UI changes:
                mPreviousView.setCardBackgroundColor(getColor(R.color.compositionDisplay));
                playPausePlayback.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                playPausePlayback.setVisibility(View.INVISIBLE);
                stopPlayback.setVisibility(View.INVISIBLE);

                break;
        }

    }
}
