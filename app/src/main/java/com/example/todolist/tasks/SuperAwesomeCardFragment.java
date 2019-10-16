package com.example.todolist.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.example.todolist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuperAwesomeCardFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    @BindView(R.id.task_Text)
    TextView textView;

    private int position;

    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container,false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView, 50);
//        textView.setText("CARD " + position);
        return rootView;
    }
}

