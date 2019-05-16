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
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCompositionsAdapter extends FirestoreRecyclerAdapter<AudioComposition, UserCompositionsAdapter.ViewHolder>{

    public static final String TAG = "UserCompositionAdapter";
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public interface OnCompositionClickedListener{
        void onCompositionClickedListener(AudioComposition ac, MaterialCardView v);
    }
    //implement the onItemMove and onItemDismiss for the swipe to dismisss functionality


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

    //when user swipes the composition at position, remove it from the adapter
    //which means removing it from firebase too
    public void removeItemOnSwiped(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
        //no need to call notifyDataSetRemoved. FirestoreRecyclerAdapter class takes
        //care of that.
    }


    //May not work since this recyclerview is adapted for firestore.
    //idea is to change the view returned depending on whats in the list
    //if the list is empty i want to display a screen to help users
    //create their first composition
    @Override
    public int getItemViewType(int position) {
        if(getItemCount() == 0){
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        }else{
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"OnCreate");

        switch (viewType){
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                Log.d(TAG, "onCreateViewHolder: EmptyList");
                break;
            case VIEW_TYPE_OBJECT_VIEW:
                Log.d(TAG, "onCreateViewHolder: full list");
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.display_composition_list_item, parent, false));

        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public static final String TAG = "User Composition View Holder";
        @BindView(R.id.tv_composition_title)
        TextView title;

        //@BindView(R.id.tv_duration)
        //TextView duration;

        @BindView(R.id.tv_tags)
        TextView tags;

        @BindView(R.id.cv_composition)
        MaterialCardView compostionView;

//        @BindView(R.id.ib_play_pause)
//        ImageButton playPauseButton;

        AudioComposition audioComposition;

        OnCompositionClickedListener onCompositionClickedListener;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            ButterKnife.bind(this, itemView);
            compostionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Clicked View");

                    if(onCompositionClickedListener != null){
                        onCompositionClickedListener.onCompositionClickedListener(audioComposition, compostionView);
                    }
                }
            });
        }

        public void bind(AudioComposition ac){
            Log.d(TAG, "binding audio composition " + ac.getCompositionTitle());

            audioComposition = ac;

            title.setText(ac.getCompositionTitle());

            String durationText = ac.getLength() + " mins";
           // duration.setText(durationText);

            if(audioComposition.getTags() != null) {
                StringBuilder string_tags = new StringBuilder();
                for (String s : audioComposition.getTags()) {
                    string_tags.append(s);
                }


                tags.setText(string_tags.toString().replace("[", "").replace("]", ""));
            }

//            playPauseButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(onCompositionClickedListener != null){
//                        if(!clicked) {
//                            playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
//
//                        }else{
//                            playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//                        }
//
//                        onCompositionClickedListener.onCompositionClickedListener(audioComposition);
//                    }
//                }
//            });

        }

        public void setOnCompositionClickedListener(OnCompositionClickedListener listener){
            onCompositionClickedListener = listener;
        }
    }
    
}
