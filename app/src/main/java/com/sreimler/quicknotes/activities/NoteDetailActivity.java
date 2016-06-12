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

package com.sreimler.quicknotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.fragments.NoteDetailFragment;
import com.sreimler.quicknotes.helpers.FirebaseUtil;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Handles the display of note details.
 */
public class NoteDetailActivity extends AppCompatActivity implements NoteDetailFragment.OnFragmentInteractionListener {

    public static final String EXTRA_NOTE_ID = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);

        // Hide the actionbar title
        setTitle("");

        if (savedInstanceState == null) {
            // Get post key from intent
            String noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);
            if (noteId == null) {
                throw new IllegalArgumentException("Must pass EXTRA_NOTE_ID");
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.note_detail__container, NoteDetailFragment.newInstance(noteId))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle("Note Details");
    }

    @Override
    public void deleteNote(String noteId) {
        DatabaseReference ref = FirebaseUtil.getNoteRef();
        if (ref != null) {
            ref.child(noteId).removeValue();
            Timber.i("Note deleted!");
        } else {
            Timber.e("Error on deleting note");
        }
        finish();
    }

    @Override
    public void editNote(String noteId) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, noteId);
        startActivity(intent);
    }
}
