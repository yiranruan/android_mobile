package com.example.mobileproject.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.mobileproject.R;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

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

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item, container, false);

        TextView groupName, countMembers, subjectName, deadline, description;

        groupName = view.findViewById(R.id.group_name);
        countMembers = view.findViewById(R.id.count_member);
        subjectName = view.findViewById(R.id.subject_name);
        deadline = view.findViewById(R.id.deadline);
        description = view.findViewById(R.id.desc);

        groupName.setText(models.get(position).getGroupName());
        countMembers.setText(models.get(position).getUserCount());
        subjectName.setText((models.get(position).getSubjectName()));
        deadline.setText("Deadline: " + models.get(position).getDeadline());
        description.setText(models.get(position).getDescription());


        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
