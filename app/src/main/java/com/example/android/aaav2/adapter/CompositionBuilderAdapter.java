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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.android.aaav2.CompositionActivity.PLAYBACK_PAUSE;
import static com.example.android.aaav2.CompositionActivity.PLAYBACK_PLAY;


/*
 * Adapter creates views (via AudioViewHolder) for items and replaces the content w/ data and returns
 * information about the data like how many items in a given data source
 * */
public class CompositionBuilderAdapter extends FirestoreAdapter<CompositionBuilderAdapter.ViewHolder>{

    public interface OnAudioClipSelectedListener{
        void onAudioClipSelected(DocumentSnapshot AudioClip, int playback, int adapterPos);
    }

    public interface OnVolumeChangedListener{
        void onVolumeChangedListener(DocumentSnapshot AudioClip, float volume, int adapterPos);
    }

    private OnAudioClipSelectedListener mListener;
    private OnVolumeChangedListener mVolumeChangedListener;

    public CompositionBuilderAdapter(Query query, OnAudioClipSelectedListener listener,
                                     OnVolumeChangedListener volumeChangedListener){
        super(query);
        mListener = listener;
        mVolumeChangedListener = volumeChangedListener;
    }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        //release mediasource
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View audioView = inflater.inflate(R.layout.audio_clip_list_item, parent, false);
        return new ViewHolder(audioView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.bind(getSnapshot(position), mListener);
        //gets DocumentSnapshot and sends to ViewHolder to set up
        //DocumentSnapshot contains data read from doc in Firestore
        holder.bind(getSnapshot(position), position, mListener, mVolumeChangedListener);
    }


    /*A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    * Adapter should add fields for caching expensive Bind results
    *
    * ViewHolder will get a snapshot of an audio clip to bind to
    * */
    static class ViewHolder extends RecyclerView.ViewHolder{

        private String TAG = "ViewHolder";

        @BindView(R.id.cb_audio_clip)
        CheckBox audioCheckBox;

        @BindView(R.id.cb_sb_volume)
        SeekBar audioVolumeBar;

        AudioClip mClip;
        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, int i, final OnAudioClipSelectedListener listener,
                         final OnVolumeChangedListener volumeListener) {
            Log.d(TAG, "Binding");

            AudioClip clip = new AudioClip();

            clip.setCategory(snapshot.get("category").toString());
            clip.setEmoji(snapshot.get("emoji").toString());
            clip.setFileName(snapshot.get("file_name").toString());
            clip.setTitle(snapshot.get("title").toString());
            clip.setVolume(snapshot.get("volume").toString());

            mClip = clip;
            Resources resources = itemView.getResources();

            audioVolumeBar.setVisibility(INVISIBLE);
            //default seekbar range is 0-100 volume accepts 0 - 1
            audioVolumeBar.setThumbOffset(100);
            audioVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //default seekbar range is 0-100 volume accepts 0 - 1
                    float volume = progress * 0.01f;
                    if(volumeListener != null){
                        volumeListener.onVolumeChangedListener(snapshot, volume, getAdapterPosition());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //default seekbar range is 0-100 volume accepts 0 - 1
                   float volume = seekBar.getThumbOffset() * 0.01f;
                   if(volumeListener != null){
                       volumeListener.onVolumeChangedListener(snapshot, volume, getAdapterPosition());
                   }
                }
            });

            audioCheckBox.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   if(listener != null){
                       //volumebar invisible means not clicked yet
                       if(audioVolumeBar.getVisibility() == INVISIBLE){
                           audioVolumeBar.setVisibility(VISIBLE);

                           listener.onAudioClipSelected(snapshot,PLAYBACK_PLAY, getAdapterPosition());
                       }
                       else{
                           audioVolumeBar.setVisibility(INVISIBLE);
                           listener.onAudioClipSelected(snapshot,PLAYBACK_PAUSE, getAdapterPosition());
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
        private void setIcon(){
            String title = mClip.getTitle();
            if(title == "Gusty"){
                audioCheckBox.setButtonDrawable(R.drawable.windyweatherStateList);
            }
            else if(title == "Open Window"){
                audioCheckBox.setButtonDrawable(R.drawable.moderaterainStateList);
            }
            else if(title == "Closed Window"){
                audioCheckBox.setButtonDrawable(R.drawable.statelistdrawable);
            }
            else if(title == "Crickets"){
                audioCheckBox.setButtonDrawable(R.drawable.grasshopperStateList);
            }
        }
    }
}

