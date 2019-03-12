package com.example.android.aaav2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.aaav2.adapter.SaveCompositionDialogAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.example.android.aaav2.viewmodel.EditCompositionViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveWIPDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveWIPDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveWIPDialogFragment extends DialogFragment implements SaveCompositionDialogAdapter.OnVolumeChangedListener
, SaveCompositionDialogAdapter.OnAudioClipRemovedListener, EditCompositionViewModel.OnCompositionRetrievedListener {
    public static final String TAG = "Save WIP Dialog Fragment";

    public interface OnCompositionSavedListener{
        void onCompositionSavedListener();
    }
    // TODO: Rename and change types of parameters
//    private MutableLiveData<AudioComposition> mAudioComposition;
//    private String mParam2;

    @BindView(R.id.rv_save_audio_clips)
    RecyclerView recyclerView;

    @BindView(R.id.ti_et_title)
    TextInputEditText title;

    @BindView(R.id.et_playback_duration)
    TextInputEditText playbackInput;

    @BindView(R.id.ib_save_composition)
    MaterialButton saveButton;

    private OnFragmentInteractionListener mListener;
    private CompositionBuilderViewModel mBuilderViewModel;
    private EditCompositionViewModel mEditViewModel;
    private SaveCompositionDialogAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private MutableLiveData<AudioComposition> mAudioComposition;



    private OnCompositionSavedListener onCompositionSavedListener;

    public SaveWIPDialogFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveWIPDialogFragment newInstance() {
        //SaveWIPDialogFragment fragment = new SaveWIPDialogFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new SaveWIPDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mAudioComposition =  getArguments().get("composition");
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        mBuilderViewModel = ViewModelProviders.of(getActivity()).get(CompositionBuilderViewModel.class);

        mEditViewModel = ViewModelProviders.of(this).get(EditCompositionViewModel.class);
        mEditViewModel.setOnCompositionRetrieved(this);
        if(mAudioComposition == null){
            mAudioComposition = new MutableLiveData<>();
            mEditViewModel.getAudioComposition();
        }

        mEditViewModel.setOnAudioCompositionSavedListener(new Repository.OnAudioCompositionSavedListener() {
            @Override
            public void onAudioCompositionSavedListener() {
                //close the view
                //destroy the window and the fragement underneath, taking care to clean up the resources
                Log.d(TAG, "cleaning up resources and closing");
                //notify activity that we are done so activity can close fragment viewpager
                if(onCompositionSavedListener != null)
                    onCompositionSavedListener.onCompositionSavedListener();

                cleanup_close();

            }
        });
    }

    private void cleanup_close(){
        mBuilderViewModel.stopAudio();

        getDialog().dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_save_composition, container, false);
        ButterKnife.bind(this, v);
        //recyclerView = v.findViewById(R.id.rv_save_audio_clips);
        //saveButton = v.findViewById(R.id.ib_save_composition);
        //title = v.findViewById(R.id.ti_et_title);

        initRecyclerView();

        // Inflate the layout for this fragment
        return v;
    }

    @OnClick(R.id.ib_save_composition)
    public void onClick(View v){
        Log.d(TAG, "ATTEMPTING TO SAVE");

        //grab title
        if(title.getText() != null){
            mAudioComposition.getValue().setCompositionTitle(
                    title.getText().toString());
        }
        else
            //todo use snackbars
            Log.d(TAG, "Could not save title");


        //grab duration
        if(playbackInput.getText() != null){
            mAudioComposition.getValue().setLength(Double.valueOf(playbackInput.getText().toString()));
        }
        else
            Log.d(TAG, "error saving duration");



        mEditViewModel.SaveComposition(mAudioComposition.getValue());
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        params.height = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initRecyclerView(){
        Query mQuery = FirebaseFirestore.getInstance().collection("wip")
                .whereEqualTo("userID", mBuilderViewModel.getUserID())
                .whereEqualTo("selected", true);

        FirestoreRecyclerOptions<AudioClip> options = new FirestoreRecyclerOptions.Builder<AudioClip>()
                .setLifecycleOwner(this)
                //.setQuery(mQuery, config, AudioClip.class)
                .setQuery(mQuery, AudioClip.class)
                .build();

        mAdapter = new SaveCompositionDialogAdapter(options, this, this);
        mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);   //should set up everything else.
    }


    @Override
    public void onAudioClipRemovedListener(AudioClip ac, int adapterPos) {
                ac.setSelected(false);
                mBuilderViewModel.pauseAudio(ac);
                //pause removes from recyclerview
    }

    @Override
    public void onVolumeChangedListener(AudioClip ac, int adapterPos, float volume) {
        ac.setVolume(String.valueOf(volume));

        mBuilderViewModel.setAudioVolume(ac, volume);
    }

    @Override
    public void onCompositionRetrieved(MutableLiveData<AudioComposition> ac) {
        Log.d(TAG, "LOADED AUDIO COMPOSITION **************************************");
        mAudioComposition = ac;
    }

    public void setOnCompositionSavedListener(OnCompositionSavedListener onCompositionSavedListener) {
        this.onCompositionSavedListener = onCompositionSavedListener;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
