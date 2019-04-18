package com.example.android.aaav2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.aaav2.R;
import com.example.android.aaav2.model.AudioComposition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCompositionsAdapter extends FirestoreRecyclerAdapter<AudioComposition, UserCompositionsAdapter.ViewHolder> {

    public static final String TAG = "UserCompositionAdapter";

    public interface OnCompositionClickedListener{
        void onCompositionClickedListener(AudioComposition ac);
    }

    private OnCompositionClickedListener onCompositionClickedListener;
    public UserCompositionsAdapter(@NonNull FirestoreRecyclerOptions<AudioComposition> options
    , OnCompositionClickedListener listener){
        super(options);
        onCompositionClickedListener = listener;
        Log.d(TAG,"Creating Adapter");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull AudioComposition audioComposition) {
        Log.d(TAG,"On Bind Composition");
        viewHolder.setOnCompositionClickedListener(onCompositionClickedListener);
        viewHolder.bind(audioComposition);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        Log.d(TAG, "ItemCount: " + count);
        return count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"OnCreate");
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_composition_list_item, parent, false));
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public static final String TAG = "User Composition View Holder";
        @BindView(R.id.tv_composition_title)
        TextView title;

        @BindView(R.id.tv_duration)
        TextView duration;

        @BindView(R.id.ib_play_pause)
        ImageButton playPauseButton;

        AudioComposition audioComposition;

        OnCompositionClickedListener onCompositionClickedListener;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(AudioComposition ac){
            Log.d(TAG, "binding audio composition " + ac.getCompositionTitle());

            audioComposition = ac;

            title.setText(ac.getCompositionTitle());

            String durationText = ac.getLength() + " mins";
            duration.setText(durationText);

            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onCompositionClickedListener != null){
                        onCompositionClickedListener.onCompositionClickedListener(audioComposition);
                    }
                }
            });
        }

        public void setOnCompositionClickedListener(OnCompositionClickedListener listener){
            onCompositionClickedListener = listener;
        }
    }
}
