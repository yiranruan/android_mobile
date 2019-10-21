package com.example.mobileproject.tasks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mobileproject.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import devlight.io.library.ntb.NavigationTabBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class HorizontalCoordinatorNtbActivity extends Activity {

    List<Task> task_todo = new ArrayList<>();
    List<Task> task_doing = new ArrayList<>();
    List<Task> task_done = new ArrayList<>();
    List<RecycleAdapter> rec_adapters = new ArrayList<>();


    private RecyclerView recyclerView;
    private int task_position;
    private RecycleAdapter.TaskHolder current_holder;
    private String userID;
    private String token;
    private int groupID;
    private String groupName;
    Intent intent;

    OkHttpClient client;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
//        Fresco.initialize(this);
        setContentView(R.layout.activity_horizontal_coordinator_ntb);
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        groupID = intent.getIntExtra("groupID", Integer.valueOf(userID));
        groupName = intent.getStringExtra("groupName");
        /*

            fetch task data from server

         */
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userID)
                .add("token", token)
                .add("groupID", String.valueOf(groupID))
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.group_tasks))
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
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray tasks = jsonResponse.getJSONArray("tasks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        initUI();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("item", "onContextItemSelected: "+item.getItemId());
        int position;
        switch (item.getItemId()) {//根据子菜单ID进行菜单选择判断
            case 1:
                position = current_holder.getAdapterPosition();
//                addTask(position, task_todo.get(position), task_doing, rec_adapters.get(1));
//                removeTask(position, task_todo, rec_adapters.get(0));
                task_doing.add(0, task_todo.get(position));
//                rec_adapters.get(1).notifyItemInserted(-1);
                if(task_doing.size()==1){

                    rec_adapters.get(1).notifyDataSetChanged();

                } else {

                    rec_adapters.get(1).notifyItemInserted(-1);

                }
                task_todo.remove(position);
                rec_adapters.get(0).notifyItemRemoved(position);
                rec_adapters.get(0).notifyItemRangeChanged(position,task_todo.size());
                break;
            case 2:
                position = current_holder.getAdapterPosition();
                task_todo.add(0, task_doing.get(position));
                if(task_todo.size()==1){

                    rec_adapters.get(0).notifyDataSetChanged();
                } else {

                    rec_adapters.get(0).notifyItemInserted(-1);

                }
                task_doing.remove(position);
                rec_adapters.get(1).notifyItemRemoved(position);
                rec_adapters.get(1).notifyItemRangeChanged(position,task_doing.size());
                break;
            case 3:
                position = current_holder.getAdapterPosition();
                task_done.add(0, task_doing.get(position));
                if(task_done.size()==1){

                    rec_adapters.get(2).notifyDataSetChanged();
                } else {

                    rec_adapters.get(2).notifyItemInserted(-1);

                }
                task_doing.remove(position);
                rec_adapters.get(1).notifyItemRemoved(position);
                rec_adapters.get(1).notifyItemRangeChanged(position,task_doing.size());
                break;
            default:
                position = current_holder.getAdapterPosition();
                task_doing.add(0, task_done.get(position));
                if(task_doing.size()==1){

                    rec_adapters.get(1).notifyDataSetChanged();
                } else {

                    rec_adapters.get(1).notifyItemInserted(-1);

                }
                task_done.remove(position);
                rec_adapters.get(2).notifyItemRemoved(position);
                rec_adapters.get(2).notifyItemRangeChanged(position,task_done.size());
        }
        return super.onContextItemSelected(item);
    }


    private void initUI() {
        populate();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        @SuppressLint("ResourceType")
        CollapsingToolbarLayout tb = findViewById(R.id.toolbar);
        Log.d("nameg", "initUI: "+groupName);
        tb.setTitle(groupName);

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @SuppressLint("WrongConstant")
            @Override
            public Object instantiateItem(final ViewGroup container, final int page_position) {
                final View view = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_vp_list, null, false);

                recyclerView = (RecyclerView) view.findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(
                                getBaseContext(), LinearLayoutManager.VERTICAL, false
                        )
                );
                RecycleAdapter rec_adapter;

                switch (page_position) {
                    case 0: rec_adapter = new RecycleAdapter(task_todo, page_position, getBaseContext());
                        break;
                    case 1: rec_adapter = new RecycleAdapter(task_doing, page_position, getBaseContext());
                        break;
                    default: rec_adapter = new RecycleAdapter(task_done, page_position, getBaseContext());
                        break;
                }


                rec_adapter.setOnItemClickLitener(new RecycleAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ///WWWWWWWW
                        Task task;
                        int RequestCode;
                        switch (page_position) {
                            case 0:
                                task = task_todo.get(position);
                                RequestCode = 11;
                                break;
                            case 1:
                                task = task_doing.get(position);
                                RequestCode = 22;
                                break;
                            default:
                                task = task_done.get(position);
                                RequestCode = 33;
                                break;
                        }

                        String title = task.getTitle();
                        String description = task.getDescription();
                        String username = task.getUsername();
                        String status = task.getStatus();
                        String strDate = task.getCreateDate();
                        String eduDate = task.getDueDate();
                        String location = task.getlocation();
                        String path = task.getPath();


                        // 传输 数据 让其展示
                        Intent intent = new Intent(HorizontalCoordinatorNtbActivity.this, ShowTaskActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);
                        intent.putExtra("status", status);
                        intent.putExtra("username", username);
                        intent.putExtra("strDate", strDate);
                        intent.putExtra("dueDate", eduDate);
                        intent.putExtra("path", path);
                        intent.putExtra("location", location);
                        intent.putExtra("userID", userID);
                        intent.putExtra("groupID", Integer.toString(groupID));

                        startActivityForResult(intent, RequestCode);
                    }
                });

                rec_adapter.setOnItemLongClickListener(new RecycleAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position, @NonNull RecycleAdapter.TaskHolder holder) {
                        Log.d("task", "this is tasks"+position);
                        task_position = position;
                        current_holder = holder;
                    }
                });
                rec_adapters.add(rec_adapter);

                recyclerView.setAdapter(rec_adapter);
                registerForContextMenu(recyclerView);

                DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
                defaultItemAnimator.setAddDuration(1000);
                defaultItemAnimator.setRemoveDuration(1000);
                recyclerView.setItemAnimator(defaultItemAnimator);


                container.addView(view);
                return view;
            }
        });



        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("ToDoList")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[3]))
                        .title("DoingList")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[1]))
                        .title("DoneList")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });


        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @SuppressLint("WrongConstant")
            @Override
            public void onPageSelected(final int page_position) {
                Log.d("page", "page_position"+page_position);

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });



        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Intent intent = new Intent(HorizontalCoordinatorNtbActivity.this, CreateButton.class);
                intent.putExtra("userid", userID);
                intent.putExtra("groupid", Integer.toString(groupID));
                startActivityForResult(intent, 1);



//                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
//                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
//                    navigationTabBar.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            final String title = String.valueOf(new Random().nextInt(15));
//                            if (!model.isBadgeShowed()) {
//                                model.setBadgeTitle(title);
//                                model.showBadge();
//                            } else model.updateBadgeTitle(title);
//                        }
//                    }, i * 100);
//                }

//                coordinatorLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Snackbar snackbar = Snackbar.make(navigationTabBar, "Coordinator NTB", Snackbar.LENGTH_SHORT);
//                        snackbar.getView().setBackgroundColor(Color.parseColor("#9b92b3"));
//                        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text))
//                                .setTextColor(Color.parseColor("#423752"));
//                        snackbar.show();
//                    }
//                }, 1000);
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));



    }



//    public void myItemClick(View view){
//
//        int position = recyclerView.getChildAdapterPosition(view);
//        Toast.makeText(HorizontalCoordinatorNtbActivity.this, "hahah",
//                Toast.LENGTH_SHORT).show();
//    }



    private void populate() {


//        this.task_todo.add(new Task(1, "todo_test1", "This is todo_test 1", 1, 1, "ToDo"));
//        this.task_todo.add(new Task(2, "todo_test2", "This is todo_test 2", 2, 1, "ToDo"));
//        this.task_todo.add(new Task(3, "todo_test3", "This is todo_test 3", 3, 1, "ToDo"));
//        this.task_todo.add(new Task(4, "todo_test4", "This is todo_test 4", 4, 1, "ToDo"));
//
//        this.task_doing.add(new Task(1, "doing_test1", "This is doing_test 1", 1, 1, "Doing"));
//        this.task_doing.add(new Task(2, "doing_test2", "This is doing_test 2", 2, 1, "Doing"));
//        this.task_doing.add(new Task(3, "doing_test3", "This is doing_test 3", 3, 1, "Doing"));
//        this.task_doing.add(new Task(4, "doing_test4", "This is doing_test 4", 4, 1, "Doing"));
//
//        this.task_done.add(new Task(1, "done_test1", "This is done_test 1", 1, 1, "Done"));
//        this.task_done.add(new Task(2, "done_test2", "This is done_test 2", 2, 1, "Done"));
//        this.task_done.add(new Task(3, "done_test3", "This is done_test 3", 3, 1, "Done"));
//        this.task_done.add(new Task(4, "done_test4", "This is done_test 4", 4, 1, "Done"));
//

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

    static class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<Task> tasks;
        private int page_position;
        private Context context;
        private OnItemClickLitener mOnItemClickLitener;
        private OnItemLongClickListener mOnItemLongClickListener;

        public interface OnItemClickLitener {
            void onItemClick(View view, int position);
        }

        public interface OnItemLongClickListener {
            void onItemLongClick(View view, int position, @NonNull RecycleAdapter.TaskHolder holder);
        }

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
            this.mOnItemLongClickListener = mOnItemLongClickListener;
        }

        public RecycleAdapter(List<Task> tasks,int page_position, Context context) {
            super();
            this.tasks = tasks;
            this.context = context;
            this.page_position = page_position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

//        view.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
            View view = LayoutInflater.from(this.context).inflate(R.layout.item_list, parent, false);
            return new TaskHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TaskHolder) {
                ((TaskHolder) holder).txt.setText(tasks.get(position).getTitle());

                if (mOnItemClickLitener != null) {
                    ((TaskHolder) holder).txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("task", "onClick: " + position);
                            mOnItemClickLitener.onItemClick(view, position);
                        }
                    });

                }
                if (mOnItemLongClickListener != null) {
                    int pos = ((TaskHolder) holder).getAdapterPosition();
                    ((TaskHolder) holder).txt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Log.d("task", "onClick: " + pos);
                            mOnItemLongClickListener.onItemLongClick(view, pos, ((TaskHolder) holder));
                            return false;
                        }
                    });
                }
            }


        }

        @Override
        public int getItemViewType(int pos) {
            Log.d("holder", "getItemViewType: " + pos);
            return this.page_position;
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }


        public class TaskHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            public TextView txt;
            FrameLayout itemLayout;
            int viewType;

            @SuppressLint("WrongViewCast")
            public TaskHolder(View itemView, int viewType) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
                itemLayout = (FrameLayout) itemView.findViewById(R.id.txt_f_list);
                itemView.setOnCreateContextMenuListener(this);
                this.viewType = viewType;
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                switch (viewType) {
                    case 0:
                        menu.setHeaderTitle("Menu");//上下文菜单的标题
//                      menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                        menu.add(Menu.NONE, 1, 1, "I want to do it");
//                      menu.add(Menu.NONE, 2, 2, "Done");
                        break;
                    case 1:
                        menu.setHeaderTitle("Menu");//上下文菜单的标题
//                      menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                        menu.add(Menu.NONE, 2, 2, "I don't want to do it");
                        menu.add(Menu.NONE, 3, 3, "Done");
                        break;
                    case 2:
                        menu.setHeaderTitle("Menu");//上下文菜单的标题
//                      menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                        menu.add(Menu.NONE, 4, 4, "Do it again");
//                      menu.add(Menu.NONE, 2, 2, "Done");
                        break;
                }
            }
        }
    }



    /// WWWWWW

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 从CREATE ACTIVITY 里传回来的数据
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                // 接听数据

                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                String username = data.getStringExtra("username");
                String startDate = data.getStringExtra("startDate");
                String dueDate = data.getStringExtra("dueDate");
                String location = data.getStringExtra("location");
//                Bitmap bitmap = data.getParcelableExtra("bitmap");
                String path = data.getStringExtra("path");


                Task newtask = new Task
                        (username, title, description, userID,
                                groupID, startDate, dueDate, path, location, "To-Do");


                task_todo.add(0, newtask);
                rec_adapters.get(0).notifyDataSetChanged();

//                dic.put(title, newtask);
//
//                // 更新ADAPTER
//                list.add(title);
//                adapter.setData(list);


            }
        }
        // 从 SHOW TASK ACTIVITY 里传回来的数据
        else if (requestCode == 2){
//            if(resultCode == RESULT_OK) {
//                String title = data.getStringExtra("title");
//                String description = data.getStringExtra("description");
//                String username = data.getStringExtra("username");
//                String startDate = data.getStringExtra("startDate");
//                String dueDate = data.getStringExtra("dueDate");
//                String bitmap = data.getParcelableExtra("bitmap");
//                String location = data.getStringExtra("location");
//                String status = data.getStringExtra("status");
//                Task newtask = new Task
//                        (username, title, description, userID,
//                                groupID, startDate, dueDate, path, location, status);
//
//            }
        }
    }
}