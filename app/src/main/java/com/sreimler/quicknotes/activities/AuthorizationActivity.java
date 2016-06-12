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

import com.firebase.ui.auth.AuthUI;
import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.helpers.FirebaseUtil;

import timber.log.Timber;

/**
 * Handles the user authorization.
 * After successful authorization, the {@link MainActivity} is started.
 */
public class AuthorizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check the user auth state
        String userId = FirebaseUtil.getCurrentUserId();
        if (userId == null) {
            Timber.d("user is signed out");

            // User is signed out, show the Firebase signup/login activity
            startActivity(AuthUI
                    .getInstance()
                    .createSignInIntentBuilder()
                    .setProviders(AuthUI.EMAIL_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER)
                    .build());
        } else {
            Timber.d("user is signed in with id %s", userId);

            // User is signed in, redirect to the note list
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
