package com.example.mynote.fragments;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.mynote.R;
import com.example.mynote.fragments.template.NoteFragment;
import com.example.mynote.models.DatabaseModel;

import jp.wasabeef.richeditor.RichEditor;

public class SimpleNoteFragment extends NoteFragment {
    private RichEditor body;

    public SimpleNoteFragment() {}

    @Override
    public int getLayout() {
        return R.layout.fragment_simple_note;
    }

    @Override
    public void saveNote(final SaveListener listener) {
        super.saveNote(listener);
        note.body = body.getHtml();

        new Thread() {
            @Override
            public void run() {
                long id = note.save();
                if (note.id == DatabaseModel.NEW_MODEL_ID) {
                    note.id = id;
                }
                listener.onSave();
                interrupt();
            }
        }.start();
    }

    @Override
    public void init(View view) {
        body = (RichEditor) view.findViewById(R.id.editor);
        body.setPlaceholder("Note");
        body.setEditorBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg));

        view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setBold();
            }
        });

        view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setItalic();
            }
        });

        view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setUnderline();
            }
        });

        view.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.setHeading(1);
            }
        });

        view.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.setHeading(2);
            }
        });

        view.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.setHeading(3);
            }
        });

        view.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setAlignRight();
            }
        });

        view.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setAlignLeft();
            }
        });

        view.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setAlignCenter();
            }
        });

        view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setBullets();
            }
        });

        view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                body.setNumbers();
            }
        });

        body.setHtml(note.body);
    }
}
