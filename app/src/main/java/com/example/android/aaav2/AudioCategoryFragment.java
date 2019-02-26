package com.example.android.aaav2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.aaav2.adapter.CompositionBuilderAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioCategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AudioCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioCategoryFragment extends Fragment implements
        CompositionBuilderAdapter.OnAudioClipSelectedListener, CompositionBuilderAdapter.OnVolumeChangedListener {
    @BindView(R.id.rv_audio_clips)
    RecyclerView recyclerView;

    public static final int PLAYBACK_PLAY = 1;
    public static final int PLAYBACK_PAUSE = 0;
    public static final int PLAYBACK_STOP = 2;

    private String TAG = "CompositionActivity";

    private CompositionBuilderAdapter mAdapter;
    private CompositionBuilderViewModel mViewModel;

    private FirebaseAuth mAuth; //needed to authenticate users when App boots
    private FirebaseFirestore mFirestore; //needed to access collections/docs
    private Query mQuery;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String mCategoryDisplayName = "";
    int mCurrentPosition = -1;

    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AudioCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioCategoryFragment newInstance(String param1, String param2) {
        AudioCategoryFragment fragment = new AudioCategoryFragment();
        Bundle args = new Bundle();
        args.putString(mCategoryDisplayName, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(mCategoryDisplayName);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    /*
    * The fragment created is the AudioCategoryFragment which includes a RecyclerView of
    * the category and a bottom app barrrrrr
    * */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout - the recyclerview one
        View v = inflater.inflate(R.layout.category_view, container, false);


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(CompositionBuilderViewModel.class);

        mViewModel.setDataDownloadedListener(new CompositionBuilderViewModel.DataDownloadedListener() {
            @Override
            public void onDataDownloaded() {
                initRecyclerView();

                if(mAdapter != null)
                    mAdapter.startListening();
            }
        });
    }

    private void initRecyclerView(){
        mQuery = mFirestore.collection("audio_clips");

        mAdapter = new CompositionBuilderAdapter(mQuery, this, this){
            @Override
            protected void onDataChanged(){
                Log.d(TAG, "DATA WAS CHANGED IN CompBuilderAdapter");
                //data should not change
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        recyclerView.setAdapter(mAdapter);
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

    @Override
    public void onAudioClipSelected(DocumentSnapshot AudioClip, int playback, int adapterPos) {
        com.example.android.aaav2.model.AudioClip clip = AudioClip.toObject(com.example.android.aaav2.model.AudioClip.class);

        switch(playback){
            case PLAYBACK_PAUSE:
                mViewModel.pauseAudio(adapterPos);
                break;
            case PLAYBACK_PLAY:
                mViewModel.playAudio(adapterPos);
                break;
        }
    }

    @Override
    public void onVolumeChangedListener(DocumentSnapshot AudioClip, float volume, int adapterPos) {
        mViewModel.setAudioVolume(adapterPos, volume);
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
