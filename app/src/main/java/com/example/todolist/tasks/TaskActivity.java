package com.example.todolist.tasks;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.example.todolist.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.snackbar.Snackbar;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;

public class TaskActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter my_adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    RecyclerView recyclerView;
    TasksAdapter adapter;
    SwipeToAction swipeToAction;

    List<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        my_adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(my_adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(1);
        changeColor(ContextCompat.getColor(getBaseContext(), R.color.green));

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(TaskActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        swipeActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                QuickContactFragment.newInstance().show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void swipeActions(){
        Fresco.initialize(this);

//        setContentView(R.layout.activity_task);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new TasksAdapter(this.tasks);
        recyclerView.setAdapter(adapter);

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<Task>() {
            @Override
            public boolean swipeLeft(final Task itemData) {
                final int pos = removeTask(itemData);
                displaySnackbar(itemData.getID() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTask(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(Task itemData) {
                displaySnackbar(itemData.getID() + " loved", null, null);
                return true;
            }

            @Override
            public void onClick(Task itemData) {
                displaySnackbar(itemData.getID() + " clicked", null, null);
            }

            @Override
            public void onLongClick(Task itemData) {
                displaySnackbar(itemData.getID() + " long clicked", null, null);
            }
        });


        populate();

        // use swipeLeft or swipeRight and the elem position to swipe by code
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToAction.swipeRight(2);
            }
        }, 3000);
    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }

    public void onColorClicked(View v) {
        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"To-Do","Doing", "Done"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }
    }

    private void populate() {
        this.tasks.add(new Task(1, "das", "Einstein: his life and universe", 2, 3));
        this.tasks.add(new Task(2, "das", "Zero to One: Notes on Startups, or How to Build the Future", 2, 3));
        this.tasks.add(new Task(3, "das", "Tesla: Inventor of the Electrical Age", 2, 3));
        this.tasks.add(new Task(4, "das", "Orwell's Revenge: The Palimpsest", 2, 3));
        this.tasks.add(new Task(5, "das", "How to Lie with Statistics", 2, 3));
        this.tasks.add(new Task(6, "das", "Abundance: The Future Is Better Than You Think", 2, 3));
        this.tasks.add(new Task(7, "das", "Where Good Ideas Come From", 2, 3));
        this.tasks.add(new Task(8, "das", "The Information: A History, A Theory, A Flood", 2, 3));
        this.tasks.add(new Task(9, "das", "Turing's Cathedral: The Origins of the Digital Universe", 2, 3));
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private int removeTask(Task task) {
        int pos = tasks.indexOf(task);
        tasks.remove(task);
        adapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addTask(int pos, Task task) {
        tasks.add(pos, task);
        adapter.notifyItemInserted(pos);
    }
}
