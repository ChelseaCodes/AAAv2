package com.example.android.aaav2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.aaav2.adapter.EditCompositionDialogAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.example.android.aaav2.viewmodel.EditCompositionViewModel;
import com.example.android.aaav2.viewmodel.HomeViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditCompositionDialogFragment extends DialogFragment implements
        EditCompositionDialogAdapter.OnVolumeChangedListener,
        EditCompositionDialogAdapter.OnAudioClipRemovedListener,
        View.OnClickListener{

    public static final String TAG = "Edit Composition Dialog Fragment";
    public static final String COMPOSITION = "AUDIOCOMPOSTITION";

    @BindView(R.id.rv_save_audio_clips)
    RecyclerView recyclerView;

    @BindView(R.id.ti_et_title)
    TextInputEditText title;

    @BindView(R.id.et_composition_tags)
    TextInputEditText compositionTags;

    @BindView(R.id.ib_save_composition)
    MaterialButton saveButton;

    private SaveWIPDialogFragment.OnFragmentInteractionListener mListener;
    private HomeViewModel mHomeViewModel; //to access the playing composition
    private EditCompositionViewModel mEditViewModel;

    public interface OnCompositionSavedListener{
        void onCompositionSavedListener();
    }

    private AudioComposition mAudioComposition;

    public EditCompositionDialogFragment(){}

    public static EditCompositionDialogFragment newInstance(AudioComposition ac){
        EditCompositionDialogFragment fragment = new EditCompositionDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(COMPOSITION, ac);
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mAudioComposition = getArguments().getParcelable(COMPOSITION);
        }

        mEditViewModel = ViewModelProviders.of(this).get(EditCompositionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_save_composition, container, false);
        ButterKnife.bind(this, v);
        //initRecyclerView();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_delete_composition:
                Log.d(TAG, "ATTEMPTING TO DELETE");
                mEditViewModel.DeleteComposition(mAudioComposition);
            break;

            case R.id.ib_save_composition:
                Log.d(TAG, "ATTEMPTING TO SAVE");

                //grab title
                if(title.getText() != null){
                    mAudioComposition.setCompositionTitle(
                            title.getText().toString());
                }

                //grab any tags.
                if(compositionTags.getText() != null){
                    //mAudioComposition.getValue().setLength(Double.valueOf(playbackInput.getText().toString()));
                    String []tags = compositionTags.getText().toString().split(" ");
                    mAudioComposition.setTags(tags);
                }

                mEditViewModel.SaveComposition(mAudioComposition);
            break;
        }
    }

    @Override
    public void onAudioClipRemovedListener(AudioClip ac, int adapterPos) {

    }

    @Override
    public void onVolumeChangedListener(AudioClip ac, int adapterPos, float volume) {

    }
}
