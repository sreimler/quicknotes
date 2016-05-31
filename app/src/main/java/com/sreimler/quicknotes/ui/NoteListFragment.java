package com.sreimler.quicknotes.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sreimler.quicknotes.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteListFragment extends Fragment {

    public NoteListFragment() {
    }

    /**
     * Creates an instance of {@link NoteListFragment}.
     * @return The fragment instance
     */
    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }
}
