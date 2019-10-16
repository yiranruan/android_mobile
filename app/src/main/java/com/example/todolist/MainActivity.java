package com.example.todolist;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.todolist.fragments.ContentFragment;
import com.example.todolist.group.GroupCreateActivity;
import com.example.todolist.group.GroupJoinActivity;

import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

public class MainActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {

    private static final long RIPPLE_DURATION = 250;
    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    Toolbar toolbar;
//    RelativeLayout root;
//    View contentHamburger;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
//    private int res = R.drawable.content_music;
    private LinearLayout linearLayout;
//    private LinearLayout linearLayout;
    private int res = R.drawable.content_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        models = new ArrayList<>();
        final View createG = findViewById(R.id.create);
        createG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "You clicked",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, GroupCreateActivity.class);
//                intent.putExtra("extra_data", "Hello world");
                startActivityForResult(intent, 1);
            }
        });


        toolbar = findViewById(R.id.toolbar);
//        root = findViewById(R.id.root);
//        contentHamburger = findViewById(R.id.content_hamburger);
//
//        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
//        root.addView(guillotineMenu);
//
//        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
//                .setStartDelay(RIPPLE_DURATION)
//                .setActionBarViewForAnimation(toolbar)
//                .setClosedOnStart(true)
//                .build();


        ContentFragment contentFragment = ContentFragment.newInstance(R.drawable.content_music);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);






        final View joinG = findViewById(R.id.join);
        joinG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "You clicked",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, GroupJoinActivity.class);
                startActivityForResult(intent, 1);
            }
        });






        colors = new Integer[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        models.add(new Model(R.drawable.brochure, "Brochure","Brochure is xxxxx"));
        models.add(new Model(R.drawable.sticker, "Sticker","Sticker is xxxxx"));
        models.add(new Model(R.drawable.poster, "Poster","Poster is xxxxx"));
        models.add(new Model(R.drawable.namecard, "NameCard","NameCard is xxxxx"));


        startDrawable("private");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnName = data.getStringExtra("group_name");
                    String returnPw = data.getStringExtra("password");
                    Toast.makeText(MainActivity.this, returnName,
                            Toast.LENGTH_SHORT).show();
//                    models.add(new Model(R.drawable.namecard, returnName,"This is "+returnName));

//                    adapter = new Adapter(models, this);
//                    startActivity(new Intent(MainActivity.this, GroupCreateActivity.class));
                    startDrawable(returnName);
                }
                // send msg to server!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                break;
            default:
                break;
        }
    }

    protected void startDrawable(String returnName){
//        drawables = getResources().obtainTypedArray(R.array.random_imgs);

        if (returnName != "private") {
            int count = adapter.getCount();
            switch (count%4){
                case 0:
                    models.add(new Model(R.drawable.brochure, returnName,"This is "+returnName));
                    break;
                case 1:
                    models.add(new Model(R.drawable.sticker, returnName,"This is "+returnName));
                    break;
                case 2:
                    models.add(new Model(R.drawable.poster, returnName,"This is "+returnName));
                    break;
                case 3:
                    models.add(new Model(R.drawable.namecard, returnName,"This is "+returnName));
                    break;
                default:
                    break;
            }
        }
        adapter = new Adapter(models, this);
        Log.d("adapter", "This is a adp");
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0,130,0);

//        colors = color_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                position = position % colors.length;
//                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)){
                if (position < (colors.length - 1)){
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }





















    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.ic_backspace_black_24dp);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.BUILDING, R.drawable.ic_account_balance_black_24dp);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.BOOK, R.drawable.ic_group_black_24dp);
        list.add(menuItem2);
//        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.PAINT, R.drawable.icn_3);
//        list.add(menuItem3);
//        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.CASE, R.drawable.icn_4);
//        list.add(menuItem4);
//        SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.SHOP, R.drawable.icn_5);
//        list.add(menuItem5);
//        SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.PARTY, R.drawable.icn_6);
//        list.add(menuItem6);
//        SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.MOVIE, R.drawable.icn_7);
//        list.add(menuItem7);
    }


    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d("setting","yes");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
        ContentFragment contentFragment = ContentFragment.newInstance(this.res);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                return replaceFragment(screenShotable, position);
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}
