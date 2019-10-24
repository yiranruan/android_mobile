package com.example.mobileproject.group;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.viewpager.widget.PagerAdapter;

import com.example.mobileproject.R;

import java.util.List;
import java.util.Random;

public class Adapter extends PagerAdapter implements View.OnCreateContextMenuListener{

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;
    public CardView cardView;
    public View view;
    private OnItemLongClickListener mOnItemLongClickListener;
    int pickedimg = -1;

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Menu");//上下文菜单的标题
//        contextMenu.setHeaderIcon(android.R.drawable.ic_delete); //上下文菜单图标
        contextMenu.add(Menu.NONE, 1, 1, "Enter the group");
////                      menu.add(Menu.NONE, 2, 2, "Done");
        contextMenu.add(Menu.NONE, 2, 2, "Delete");
//                      menu.add(Menu.NONE, 2, 2, "Done");

    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
////        onCreateContextMenu(menu, v, menuInfo);
////        View v2 =
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.group_menu, menu);
//        menu.setHeaderTitle("Menu");
//    }



    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return -2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        int[] images = {R.drawable.group_cover1, R.drawable.group_cover2, R.drawable.group_cover3, R.drawable.group_cover4,
                R.drawable.group_cover5, R.drawable.group_cover6, R.drawable.group_cover7};
        Random random = new Random();
        int image =  images[random.nextInt(images.length)];
        while (pickedimg == image){
            image =  images[random.nextInt(images.length)];
        }
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item, container, false);
        ImageView imageView = view.findViewById(R.id.image_item);
        TextView groupName, countMembers, subjectName, inviteCode, description;
        imageView.setImageResource(image);
        groupName = view.findViewById(R.id.group_name);
        countMembers = view.findViewById(R.id.count_member);
        subjectName = view.findViewById(R.id.subject_name);
        inviteCode = view.findViewById(R.id.invite_code);
        description = view.findViewById(R.id.desc);

        groupName.setText("Group: " + models.get(position).getGroupName());
        countMembers.setText(Integer.toString(models.get(position).getUserCount()) + " members");
        subjectName.setText("Subject: " + (models.get(position).getSubjectName()));
        inviteCode.setText("Code: " + models.get(position).getInviteCode());
        description.setText(models.get(position).getDescription());

        //        final View card = findViewById(R.id.my_card);
        Log.d("hhh", "instantiateItem: 111");


        view.setOnCreateContextMenuListener(this);
        cardView = view.findViewById(R.id.my_card);
        if (mOnItemLongClickListener != null) {
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d("task", "onClick: " + position);
                    mOnItemLongClickListener.onItemLongClick(view, position);
                    return false;
                }
            });
        }


//
//        Log.d("hhh", "instantiateItem: 222");
//        cardView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
