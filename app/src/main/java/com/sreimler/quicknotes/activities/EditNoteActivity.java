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

import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.fragments.EditNoteFragment;

/**
 * Handles the creation and editing of notes.
 */
public class EditNoteActivity extends AppCompatActivity implements EditNoteFragment.OnFragmentInteractionListener {

    public static final String EXTRA_NOTE_ID = "note_id";

    private String mNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mNoteId = getIntent().getStringExtra(EXTRA_NOTE_ID);
        if (mNoteId == null) {
            setTitle("Create Note");
        } else {
            setTitle("Edit Note");
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_note__container, EditNoteFragment.newInstance(mNoteId))
                .commit();
    }

    @Override
    public void noteSaved(String noteId) {
        if (mNoteId == null) {
            // A new note was created - create an intent to the note detail view
            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, noteId);
            startActivity(intent);
        }

        finish();
    }
}
