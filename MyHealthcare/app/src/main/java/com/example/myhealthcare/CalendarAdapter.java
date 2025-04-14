package com.example.myhealthcare;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<Date> dateList = new ArrayList<>();
    private Calendar currentDate = Calendar.getInstance();
    private int selectedPosition = -1;

    public CalendarAdapter(Context context, Calendar month) {
        this.context = context;
        refreshDays(month);
    }

    @Override
    public int getCount() {
        return dateList.size();
    }

    @Override
    public Object getItem(int position) {
        return dateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.calendar_cell, parent, false);
        } else {
            view = convertView;
        }

        TextView textView = (TextView) view;
        Date date = dateList.get(position);

        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            textView.setText(String.valueOf(day));

            // Set text color based on whether it's the current month
            if (cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(Color.LTGRAY);
            }

            // Highlight selected date
            if (position == selectedPosition) {
                textView.setBackgroundColor(Color.parseColor("#6200EE"));
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }

            // Highlight today's date
            Calendar today = Calendar.getInstance();
            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                textView.setTextColor(Color.parseColor("#6200EE"));
                textView.setTextSize(14);
            }
        } else {
            textView.setText("");
        }

        return view;
    }

    public void refreshDays(Calendar month) {
        dateList.clear();
        currentDate = (Calendar) month.clone();

        // Get the first day of the month
        Calendar firstDay = (Calendar) month.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);

        // Get the day of week for the first day of month
        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);

        // Adjust for Monday as first day of week (Calendar uses Sunday as 1)
        firstDayOfWeek = firstDayOfWeek - 1;
        if (firstDayOfWeek == 0) {
            firstDayOfWeek = 7;
        }

        // Fill the calendar with days
        Calendar day = (Calendar) firstDay.clone();
        day.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek + 1);

        // 6 weeks to display
        for (int i = 0; i < 42; i++) {
            dateList.add(day.getTime());
            day.add(Calendar.DAY_OF_MONTH, 1);
        }

        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }
}