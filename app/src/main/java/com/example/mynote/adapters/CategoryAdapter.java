package com.example.mynote.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mynote.R;
import com.example.mynote.adapters.template.ModelAdapter;
import com.example.mynote.models.Category;
import com.example.mynote.widgets.CategoryViewHolder;

import java.util.ArrayList;

public class CategoryAdapter extends ModelAdapter<Category, CategoryViewHolder> {
    public CategoryAdapter(ArrayList<Category> items, ArrayList<Category> selected, ClickListener<Category> listener) {
        super(items, selected, listener);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
    }
}

