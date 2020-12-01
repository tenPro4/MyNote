package com.example.mynote.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mynote.R;
import com.example.mynote.adapters.template.ModelAdapter;
import com.example.mynote.models.Note;
import com.example.mynote.widgets.NoteViewHolder;

import java.util.ArrayList;

public class NoteAdapter extends ModelAdapter<Note, NoteViewHolder> {
    public NoteAdapter(ArrayList<Note> items, ArrayList<Note> selected, ClickListener<Note> listener) {
        super(items, selected, listener);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }
}