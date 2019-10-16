package com.example.todolist.tasks;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import co.dift.ui.SwipeToAction;

public class TasksAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private List<Task> items;

    /** References to the views for each data item **/
    public class TaskViewHolder extends SwipeToAction.ViewHolder<Task> {
        public TextView idView;
        public TextView textView;
        public SimpleDraweeView imageView;

        public TaskViewHolder(View v) {
            super(v);

            idView = (TextView) v.findViewById(R.id.task_id);
            textView = (TextView) v.findViewById(R.id.task_Text);
//            imageView = (SimpleDraweeView) v.findViewById(R.id.image); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    /** Constructor **/
    public TasksAdapter(List<Task> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_card, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Task item = items.get(position);
        TaskViewHolder vh = (TaskViewHolder) holder;
        vh.idView.setText(Integer.toString(item.getID()));
        vh.textView.setText(item.getText());
//        vh.imageView.setImageURI(Uri.parse(item.getImageUrl())); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        vh.data = item;
    }
}