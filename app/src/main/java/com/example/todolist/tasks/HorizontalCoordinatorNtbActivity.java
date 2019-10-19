package com.example.todolist.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.example.todolist.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import devlight.io.library.ntb.NavigationTabBar;

public class HorizontalCoordinatorNtbActivity extends Activity {
    List<Task> task_todo = new ArrayList<>();
    List<Task> task_doing = new ArrayList<>();
    List<Task> task_done = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecycleAdapter rec_adapter;
    private int task_position;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(this);
        setContentView(R.layout.activity_horizontal_coordinator_ntb);
        initUI();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("item", "onContextItemSelected: "+item.getItemId());
        switch (item.getItemId()) {//根据子菜单ID进行菜单选择判断
            case 1:
//                removeTask(pos, task_todo);
                Log.d("task_process", "b4: task_doing.size(): "+task_doing.size());
                Log.d("task_process", "b4: task_done.size(): "+task_todo.size());
//                addTask(task_position, task_todo.get(task_position), task_doing);
                removeTask(task_position, task_todo);
                Log.d("task_process", "after: task_doing.size(): "+task_doing.size());
                Log.d("task_process", "after: task_done.size(): "+task_todo.size());
                break;
            case 2:
//                addTask(task_position, task_doing.get(task_position), task_todo);
                removeTask(task_position, task_doing);
                Log.d("task_process", "onContextItemSelected: "+task_position);
                break;
            case 3:
                addTask(task_position, task_doing.get(task_position), task_todo);
                removeTask(task_position, task_doing);
                Log.d("task_process", "onContextItemSelected: "+task_position);
                break;
            default:
                addTask(task_position, task_done.get(task_position), task_doing);
                removeTask(task_position, task_done);
                Log.d("task_process", "onContextItemSelected: "+task_position);
        }
        return super.onContextItemSelected(item);
    }

    private void removeTask(int pos, List<Task> tasks) {
        tasks.remove(pos);
//        rec_adapter.notifyItemRangeChanged(pos, rec_adapter.getItemCount()-1);
//        rec_adapter.notifyDataSetChanged();
        rec_adapter.notifyItemRemoved(pos);
//        rec_adapter.notifyItemRangeChanged(pos, tasks.size()-pos);
    }

    private void addTask(int pos, Task task, List<Task> tasks) {
        tasks.add(task);
        rec_adapter.notifyItemInserted(pos);
    }

    private void initUI() {
        populate();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        Intent intent = getIntent();
        String group_name = intent.getStringExtra("GroupName");
        @SuppressLint("ResourceType")
        CollapsingToolbarLayout tb = findViewById(R.id.toolbar);
        Log.d("nameg", "initUI: "+group_name);
        tb.setTitle(group_name);


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

//            @Override
//            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.setPrimaryItem(container, position, object);
//                switch (position) {
//                    case 0:
//                        ;
//                        break;
//                    case 1:
//                        ;
//                        break;
//                    case 2:
//                        ;
//                        break;
////                    default:
//                }
//            }

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
//                recyclerView.setItemAnimator(new DefaultItemAnimator());



//                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


//                RecycleAdapter rec_adapter = new RecycleAdapter(task_todo, task_doing, task_done, position, getBaseContext(), recyclerView);
//
//                rec_adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(RecyclerView parent, View view, int position, String data) {
//                        Toast.makeText(HorizontalCoordinatorNtbActivity.this, data, Toast.LENGTH_SHORT).show();
//                    }
//                });

                rec_adapter = new RecycleAdapter(task_todo, task_doing, task_done, page_position, getBaseContext());
                rec_adapter.setOnItemClickLitener(new RecycleAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("page", "hahahah");
                        Toast.makeText(HorizontalCoordinatorNtbActivity.this,"postion"+position,Toast.LENGTH_SHORT).show();
//                        switch (page_position) {
//                            case 0:
//                                Log.d("page_pos", "onItemClick: "+0);
//                                break;
//                            case 1:
//                                Log.d("page_pos", "onItemClick: "+1);
//                                break;
//                            case 2:
//                                Log.d("page_pos", "onItemClick: "+2);
//                                break;
//                        }
                    }
                });

                rec_adapter.setOnItemLongClickListener(new RecycleAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d("task", "this is tasks"+position);
                        task_position = position;
                    }
                });

                recyclerView.setAdapter(rec_adapter);
                registerForContextMenu(recyclerView);

                DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
//                defaultItemAnimator.setAddDuration(1000);
//                defaultItemAnimator.setRemoveDuration(1000);
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
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final String title = String.valueOf(new Random().nextInt(15));
                            if (!model.isBadgeShowed()) {
                                model.setBadgeTitle(title);
                                model.showBadge();
                            } else model.updateBadgeTitle(title);
                        }
                    }, i * 100);
                }

                coordinatorLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snackbar = Snackbar.make(navigationTabBar, "Coordinator NTB", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(Color.parseColor("#9b92b3"));
                        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text))
                                .setTextColor(Color.parseColor("#423752"));
                        snackbar.show();
                    }
                }, 1000);
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
        this.task_todo.add(new Task(1, "todo_test1", "This is todo_test 1", 1, 1));
        this.task_todo.add(new Task(2, "todo_test2", "This is todo_test 2", 2, 1));
        this.task_todo.add(new Task(3, "todo_test3", "This is todo_test 3", 3, 1));
        this.task_todo.add(new Task(4, "todo_test4", "This is todo_test 4", 4, 1));

        this.task_doing.add(new Task(1, "doing_test1", "This is doing_test 1", 1, 1));
        this.task_doing.add(new Task(2, "doing_test2", "This is doing_test 2", 2, 1));
        this.task_doing.add(new Task(3, "doing_test3", "This is doing_test 3", 3, 1));
        this.task_doing.add(new Task(4, "doing_test4", "This is doing_test 4", 4, 1));

        this.task_done.add(new Task(1, "done_test1", "This is done_test 1", 1, 1));
        this.task_done.add(new Task(2, "done_test2", "This is done_test 2", 2, 1));
        this.task_done.add(new Task(3, "done_test3", "This is done_test 3", 3, 1));
        this.task_done.add(new Task(4, "done_test4", "This is done_test 4", 4, 1));

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
        private List<Task> todo;
        private List<Task> doing;
        private List<Task> done;
        private int page_position;
        private Context context;
        private OnItemClickLitener mOnItemClickLitener;
        private OnItemLongClickListener mOnItemLongClickListener;

        public interface OnItemClickLitener {
            void onItemClick(View view, int position);
        }

        public interface OnItemLongClickListener {
            void onItemLongClick(View view, int position);
        }

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener){
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
            this.mOnItemLongClickListener = mOnItemLongClickListener;
        }

        public RecycleAdapter(List<Task> todo, List<Task> doing, List<Task> done, int page_position, Context context) {
            super();
            this.todo = todo;
            this.doing = doing;
            this.done = done;
            this.page_position = page_position;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

//        view.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
            switch (viewType) {
                case 0:
                    View view1;
                    view1 = LayoutInflater.from(this.context).inflate(R.layout.item_list, parent, false);
//                view1.setOnClickListener(this);
                    final TodoHolder todo_hold = new TodoHolder(view1);
                    return todo_hold;
                case 1:
                    View view2;
                    view2 = LayoutInflater.from(this.context).inflate(R.layout.item_list, parent, false);
                    final DoingHolder doing_hold = new DoingHolder(view2);
                    return doing_hold;
                default:
                    View view3;
                    view3 = LayoutInflater.from(this.context).inflate(R.layout.item_list, parent, false);
                    final DoneHolder done_hold = new DoneHolder(view3);
                    return done_hold;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TodoHolder) {
                ((TodoHolder) holder).txt.setText(todo.get(position).getText());
                if (mOnItemClickLitener != null) {
                    ((TodoHolder) holder).txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickLitener.onItemClick(view, position);
                        }
                    });

                }
                if (mOnItemLongClickListener != null) {
                    int pos = ((TodoHolder) holder).getLayoutPosition();
                    ((TodoHolder) holder).txt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnItemLongClickListener.onItemLongClick(view, pos);
                            return false;
                        }
                    });
                }
            } else if (holder instanceof DoingHolder) {
                ((DoingHolder) holder).txt.setText(doing.get(position).getText());
                if (mOnItemClickLitener != null) {
                    ((DoingHolder) holder).txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickLitener.onItemClick(view, position);
                        }
                    });
                }
                if (mOnItemLongClickListener != null) {
                    int pos = ((DoingHolder) holder).getLayoutPosition();
                    ((DoingHolder) holder).txt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnItemLongClickListener.onItemLongClick(view, pos);
                            return false;
                        }
                    });
                }
            } else if (holder instanceof DoneHolder){
                ((DoneHolder) holder).txt.setText(done.get(position).getText());
                if (mOnItemClickLitener != null) {
                    ((DoneHolder) holder).txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickLitener.onItemClick(view, position);
                        }
                    });
                }

                if (mOnItemLongClickListener != null) {
                    int pos = ((DoneHolder) holder).getLayoutPosition();
                    ((DoneHolder) holder).txt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnItemLongClickListener.onItemLongClick(view, pos);
                            return false;
                        }
                    });
                }
            }


        }

        @Override
        public int getItemViewType(int pos) {
            Log.d("holder", "getItemViewType: "+pos);
            return this.page_position;
        }

        @Override
        public int getItemCount() {
            switch (this.page_position) {
                case 0: return todo.size();
                case 1: return doing.size();
                default: return done.size();
            }
        }



        public class TodoHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            public TextView txt;
            FrameLayout itemLayout;

            @SuppressLint("WrongViewCast")
            public TodoHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
                itemLayout = (FrameLayout) itemView.findViewById(R.id.txt_f_list);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                super.onCreateContextMenu(menu, v, menuInfo);

                menu.setHeaderTitle("Menu");//上下文菜单的标题
//                menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                menu.add(Menu.NONE, 1, 1, "I want to do it");
//                menu.add(Menu.NONE, 2, 2, "Done");
            }


        }

        public class DoingHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            public TextView txt;
            FrameLayout itemLayout;

            @SuppressLint("WrongViewCast")
            public DoingHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
                itemLayout = (FrameLayout) itemView.findViewById(R.id.txt_f_list);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Menu");//上下文菜单的标题
//                menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                menu.add(Menu.NONE, 2, 2, "I don't want to do it");
                menu.add(Menu.NONE, 3, 3, "Done");
            }
        }

        public class DoneHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            public TextView txt;
            FrameLayout itemLayout;

            @SuppressLint("WrongViewCast")
            public DoneHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
                itemLayout = (FrameLayout) itemView.findViewById(R.id.txt_f_list);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Menu");//上下文菜单的标题
//                menu.setHeaderIcon(android.R.drawable.ic_btn_speak_now); //上下文菜单图标
                menu.add(Menu.NONE, 4, 4, "Do it again");
//                menu.add(Menu.NONE, 2, 2, "Done");
            }
        }
    }

}


