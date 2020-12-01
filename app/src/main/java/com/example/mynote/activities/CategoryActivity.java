package com.example.mynote.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mynote.App;
import com.example.mynote.R;
import com.example.mynote.database.Controller;
import com.example.mynote.database.OpenHelper;
import com.example.mynote.fragments.CategoryFragment;
import com.example.mynote.fragments.template.RecyclerFragment;
import com.example.mynote.inner.Animator;
import com.example.mynote.models.Category;

public class CategoryActivity extends AppCompatActivity implements RecyclerFragment.Callbacks {
    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CHANGE = 101;
    private Toolbar toolbar;
    private CategoryFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(Category.getStyle(getIntent().getIntExtra(OpenHelper.COLUMN_THEME, Category.THEME_GREEN)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception ignored) {
        }

        toolbar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.selection_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.searchItem();
            }
        });

        if (savedInstanceState == null) {
            fragment = new CategoryFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onChangeSelection(boolean state) {
        if (state) {
            Animator.create(getApplicationContext())
                    .on(toolbar)
                    .setEndVisibility(View.INVISIBLE)
                    .animate(R.anim.fade_out);
        } else {
            Animator.create(getApplicationContext())
                    .on(toolbar)
                    .setStartVisibility(View.VISIBLE)
                    .animate(R.anim.fade_in);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_date_sorting){
            if(App.sortNotesBy == Controller.SORT_DATE_ASC) App.instance.putPrefs(App.SORT_NOTES_KEY, Controller.SORT_DATE_DESC);
            else App.instance.putPrefs(App.SORT_NOTES_KEY, Controller.SORT_DATE_ASC);
            App.instance.noteSorting();
            fragment.reloadItem();
            return true;
        }else if(id == R.id.action_title_sorting){
            if(App.sortNotesBy == Controller.SORT_TITLE_ASC) App.instance.putPrefs(App.SORT_NOTES_KEY, Controller.SORT_TITLE_DESC);
            else App.instance.putPrefs(App.SORT_NOTES_KEY, Controller.SORT_TITLE_ASC);
            App.instance.noteSorting();
            fragment.reloadItem();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void toggleOneSelection(boolean state) {
    }

    @Override
    public void toggleEditButton(boolean state) {

    }

    @Override
    public void onBackPressed() {
        if (fragment.isFabOpen) {
            fragment.toggleFab(true);
            return;
        }

        if (fragment.selectionState) {
            fragment.toggleSelection(false);
            fragment.emptySearchInput();
            return;
        }

        Intent data = new Intent();
        data.putExtra("position", fragment.categoryPosition);
        data.putExtra(OpenHelper.COLUMN_COUNTER, fragment.items.size());
        setResult(RESULT_CHANGE, data);
        finish();
    }
}

