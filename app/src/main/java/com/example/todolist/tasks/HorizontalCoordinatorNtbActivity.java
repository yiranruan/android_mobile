package com.example.todolist.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Random;

import devlight.io.library.ntb.NavigationTabBar;

public class HorizontalCoordinatorNtbActivity extends Activity {
    Task[] task_todo = new Task[]{
            new Task(1, "todo_test1", "This is todo_test 1", 1, 1),
            new Task(2, "todo_test2", "This is todo_test 2", 2, 1),
            new Task(3, "todo_test3", "This is todo_test 3", 3, 1),
            new Task(4, "todo_test4", "This is todo_test 4", 4, 1),
    };
    Task[] task_doing = new Task[]{
            new Task(1, "doing_test1", "This is doing_test 1", 1, 1),
            new Task(2, "doing_test2", "This is doing_test 2", 2, 1),
            new Task(3, "doing_test3", "This is doing_test 3", 3, 1),
            new Task(4, "doing_test4", "This is doing_test 4", 4, 1),
    };
    Task[] task_done = new Task[]{
            new Task(1, "done_test1", "This is done_test 1", 1, 1),
            new Task(2, "done_test2", "This is done_test 2", 2, 1),
            new Task(3, "done_test3", "This is done_test 3", 3, 1),
            new Task(4, "done_test4", "This is done_test 4", 4, 1),
    };

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_coordinator_ntb);
        initUI();
    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
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
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_vp_list, null, false);

                recyclerView = (RecyclerView) view.findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(
                                getBaseContext(), LinearLayoutManager.VERTICAL, false
                        )
                );

                recyclerView.setAdapter(new RecycleAdapter(task_todo, task_doing, task_done, position));
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
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.ic_fourth),
//                        Color.parseColor(colors[2]))
//                        .title("Diploma")
//                        .build()
//        );
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.ic_fifth),
//                        Color.parseColor(colors[4]))
//                        .title("Medal")
//                        .build()
//        );

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
            public void onPageSelected(final int position) {
                findViewById(R.id.txt_vp_item_list).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Toast.makeText(HorizontalCoordinatorNtbActivity.this, "Click", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void myItemClick(View view){
        int position = recyclerView.getChildAdapterPosition(view);
        Toast.makeText(HorizontalCoordinatorNtbActivity.this, "hahah",
                Toast.LENGTH_SHORT).show();
    }


    public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Task[] todo;
        private Task[] doing;
        private Task[] done;
        private int position;

        public RecycleAdapter(Task[] todo, Task[] doing, Task[] done, int position) {
            super();
            this.todo = todo;
            this.doing = doing;
            this.done = done;
            this.position = position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            switch (viewType) {
                case 0:
                    return new TodoHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list, parent, false));
                case 1:
                    return new DoingHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list, parent, false));
                default:
                    return new DoneHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list, parent, false));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TodoHolder) {
                ((TodoHolder) holder).txt.setText(todo[position].getText());
            } else if (holder instanceof DoingHolder) {
                ((DoingHolder) holder).txt.setText(doing[position].getText());
            } else {
                ((DoneHolder) holder).txt.setText(done[position].getText());
            }
        }

        @Override
        public int getItemViewType(int pos) {
            return this.position;
        }

        @Override
        public int getItemCount() {
            switch (this.position) {
                case 0: return todo.length;
                case 1: return doing.length;
                default: return done.length;
            }
        }

        public class TodoHolder extends RecyclerView.ViewHolder {

            public TextView txt;

            public TodoHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
            }
        }

        public class DoingHolder extends RecyclerView.ViewHolder {

            public TextView txt;

            public DoingHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
            }
        }

        public class DoneHolder extends RecyclerView.ViewHolder {

            public TextView txt;

            public DoneHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
            }
        }
    }
}
