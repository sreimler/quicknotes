/*
 * Copyright 2016 Sören Reimler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sreimler.quicknotes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.adapters.NoteViewHolder;
import com.sreimler.quicknotes.models.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Displays a list of notes.
 */
public class NoteListFragment extends Fragment {

    public static final String TAG = "note_list_fragment";

    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter mAdapter;
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.fragment_note_list__recycler_view)
    RecyclerView mRecyclerView;

    public NoteListFragment() {
    }

    /**
     * Creates an instance of {@link NoteListFragment}.
     *
     * @return The fragment instance
     */
    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, view);

        // Get Firebase db reference
        mReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                    Note.class,
                    R.layout.item_note,
                    NoteViewHolder.class,
                    mReference.child(Note.USER_CHILD).child(user.getUid()).child(Note.NOTES_CHILD)) {
                @Override
                protected void populateViewHolder(NoteViewHolder viewHolder, Note note, int position) {
                    final DatabaseReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String noteId = postRef.getKey();
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Notify NoteListActivity about the click
                            mListener.listItemClicked(noteId);
                        }
                    });

                    viewHolder.bind(note);
                }
            };
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Timber.e("User not authorized");
        }

        return view;
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
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void listItemClicked(String noteId);
    }
}
