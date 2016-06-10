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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.fragments.NoteDetailFragment;
import com.sreimler.quicknotes.helpers.FirebaseUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Handles the display of note details.
 */
public class NoteDetailActivity extends AppCompatActivity implements NoteDetailFragment.OnFragmentInteractionListener {

    public static final String EXTRA_NOTE_ID = "note_id";

    private String mNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);

        // Get post key from intent
        mNoteId = getIntent().getStringExtra(EXTRA_NOTE_ID);
        if (mNoteId == null) {
            throw new IllegalArgumentException("Must pass EXTRA_NOTE_ID");
        }

        // Hide the actionbar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.note_detail__container, NoteDetailFragment.newInstance(mNoteId))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_note:
                // TODO: Request deletion confirmation
                DatabaseReference ref = FirebaseUtil.getNoteRef();
                if (ref != null) {
                    ref.child(mNoteId).removeValue();
                    Timber.i("Note deleted!");
                } else {
                    Timber.e("Error on deleting note");
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.note_detail__fab)
    void editNote() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, mNoteId);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
