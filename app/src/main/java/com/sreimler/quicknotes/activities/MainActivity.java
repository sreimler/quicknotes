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
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.fragments.NoteListFragment;
import com.sreimler.quicknotes.helpers.FirebaseUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Coordinates the main view of the app.
 */
public class MainActivity extends AppCompatActivity implements NoteListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.main__toolbar)
    Toolbar mToolbar;
    private FragmentManager mFragmentManager;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout);
        ButterKnife.bind(this);

        // Setup the toolbar
        setSupportActionBar(mToolbar);

        // Add the note list fragment
        mFragmentManager = getSupportFragmentManager();
        addFragment(NoteListFragment.newInstance(), NoteListFragment.TAG);

        // Configure the drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // In order to access the elements of the navigation here,
        // the navigation view header layout has to be inflated manually
        View navigationHeader = mNavigationView.getHeaderView(0);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Display the user account details in the navigation view header
        FirebaseUser user = FirebaseUtil.getCurrentUser();
        if (user != null) {
            mUserNameTextView = ButterKnife.findById(navigationHeader, R.id.nav_header__txtv_user_name);
            mUserNameTextView.setText(user.getDisplayName());
            mUserEmailTextView = ButterKnife.findById(navigationHeader, R.id.nav_header__txtv_user_email);
            mUserEmailTextView.setText(user.getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                // Sign the user out and redirect to the AuthorizationActivity
                FirebaseUtil.signOut();
                startActivity(new Intent(MainActivity.this, AuthorizationActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks
        switch (item.getItemId()) {
            case R.id.nav_item_note_list:
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            // If the navigation drawer is opened, close the drawer
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            // Otherwise, execute the default behavior
            super.onBackPressed();
        }
    }

    @OnClick(R.id.main__fab)
    void createNote() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivity(intent);
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (mFragmentManager.getBackStackEntryCount() == 0) {
            // The first fragment should be added to the container
            // and not be placed in the back stack
            transaction.add(R.id.main__container, fragment, tag);
        } else {
            // Consecutive fragments should replace the initial fragment
            // and be added to the back stack to allow proper navigation
            transaction.replace(R.id.main__container, fragment, tag)
                    .addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void listItemClicked(String noteId) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, noteId);
        startActivity(intent);
    }
}
