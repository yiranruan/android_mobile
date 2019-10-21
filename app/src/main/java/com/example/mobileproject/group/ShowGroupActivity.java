package com.example.mobileproject.group;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;


import com.example.mobileproject.R;
import com.example.mobileproject.fragment.ContentFragment;
import com.example.mobileproject.tasks.HorizontalCoordinatorNtbActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class ShowGroupActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {

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
    private int page_position = 0;
    private String userID = "6";
    private String token = "umeuuuufae";
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);
        models = new ArrayList<>();

        /*

            get group information from server

         */

        Intent intent = getIntent();
//        userID = intent.getStringExtra("userID");
//        token = intent.getStringExtra("token");

        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userID)
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.get_group_list))
                .post(requestBody)
                .build();

        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonData = new JSONObject(responseData);
                    JSONArray jsonArray = jsonData.getJSONArray("list");
                    Log.d("group list", "onResponse: " + jsonArray.length());
                    for (int i = 0 ; i < jsonArray.length(); i++) {
                        JSONObject element = jsonArray.getJSONObject(i);
                        Log.d("group", "group " + i + ": " + element.toString());
                        int groupID = element.getInt("groupID");
                        String groupName = element.getString("groupName");
                        String description = element.getString("description");
                        int memberCount = element.getInt("memberCount");
                        String inviteCode = element.getString("groupCode");
                        String subjectName = element.getString("subjectName");
                        Log.d("TEST", "onResponse: 1111");
                        models.add(new Model(groupID, memberCount, groupName, subjectName, inviteCode, description));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                    models.add(new Model(Integer.valueOf(userID),  1, "Personal Tasks", " ","", "This is a personal task"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        final View createG = findViewById(R.id.create);
        createG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShowGroupActivity.this, "You clicked",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ShowGroupActivity.this, GroupCreateActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                startActivityForResult(intent, 2);
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
                Toast.makeText(ShowGroupActivity.this, "You clicked",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShowGroupActivity.this, GroupJoinActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                startActivityForResult(intent, 1);
            }
        });






        colors = new Integer[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };


        Log.d("TEST", "onResponse: 2222");
        startDrawable();


//        models.add(new Model(1, R.drawable.brochure, "Brochure","Brochure is xxxxx"));
//        models.add(new Model(2,R.drawable.sticker, "Sticker","Sticker is xxxxx"));
//        models.add(new Model(3,R.drawable.poster, "Poster","Poster is xxxxx"));
//        models.add(new Model(4,R.drawable.namecard, "NameCard","NameCard is xxxxx"));




        final Button btn_task = findViewById(R.id.btnTask);
        btn_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShowGroupActivity.this, "You clicked",
                        Toast.LENGTH_SHORT).show();
                Log.d("page_pos", "onClick: "+page_position);

                Intent intent = new Intent(ShowGroupActivity.this, HorizontalCoordinatorNtbActivity.class);
//                intent.putExtra("GroupName", models.get(page_position).getGroupName());
                intent.putExtra("groupID",models.get(page_position).getGroupID());
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                startActivity(intent); // 获得position 得到特定页面
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // Join a new group
            case 1:
                if (resultCode == RESULT_OK){
                    String groupInfo = data.getStringExtra("groupInfo");
                    try {
                        JSONObject jsonGroupInfo = new JSONObject(groupInfo);
                        models.add(new Model(
                                jsonGroupInfo.getInt("groupID"),
                                jsonGroupInfo.getInt("memberCount"),
                                jsonGroupInfo.getString("groupName"),
                                jsonGroupInfo.getString("subjectName"),
                                jsonGroupInfo.getString("groupCode"),
                                jsonGroupInfo.getString("description")
                                ));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(ShowGroupActivity.this, "You are in this group", Toast.LENGTH_SHORT).show();
                }
                break;

            // Create a new group
            case 2:
                Log.d("create group", "onActivityResult: 3");
                if(resultCode == RESULT_OK){
                    String group = data.getStringExtra("groupInfo");
                    Log.d("created group", "onActivityResult: " + group);
                    try {
                        JSONObject groupJson = new JSONObject(group);
                        models.add(new Model(
                                groupJson.getInt("groupID"),
                                groupJson.getInt("memberCount"),
                                groupJson.getString("groupName"),
                                groupJson.getString("subjectName"),
                                groupJson.getString("groupCode"),
                                groupJson.getString("description")
                        ));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(ShowGroupActivity.this, "Fail to create a new group, try it later", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    protected void startDrawable(){
        Log.d("TEST", "onResponse: 3333");
//        drawables = getResources().obtainTypedArray(R.array.random_imgs);

//        if (returnName != "private") {
//            int count = adapter.getCount();
//            switch (count%4){
//                case 0:
//                    models.add(new Model(1,R.drawable.brochure, returnName,"This is "+returnName));
//                    break;
//                case 1:
//                    models.add(new Model(2,R.drawable.sticker, returnName,"This is "+returnName));
//                    break;
//                case 2:
//                    models.add(new Model(3,R.drawable.poster, returnName,"This is "+returnName));
//                    break;
//                case 3:
//                    models.add(new Model(4,R.drawable.namecard, returnName,"This is "+returnName));
//                    break;
//                default:
//                    break;
//            }
//        }
        Log.d("run", "startDrawable: ");

        adapter = new Adapter(models, this);
        Log.d("adapter", "This is a adp");
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0,130,0);


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
                page_position = position;

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
