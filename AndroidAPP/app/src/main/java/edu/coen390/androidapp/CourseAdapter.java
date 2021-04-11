package edu.coen390.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import edu.coen390.androidapp.Model.Course;

public class CourseAdapter extends BaseAdapter {


    private final List<Course> courses;
    Context mContext;
    private TextView courseTextView;

    public CourseAdapter(Context context, List<Course> courses) {
        this.mContext = context;
        this.courses = courses;
    }


    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = (Course) getItem(position);
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.course_item, parent, false);
        }


        courseTextView = listItem.findViewById(R.id.courseTextView);

        String coursesString = course.getTitle() + "\n" + course.getCode();
        courseTextView.setText(coursesString);


        return listItem;
    }
}
