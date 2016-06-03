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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sreimler.quicknotes.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Coordinates the main view of the app.
 */
public class NoteListActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragmentManager = getSupportFragmentManager();

        addFragment(NoteListFragment.newInstance(), NoteListFragment.TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void editNote() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivity(intent);
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.getBackStackEntryCount() == 0) {
            // The first fragment should be added to the container
            // and not be placed in the back stack
            transaction.add(R.id.container, fragment, tag);
        } else {
            // Consecutive fragments should replace the initial fragment
            // and be added to the back stack to allow proper navigation
            transaction.replace(R.id.container, fragment, tag)
                    .addToBackStack(null);
        }

        transaction.commit();
    }
}