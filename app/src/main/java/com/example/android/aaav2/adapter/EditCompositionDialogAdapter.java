package com.example.android.aaav2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.android.aaav2.EditCompositionDialogFragment;
import com.example.android.aaav2.R;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EditCompositionDialogAdapter extends RecyclerView.Adapter<EditCompositionDialogAdapter.ViewHolder> {
    //listeners
    public interface OnAudioClipRemovedListener{
        void onAudioClipRemovedListener(AudioClip ac, int adapterPos);
    }

    public interface OnVolumeChangedListener{
        void onVolumeChangedListener(AudioClip ac, int adapterPos, float volume);
    }

    private OnAudioClipRemovedListener audioClipRemovedListener;
    private OnVolumeChangedListener volumeChangedListener;
    private AudioComposition editComposition;

    public EditCompositionDialogAdapter(Context c, AudioComposition ac, OnAudioClipRemovedListener removedListener,
                                        OnVolumeChangedListener volumeListener) {
        audioClipRemovedListener = removedListener;
        volumeChangedListener = volumeListener;
        editComposition = ac;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_audio_clip_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioClip audioClip = editComposition.getAudioClips().get(position);
        holder.setOnAudioClipRemovedListener(audioClipRemovedListener);
        holder.setOnVolumeChangedListener(volumeChangedListener);
        holder.bind(audioClip);
    }

    //this returns the number of audio clips inside a composition. This way the RV
    //knows to only make this many views.
    @Override
    public int getItemCount() {
        return editComposition.getAudioClips().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public static final String TAG = "EditComposition ViewHolder";

        @BindView(R.id.iv_audio_icon)
        ImageView audioIcon;

        @BindView(R.id.sb_audio_volume)
        SeekBar audioVolume;

        @BindView(R.id.ib_remove_clip)
        ImageButton removeClip;

        private OnVolumeChangedListener volumeChangedListener;
        private OnAudioClipRemovedListener clipRemovedListener;
        private AudioClip audioClip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(AudioClip ac){
            audioClip = ac;

            setIcon(audioClip.getTitle());

            int volume = Math.round(Float.parseFloat(ac.getVolume()) * 100.0f);
            audioVolume.setProgress(volume);

            audioVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //default seekbar range is 0-100 volume accepts 0 - 1
                    float volume = progress * 0.01f;
                    if(volumeChangedListener != null){
                        volumeChangedListener.onVolumeChangedListener(audioClip,getAdapterPosition(), volume);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            removeClip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clipRemovedListener != null){
                        clipRemovedListener.onAudioClipRemovedListener(audioClip, getAdapterPosition());
                    }
                }
            });


        }

        private void setIcon(String title){
            //todo: can set up string based case stmnt

            switch(title){
                case "Gusty":
                    audioIcon.setImageResource(R.drawable.ic_icons8_windy_weather_unselcted);
                    break;
                case "Open Window":
                    audioIcon.setImageResource(R.drawable.ic_icons8_moderate_rain_unfilled_50);
                    break;
                case "Crickets":
                    audioIcon.setImageResource(R.drawable.ic_icons8_grasshopper_unfilled_50);
                    break;
                case "Closed Window":
                    audioIcon.setImageResource(R.drawable.ic_icons8_light_rain_unselected);
                    break;
                case "Frogs":
                    audioIcon.setImageResource(R.drawable.ic_icons8_frog_uf);
                    break;
                case "Birds":
                    audioIcon.setImageResource(R.drawable.ic_icons8_bird_uf);
                    break;
                case "Gentle Waves":
                    audioIcon.setImageResource(R.drawable.ic_icons8_ocean_50);
                    break;
                case "Small Campfire":
                    audioIcon.setImageResource(R.drawable.ic_icons8_campfire_uf);
                    break;
                case "Cafe":
                    audioIcon.setImageResource(R.drawable.ic_icons8_cafe_uf);
                    break;
                case "Delta":
                    audioIcon.setImageResource(R.drawable.ic_icons8_audio_wave_uf);
                    break;
                case "Fireplace":
                    audioIcon.setImageResource(R.drawable.ic_icons8_fireplace_uf);
                    break;
                case "Cats Purr":
                    audioIcon.setImageResource(R.drawable.ic_icons8_cat_uf);
                    break;
                default:
                    //nothing
                    break;
            }
        }

        public void setOnVolumeChangedListener(OnVolumeChangedListener listener)
        {
            volumeChangedListener = listener;
        }

        public void setOnAudioClipRemovedListener(OnAudioClipRemovedListener listener){
            clipRemovedListener = listener;
        }
    }
}
