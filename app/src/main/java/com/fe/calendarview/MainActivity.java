package com.fe.calendarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = (CalendarView) findViewById(R.id.calendarview);
        calendarView.setDate("2016-12-31");
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                Toast.makeText(MainActivity.this, year+"=" + month + "=" + day, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
