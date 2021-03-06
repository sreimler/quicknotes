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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.helpers.FirebaseUtil;
import com.sreimler.quicknotes.models.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Displays the a view containing all details of a {@link com.sreimler.quicknotes.models.Note}.
 * Activities that contain this fragment must implement the
 * {@link NoteDetailFragment.OnFragmentInteractionListener} interface.
 * Use the {@link NoteDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteDetailFragment extends Fragment {

    public static final String ARG_NOTE_ID = "note_id";
    @BindView(R.id.note_detail__txtv_note_title)
    TextView mTitleView;
    @BindView(R.id.note_detail__txtv_note_description)
    TextView mDescriptionView;
    private Note mNote;
    private String mNoteId;
    private OnFragmentInteractionListener mListener;

    public NoteDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Creates an instance of {@link NoteDetailFragment}.
     *
     * @param noteId The ID of the note to be displayed.
     * @return A new fragment instance.
     */
    public static NoteDetailFragment newInstance(String noteId) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Manage the options menu entry in the fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mNoteId = getArguments().getString(ARG_NOTE_ID);

            if (mNoteId == null) {
                throw new IllegalArgumentException("Must pass ARG_NOTE_ID");
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseReference ref = FirebaseUtil.getNoteRef();
        if (ref != null) {
            ref.child(mNoteId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get note value
                            mNote = dataSnapshot.getValue(Note.class);
                            mTitleView.setText(mNote.getTitle());
                            mDescriptionView.setText(mNote.getDescription());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.w(databaseError.toException(), "getNote:onCancelled");
                        }
                    });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_note_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_note:
                // TODO: Request deletion confirmation
                mListener.deleteNote(mNoteId);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.note_detail__fab)
    void editNote() {
        mListener.editNote(mNoteId);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void deleteNote(String noteId);

        void editNote(String noteId);
    }
}
