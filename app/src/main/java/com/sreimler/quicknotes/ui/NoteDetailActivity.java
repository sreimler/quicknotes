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

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sreimler.quicknotes.R;

/**
 * Handles the display of note details.
 */
public class NoteDetailActivity extends AppCompatActivity implements NoteDetailFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, NoteDetailFragment.newInstance("-KJNKfoM8Me_hDig8ZEF"))
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
