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

package com.sreimler.quicknotes.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreimler.quicknotes.models.Note;

import timber.log.Timber;

/**
 * Contains several static methods for accessing the Firebase backend.
 */
public class FirebaseUtil {

    public static final String USER_CHILD = "users";
    public static final String NOTES_CHILD = "notes";

    /**
     * Gets the ID of the currently signed in user.
     *
     * @return The ID of the signed in user, or null.
     */
    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    /**
     * Signs a currently signed in user out.
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Gets a {@link DatabaseReference} to the base of the app's Firebase database.
     *
     * @return The base database reference.
     */
    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Gets a {@link DatabaseReference} to the {@link Note}s of the currently signed in user.
     *
     * @return The database reference, or null if no user is signed in.
     */
    public static DatabaseReference getNoteRef() {
        String userId = getCurrentUserId();
        if (userId != null) {
            return getBaseRef()
                    .child(USER_CHILD)
                    .child(getCurrentUserId())
                    .child(NOTES_CHILD);
        } else {
            Timber.e("Can't access notes - no user signed in.");
            return null;
        }
    }

    /**
     * Adds a new {@link Note} for the currently signed in user to the Firebase database.
     *
     * @param note The {@link Note} to be added.
     * @return The ID of the new {@link Note} in the Firebase database.
     */
    public static String addNote(Note note) {
        assert (getNoteRef() != null);
        DatabaseReference ref = getNoteRef().push();
        ref.setValue(note);
        return ref.getKey();
    }

    /**
     * Updates an existing {@link Note} in the Firebase database.
     *
     * @param noteId The database ID of the {@link Note} to be updated.
     * @param note   The {@link Note} object with the new values.
     */
    public static void updateNote(String noteId, Note note) {
        assert (getNoteRef() != null);
        getNoteRef().child(noteId).setValue(note);
    }
}
