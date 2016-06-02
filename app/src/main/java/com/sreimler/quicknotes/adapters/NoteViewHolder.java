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

package com.sreimler.quicknotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sreimler.quicknotes.R;
import com.sreimler.quicknotes.data.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Viewholder for the list display of {@link Note} items.
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.note_item__txtv_title)
    TextView mTitleView;

    @BindView(R.id.note_item__txtv_text)
    TextView mTextView;

    public NoteViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public void bind(Note note) {
        mTitleView.setText(note.getTitle());
        mTextView.setText(note.getText());
    }
}
