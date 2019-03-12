package com.example.android.aaav2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public UserCompositionsAdapter(@NonNull FirestoreRecyclerOptions<AudioComposition> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull AudioComposition audioComposition) {
        viewHolder.bind(audioComposition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_composition_list_item, parent, false));
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public static final String TAG = "User Composition View Holder";
        @BindView(R.id.tv_composition_title)
        TextView title;

        @BindView(R.id.tv_duration)
        TextView duration;

        AudioComposition audioComposition;
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
        }
    }
}
