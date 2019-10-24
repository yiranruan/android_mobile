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
import com.example.mobileproject.group.ShowGroupActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;

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
    private FloatingActionButton mNewTaskBtn;
    private FloatingActionButton mProgressBtn;
    private FloatingActionsMenu menuMultipleActions;
    private KProgressHUD hud;

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
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");
        hud.show();
        /*

            fetch task data from server

         */

        initUI();





    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("item", "onContextItemSelected: "+item.getItemId());
        int position;
        checkExpansion();
        String modifyingTaskID = "";
        String changingStatus = "todo";
        switch (item.getItemId()) {//根据子菜单ID进行菜单选择判断

            case 1:
                position = current_holder.getAdapterPosition();
//                addTask(position, task_todo.get(position), task_doing, rec_adapters.get(1));
//                removeTask(position, task_todo, rec_adapters.get(0));
                Task task1 = task_todo.get(position);
                task1.changeStatus("doing");
                changingStatus = "doing";
                modifyingTaskID = task1.getTaskID();
                task_doing.add(0, task1);

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
                Task task2 = task_doing.get(position);
                task2.changeStatus("todo");
                changingStatus = "todo";
                modifyingTaskID = task2.getTaskID();

                task_todo.add(0, task2);

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

                Task task3 = task_doing.get(position);
                task3.changeStatus("done");
                changingStatus = "done";
                modifyingTaskID = task3.getTaskID();

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
                Task task4 = task_done.get(position);
                task4.changeStatus("doing");

                task_doing.add(0, task4);
                if(task_doing.size()==1){

                    rec_adapters.get(1).notifyDataSetChanged();
                } else {

                    rec_adapters.get(1).notifyItemInserted(-1);

                }
                task_done.remove(position);
                rec_adapters.get(2).notifyItemRemoved(position);
                rec_adapters.get(2).notifyItemRangeChanged(position,task_done.size());
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userID)
                .add("token", token)
                .add("taskID", modifyingTaskID)
                .add("status", changingStatus)
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.update_task_status))
                .post(requestBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HorizontalCoordinatorNtbActivity.this,"Network Issue", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Change status successfully", Toast.LENGTH_SHORT ).show();
                            }
                        });

                    }
                });
            }
        }).start();


        return super.onContextItemSelected(item);
    }


    private void initUI() {

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        /// Menu
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions_task);
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
                        int page_code;
                        checkExpansion();
                        switch (page_position) {
                            case 0:
                                task = task_todo.get(position);
                                page_code = 0;
                                break;
                            case 1:
                                task = task_doing.get(position);
                                page_code = 1;
                                break;
                            default:
                                task = task_done.get(position);
                                page_code = 2;
                                break;
                        }


                        Intent intent = new Intent(HorizontalCoordinatorNtbActivity.this, ShowTaskActivity.class);
                        String taskID = task.getTaskID();

                        intent.putExtra("position", position);
                        intent.putExtra("page_position", page_position);

                        intent.putExtra("taskID", taskID);
                        intent.putExtra("groupID", groupID);
                        intent.putExtra("token", token);
                        intent.putExtra("userID", userID);
                        startActivityForResult(intent, 2);


                    }
                });

                rec_adapter.setOnItemLongClickListener(new RecycleAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position, @NonNull RecycleAdapter.TaskHolder holder) {
                        checkExpansion();
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
                        getResources().getDrawable(R.drawable.ic_todo),
                        Color.parseColor(colors[0]))
                        .title("Todo")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_doing),
                        Color.parseColor(colors[3]))
                        .title("Doing")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_done),
                        Color.parseColor(colors[1]))
                        .title("Done")
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






        /// 点击 CREATE ACTIVITY
        mNewTaskBtn = findViewById(R.id.fab);
        mNewTaskBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HorizontalCoordinatorNtbActivity.this, CreateButton.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                intent.putExtra("groupID", Integer.toString(groupID));
                checkExpansion();
                startActivityForResult(intent, 1);
                Log.d("msg","in create");
            }
        });

        mProgressBtn = findViewById(R.id.progress);
        mProgressBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                checkExpansion();
                if (task_todo.size() != 0 || task_doing.size()!=0 || task_done.size() !=0 ) {
                    Intent intent = new Intent(HorizontalCoordinatorNtbActivity.this, ProgressActivity.class);
                    intent.putExtra("todo", task_todo.size());
                    intent.putExtra("doing", task_doing.size());
                    intent.putExtra("done", task_done.size());
                    checkExpansion();
                    startActivity(intent);
                    Log.d("msg", "in create");
                    Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Progress", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HorizontalCoordinatorNtbActivity.this, "This is not data for progress", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));

        /// ---- from service ----

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
                hud.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("get tasks", "onResponse: " + responseData);
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray tasks = jsonResponse.getJSONArray("tasks");

                    for (int i = 0; i < tasks.length(); i++){
                        JSONObject task = tasks.getJSONObject(i);

                        String category = task.getString("status");
                        String members = "";
                        if (task.has("members")){
                            members = task.getString("members");
                        }
                        if(category.equals("todo")){
                            task_todo.add(0, new Task(
                                    task.getString("_id"), //taskID
                                    task.getInt("groupID"),
                                    task.getString("title"), //title
                                    members,
                                    category
                            ));
                            rec_adapters.get(0).notifyItemInserted(-1);
                            rec_adapters.get(0).notifyDataSetChanged();
                        }
                        if(category.equals("doing")){
                            task_doing.add(0, new Task(
                                    task.getString("_id"), //taskID
                                    task.getInt("groupID"),
                                    task.getString("title"), //title
                                    members,
                                    category
                            ));
                            rec_adapters.get(1).notifyItemInserted(-1);
                            rec_adapters.get(1).notifyDataSetChanged();
                        }
                        if(category.equals("done")){
                            task_done.add(0,new Task(
                                    task.getString("_id"), //taskID
                                    task.getInt("groupID"),
                                    task.getString("title"), //title
                                    members,
                                    category
                            ));
                            rec_adapters.get(2).notifyItemInserted(-1);
                            rec_adapters.get(2).notifyDataSetChanged();
                        }

                    }
                    hud.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }



//    public void myItemClick(View view){
//
//        int position = recyclerView.getChildAdapterPosition(view);
//        Toast.makeText(HorizontalCoordinatorNtbActivity.this, "hahah",
//                Toast.LENGTH_SHORT).show();
//    }



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
                ((TaskHolder) holder).txt.setText(
                        "Task: "+ tasks.get(position).getTitle() +
                                " | Members: " + tasks.get(position).getMembers()
                );

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

            public TextView txt, txt_member;
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
        checkExpansion();

        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {

                    String responseData = data.getStringExtra("responseData");
                    try {
                        JSONObject jsonData = new JSONObject(responseData);
                        JSONObject task = new JSONObject(jsonData.getString("task"));
                        String members = "";
                        if(task.has("members")){
                            members = task.getString("members");
                        }
                        if (jsonData.getBoolean("result")){
                            task_todo.add( new Task(
                                    task.getString("_id"),
                                    task.getInt("groupID"),
                                    task.getString("title"),
                                    members,
                                    "todo"
                            ));
//                        rec_adapters.get(0).notifyItemInserted(-1);
                            rec_adapters.get(0).notifyDataSetChanged();

                        }
                        else{
                            Toast.makeText(HorizontalCoordinatorNtbActivity.this,
                                    jsonData.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case 2:
                if(resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", 999);
                    int page_code = data.getIntExtra("page_code", 999);
                    int delete = data.getIntExtra("delete", 3);
                    boolean result = data.getBooleanExtra("result", false);
                    Log.d("delete", "onActivityResult: " + delete);
                    if (delete == 0) {
                        //改变
                        if (result){
                            Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Update task successed!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Fail to update the task!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if (delete == 1){
                        if(result){
                            switch (page_code){
                                case 0:
                                    task_todo.remove(position);
                                    rec_adapters.get(0).notifyItemRemoved(position);
                                    rec_adapters.get(0).notifyItemRangeChanged(position,task_todo.size());
                                    break;
                                case 1:
                                    task_doing.remove(position);
                                    rec_adapters.get(1).notifyItemRemoved(position);
                                    rec_adapters.get(1).notifyItemRangeChanged(position,task_doing.size());

                                    break;
                                case 2:
                                    task_doing.remove(position);
                                    rec_adapters.get(2).notifyItemRemoved(position);
                                    rec_adapters.get(2).notifyItemRangeChanged(position,task_doing.size());
                                    break;
                            }
                            Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Delete task successed!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Fail to delete the task!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
        }
        // 从 SHOW TASK ACTIVITY 里传回来的数据

    }

    public void checkExpansion(){
        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapseImmediately();
        }
    }
}