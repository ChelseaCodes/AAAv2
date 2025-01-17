package com.example.android.aaav2.adapter;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.example.android.aaav2.R;
import com.example.android.aaav2.model.AudioClip;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.android.aaav2.CompositionActivity.PLAYBACK_PAUSE;
import static com.example.android.aaav2.CompositionActivity.PLAYBACK_PLAY;


/*
 * Adapter creates views (via AudioViewHolder) for items and replaces the content w/ data and returns
 * information about the data like how many items in a given data source
 *
 * The FirestorePagingAdapter binds a Query to a RecyclerView by loading documents in pages.
 * */
public class CompositionBuilderAdapter extends FirestoreRecyclerAdapter<AudioClip, CompositionBuilderAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CompositionBuilderAdapter(@NonNull FirestoreRecyclerOptions<AudioClip> options, OnAudioClipSelectedListener listener
    , OnVolumeChangedListener volListener, OnVolumeDoneChangingListener doneChangingListener) {
        super(options);
        mListener = listener;
        mVolumeChangedListener = volListener;
        mVolumeDoneChangingListener = doneChangingListener;
    }

    //adapterPos used to find clip in SoundPool
    public interface OnAudioClipSelectedListener{
        void onAudioClipSelected(AudioClip ac, int playback, int adapterPos);
    }

    public interface OnVolumeChangedListener{
        void onVolumeChangedListener(AudioClip ac, float volume, int adapterPos);
    }

    public interface OnVolumeDoneChangingListener{
        void onVolumeDoneChangingListener(AudioClip ac, float volume, int adapterPos);
    }

    private OnAudioClipSelectedListener mListener;
    private OnVolumeChangedListener mVolumeChangedListener;
    private OnVolumeDoneChangingListener mVolumeDoneChangingListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View audioView = inflater.inflate(R.layout.audio_clip_list_item, parent, false);
        return new ViewHolder(audioView);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    //    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        //holder.bind(getSnapshot(position), mListener);
//        //gets DocumentSnapshot and sends to ViewHolder to set up
//        //DocumentSnapshot contains data read from doc in Firestore
//        //holder.bind(position, mListener, mVolumeChangedListener);
//    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull AudioClip audioClip) {
                viewHolder.bind(i, audioClip, mListener, mVolumeChangedListener, mVolumeDoneChangingListener);
    }

    /*A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    * Adapter should add fields for caching expensive Bind results
    *
    * ViewHolder will get a snapshot of an audio clip to bind to
    * */
    static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "PickClipViewHolder";

        @BindView(R.id.cb_audio_clip)
        CheckBox audioCheckBox;

        @BindView(R.id.cb_sb_volume)
        SeekBar audioVolumeBar;

        AudioClip mClip;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int position, AudioClip ac, final OnAudioClipSelectedListener listener,
                         final OnVolumeChangedListener volumeListener, final OnVolumeDoneChangingListener doneChanging) {
            Log.d(TAG, "Binding " + ac.getTitle());

            mClip = ac;

            //default seekbar range is 0-100 volume accepts 0 - 1
            int volume = Math.round(Float.parseFloat(ac.getVolume()) * 100.0f);
            audioVolumeBar.setProgress(volume);

            if (ac.isSelected()) {
                audioCheckBox.setChecked(true);
                audioVolumeBar.setVisibility(VISIBLE);
            } else {
                audioCheckBox.setChecked(false);
                audioVolumeBar.setVisibility(INVISIBLE);
            }

            setIcon(ac.getTitle());
            Resources resources = itemView.getResources();
            Log.d(TAG, "bind: " + resources.toString());

            audioVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //default seekbar range is 0-100 volume accepts 0 - 1
                    float volume = progress * 0.01f;
                    if(volumeListener != null){

                        volumeListener.onVolumeChangedListener(mClip, volume, getAdapterPosition());
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    //default seekbar range is 0-100 volume accepts 0 - 1
//                   float volume = seekBar.getThumbOffset() * 0.01f;
//                   if(volumeListener != null){
//                       volumeListener.onVolumeChangedListener(snapshot, volume, getAdapterPosition());
//                   }

                    float volume = seekBar.getThumbOffset() * 0.01f;
                    if(doneChanging!= null){
                        doneChanging.onVolumeDoneChangingListener(mClip, volume, getAdapterPosition());
                    }

                }
            });


            audioCheckBox.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   if(listener != null){
                       //volumebar invisible means not clicked yet
                       if(audioCheckBox.isChecked()){
                           audioCheckBox.setChecked(true);
                           mClip.setSelected(true);
                           audioVolumeBar.setVisibility(VISIBLE);

                           listener.onAudioClipSelected(mClip,PLAYBACK_PLAY, getAdapterPosition());
                       }
                       else{
                           audioCheckBox.setChecked(false);
                           audioVolumeBar.setVisibility(INVISIBLE);
                           mClip.setSelected(false);
                           listener.onAudioClipSelected(mClip,PLAYBACK_PAUSE, getAdapterPosition());
                       }

                   }
               }
            });


        }

        /*
        * setIcon is a very important function for the UI. a state list drawable will have to be
        * made for each audio clip item and it will have to be set by adding to this ifelse chain.
        * It's cancerous. I know. I don't know what else to do at the moment so this is how it is.
        * */
        private void setIcon(String title){
            //todo: can set up string based case stmnt

            switch(title){
                case "Gusty":
                    audioCheckBox.setButtonDrawable(R.drawable.windy_state_list);
                    break;
                case "Open Window":
                    audioCheckBox.setButtonDrawable(R.drawable.moderate_rain_state_list);
                    break;
                case "Crickets":
                    audioCheckBox.setButtonDrawable(R.drawable.grasshopper_state_list);
                    break;
                case "Closed Window":
                    audioCheckBox.setButtonDrawable(R.drawable.light_rain_state_list);
                    break;
                case "Frogs":
                    audioCheckBox.setButtonDrawable(R.drawable.frog_state_list);
                    break;
                case "Birds":
                    audioCheckBox.setButtonDrawable(R.drawable.bird_state_list);
                    break;
                case "Gentle Waves":
                    audioCheckBox.setButtonDrawable(R.drawable.ocean_state_list);
                    break;
                case "Small Campfire":
                    audioCheckBox.setButtonDrawable(R.drawable.campfire_state_list);
                    break;
                case "Cafe":
                    audioCheckBox.setButtonDrawable(R.drawable.cafe_state_list);
                    break;
                case "Delta":
                    audioCheckBox.setButtonDrawable(R.drawable.wave_state_list);
                    break;
                case "Fireplace":
                    audioCheckBox.setButtonDrawable(R.drawable.fireplace_state_list);
                    break;
                case "Cats Purr":
                    audioCheckBox.setButtonDrawable(R.drawable.cat_state_list);
                    break;
                default:
                    //nothing
                    break;
            }

            audioCheckBox.refreshDrawableState();
        }
    }
}

