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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.helpers.FirebaseUtil;
import com.sreimler.quicknotes.models.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Provides a form for note creation and editing.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface.
 * Use the {@link EditNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNoteFragment extends Fragment {

    public static final String ARG_NOTE_ID = "note_id";

    @BindView(R.id.edit_note__etxt_note_title)
    EditText mTitleEtxt;

    @BindView(R.id.edit_note__etxt_note_description)
    EditText mDescriptionEtxt;

    private OnFragmentInteractionListener mListener;
    private Note mNote;
    private String mNoteId;
    private MenuItem mSavingMenuItem;
    private boolean mIsSavingAllowed = false;

    public EditNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Creates an instance of {@link EditNoteFragment}.
     *
     * @param noteId Optional {@link Note} id for editing a note; if null, a new note is created.
     * @return A new fragment instance.
     */
    public static EditNoteFragment newInstance(String noteId) {
        EditNoteFragment fragment = new EditNoteFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            // When the view is created for the first time, check if it
            // should be pre-filled with values of a note object
            mNoteId = getArguments().getString(ARG_NOTE_ID);
            if (mNoteId != null) {
                // Edit an existing note - pre-fill ui with values of the note object
                DatabaseReference ref = FirebaseUtil.getNoteRef();
                if (ref != null) {
                    ref.child(mNoteId).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get note value
                                    mNote = dataSnapshot.getValue(Note.class);
                                    mTitleEtxt.setText(mNote.getTitle());
                                    // Set the cursor to the end of the description field
                                    mDescriptionEtxt.requestFocus();
                                    mDescriptionEtxt.setText(mNote.getDescription());
                                    mDescriptionEtxt.setSelection(mDescriptionEtxt.getText().length());
                                    setSavingAllowed(true);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Timber.w(databaseError.toException(), "getNote:onCancelled");
                                }
                            });
                } else {
                    Timber.e("Error on getting note details");
                }
            }

            mTitleEtxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Saving should be prevented when the title is empty
                    setSavingAllowed(s.length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNoteId == null) {
            getActivity().setTitle(getString(R.string.title_create_note));
        } else {
            getActivity().setTitle(getString(R.string.title_edit_note));
        }
    }

    private void setSavingAllowed(boolean allowed) {
        mIsSavingAllowed = allowed;
        updateSavingIcon();
    }

    private void updateSavingIcon() {
        if (mSavingMenuItem != null) {
            if (mIsSavingAllowed) {
                mSavingMenuItem.setIcon(R.drawable.ic_action_done);
            } else {
                mSavingMenuItem.setIcon(R.drawable.ic_action_done_inactive);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_note, menu);
        mSavingMenuItem = menu.findItem(R.id.action_save_note);
        updateSavingIcon();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_note:
                if (mIsSavingAllowed) {
                    String title = mTitleEtxt.getText().toString();
                    String description = mDescriptionEtxt.getText().toString();

                    if (mNoteId == null) {
                        // Create new note
                        mNote = new Note(title, description);
                        mNoteId = FirebaseUtil.addNote(mNote);
                    } else {
                        // Update the existing note object
                        mNote.setTitle(title);
                        mNote.setDescription(description);
                        FirebaseUtil.updateNote(mNoteId, mNote);
                    }

                    // Inform the hosting activity
                    mListener.noteSaved(mNoteId);
                } else {
                    // TODO: Saving not allowed - display snackbar?
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensure that the container implements the callback
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
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
        void noteSaved(String noteId);
    }
}
