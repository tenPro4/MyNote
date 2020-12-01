package com.example.mynote.fragments.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynote.R;
import com.example.mynote.adapters.template.ModelAdapter;
import com.example.mynote.database.Controller;
import com.example.mynote.database.OpenHelper;
import com.example.mynote.inner.Animator;
import com.example.mynote.models.Category;
import com.example.mynote.models.DatabaseModel;
import com.example.mynote.models.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

abstract public class RecyclerFragment<T extends DatabaseModel, A extends ModelAdapter>
        extends Fragment {
    public View fab;
    private RecyclerView recyclerView;
    private View empty;
    public Toolbar selectionToolbar;
    private TextView selectionCounter;
    private ImageView selectionBack,selectionDelete;
    private EditText inputSearch;
    public boolean selectionState = false;

    private A adapter;
    public ArrayList<T> items;
    public ArrayList<T> selected = new ArrayList<>();
    public Callbacks activity;

    public long categoryId = DatabaseModel.NEW_MODEL_ID;
    public String categoryTitle;
    public int categoryTheme;
    public int categoryPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        empty = view.findViewById(R.id.empty);
        selectionToolbar = (Toolbar) Objects.requireNonNull(getActivity()).findViewById(R.id.selection_toolbar);
        selectionCounter = (TextView) selectionToolbar.findViewById(R.id.selection_counter);
        selectionBack = selectionToolbar.findViewById(R.id.selection_back);
        selectionDelete = selectionToolbar.findViewById(R.id.selection_delete);
        inputSearch = selectionToolbar.findViewById(R.id.input_search);

        init(view);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null) adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(items.size() != 0)  adapter.searchNotes(s.toString());
            }
        });

        selectionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(false);
                emptySearchInput();
            }
        });

        selectionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<T> undos = new ArrayList<>();
                undos.addAll(selected);
                toggleSelection(false);

                new Thread() {
                    @Override
                    public void run() {
                        final int length = undos.size();
                        String[] ids = new String[length];
                        final int[] sortablePosition = new int[length];

                        for (int i = 0; i < length; i++) {
                            T item = undos.get(i);
                            ids[i] = String.format(Locale.US, "%d", item.id);
                            int position = items.indexOf(item);
                            item.position = position;
                            sortablePosition[i] = position;
                        }

                        Controller.instance.deleteNotes(ids, categoryId);

                        Arrays.sort(sortablePosition);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = length - 1; i >= 0; i--) {
                                    items.remove(sortablePosition[i]);
                                    adapter.notifyItemRemoved(sortablePosition[i]);
                                }

                                toggleEmpty();

                                StringBuilder message = new StringBuilder();
                                message.append(length).append(" ").append(getItemName());
                                if (length > 1) message.append("s were deleted");
                                else message.append(" was deleted.");

                                Snackbar.make(fab != null ? fab : selectionToolbar, message.toString(), 7000)
                                        .setAction(R.string.undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        Controller.instance.undoDeletion();
                                                        if (categoryId != DatabaseModel.NEW_MODEL_ID) {
                                                            Controller.instance.addCategoryCounter(categoryId, length);
                                                        }

                                                        Collections.sort(undos, new Comparator<T>() {
                                                            @Override
                                                            public int compare(T t1, T t2) {
                                                                return Integer.compare(t1.position, t2.position);
                                                            }
                                                        });

                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                for (int i = 0; i < length; i++) {
                                                                    T item = undos.get(i);
                                                                    addItem(item, item.position);
                                                                }
                                                            }
                                                        });
                                                        interrupt();
                                                    }
                                                }.start();
                                            }
                                        })
                                        .show();
                            }
                        });

                        interrupt();
                    }
                }.start();
            }
        });

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickFab();
                }
            });
        }

        Intent data = getActivity().getIntent();
        if (data != null) {
            // Get the parent data
            categoryId = data.getLongExtra(OpenHelper.COLUMN_ID, DatabaseModel.NEW_MODEL_ID);
            categoryTitle = data.getStringExtra(OpenHelper.COLUMN_TITLE);
            categoryTheme = data.getIntExtra(OpenHelper.COLUMN_THEME, Category.THEME_GREEN);
            categoryPosition = data.getIntExtra("position", 0);

            if (categoryTitle != null) {
                ((TextView) getActivity().findViewById(R.id.title)).setText(categoryTitle);
            }
        }

        loadItems();
    }

    public void onChangeCounter(int count) {
        toggleSearchItem(false);
        selectionCounter.setText(String.format(Locale.US, "%d", count));
    }

    public void toggleSelection(boolean state) {
        selectionState = state;
        activity.onChangeSelection(state);
        if (state) {
            Animator.create(getContext())
                    .on(selectionToolbar)
                    .setStartVisibility(View.VISIBLE)
                    .animate(R.anim.fade_in);
        } else {
            Animator.create(getContext())
                    .on(selectionToolbar)
                    .setEndVisibility(View.GONE)
                    .animate(R.anim.fade_out);

            deselectAll();
        }
    }

    public void searchItem(){
        toggleSelection(true);
        activity.onChangeSelection(true);
        activity.toggleEditButton(false);
        toggleSearchItem(true);
    }

    public void emptySearchInput(){
        inputSearch.setText("");
    }

    public void toggleSearchItem(boolean state){
        inputSearch.setVisibility(state ? View.VISIBLE : View.GONE);
        selectionToolbar.findViewById(R.id.icon_search).setVisibility(state ? View.VISIBLE : View.GONE);
        selectionDelete.setVisibility(!state ? View.VISIBLE : View.GONE);
        selectionCounter.setVisibility(!state ? View.VISIBLE : View.GONE);
    }

    private void deselectAll() {
        while (!selected.isEmpty()) {
            adapter.notifyItemChanged(items.indexOf(selected.remove(0)));
        }
    }

    public void loadItems() {
        new Thread() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                try {
                    if (categoryId == DatabaseModel.NEW_MODEL_ID) {
                        // Get all categories
                        items = (ArrayList<T>) Category.all();
                    } else {
                        // Get notes of the category by categoryId
                        items = (ArrayList<T>) Note.all(categoryId);
                    }

                    adapter = getAdapterClass().getDeclaredConstructor(
                            ArrayList.class,
                            ArrayList.class,
                            ModelAdapter.ClickListener.class
                    ).newInstance(items, selected, getListener());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleEmpty();

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(
                                    getContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                            ));
                        }
                    });
                } catch (Exception ignored) {
                } finally {
                    interrupt();
                }
            }
        }.start();
    }

    private void toggleEmpty() {
        if (items.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshItem(int position) {
        adapter.notifyItemChanged(position);
    }

    public void reloadItem(){
        loadItems();
        adapter.notifyDataSetChanged();
    }

    public T deleteItem(int position) {
        T item = items.remove(position);
        adapter.notifyItemRemoved(position);
        toggleEmpty();
        return item;
    }

    public void addItem(T item, int position) {
        if (items.isEmpty()) {
            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        items.add(position, item);
        adapter.notifyItemInserted(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Callbacks) context;
    }

    public void init(View view) {}

    public abstract void onClickFab();
    public abstract int getLayout();
    public abstract String getItemName();
    public abstract Class<A> getAdapterClass();
    public abstract ModelAdapter.ClickListener getListener();

    public interface Callbacks {
        void onChangeSelection(boolean state);
        void toggleOneSelection(boolean state);
        void toggleEditButton(boolean state);
    }
}
