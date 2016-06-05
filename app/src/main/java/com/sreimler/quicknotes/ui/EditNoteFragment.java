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

package com.sreimler.quicknotes.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.data.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Provides a form for note creation and editing.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface.
 * Use the {@link EditNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNoteFragment extends Fragment {

    @BindView(R.id.edit_note__etxt_note_title)
    EditText mTitleTxt;

    @BindView(R.id.edit_note__etxt_note_text)
    EditText mTextTxt;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabaseReference;

    public EditNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Creates an instance of {@link EditNoteFragment}.
     *
     * @return A new fragment instance.
     */
    public static EditNoteFragment newInstance() {
        return new EditNoteFragment();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void noteSaved();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        ButterKnife.bind(this, view);

        return view;
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

    @OnClick(R.id.edit_note__btn_save)
    protected void save() {
        String title = mTitleTxt.getText().toString();
        String text = mTextTxt.getText().toString();
        Note note = new Note(title, text);

        // Store note in Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            mDatabaseReference.child(Note.USER_CHILD).child(user.getUid()).child(Note.NOTES_CHILD).push().setValue(note);

            // Inform the hosting activity
            mListener.noteSaved();
        } else {
            Timber.e("User not authorized");
        }
    }
}
