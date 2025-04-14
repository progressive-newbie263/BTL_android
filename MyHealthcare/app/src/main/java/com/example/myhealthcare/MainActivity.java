package com.example.myhealthcare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;
    private GridView calendarGrid;
    private TextView monthYearText;
    private ImageButton prevMonth;
    private ImageButton nextMonth;
    private LinearLayout homeScreen;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;
    private boolean isSensorPresent = false;

    private LinearLayout firstAidScreen;
    private LinearLayout hospitalScreen;
    private LinearLayout nutritionScreen;
    private LinearLayout diseaseScreen;

    private ImageView navProfile; // Đã sửa thành ImageView
    private ImageView navHeart;  // Đã sửa thành ImageView
    private ImageView navCar;    // Đã sửa thành ImageView
    private ImageView navLocation; // Đã sửa thành ImageView
    private ImageView navContact;  // Đã sửa thành ImageView

    private NavigationHandler navigationHandler;
    private CalendarAdapter calendarAdapter;
    private Calendar currentMonth = Calendar.getInstance();
    private FirstAidScreen firstAidScreenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountTextView = findViewById(R.id.step_count_text);
        calendarGrid = findViewById(R.id.calendar_grid);
        monthYearText = findViewById(R.id.month_year_text);
        prevMonth = findViewById(R.id.prev_month);
        nextMonth = findViewById(R.id.next_month);
        homeScreen = findViewById(R.id.home_screen);

        firstAidScreen = findViewById(R.id.first_aid_screen);
        hospitalScreen = findViewById(R.id.hospital_screen);
        nutritionScreen = findViewById(R.id.nutrition_screen);
        diseaseScreen = findViewById(R.id.disease_screen);

        navProfile = findViewById(R.id.nav_profile); // Giữ nguyên findViewById
        navHeart = findViewById(R.id.nav_heart);   // Giữ nguyên findViewById
        navCar = findViewById(R.id.nav_car);     // Giữ nguyên findViewById
        navLocation = findViewById(R.id.nav_location); // Giữ nguyên findViewById
        navContact = findViewById(R.id.nav_contact);  // Giữ nguyên findViewById

        // Khởi tạo NavigationHandler và truyền các view cần thiết
        navigationHandler = new NavigationHandler(
                this,
                homeScreen,
                firstAidScreen,
                hospitalScreen,
                nutritionScreen,
                diseaseScreen,
                navProfile,
                navHeart,
                navCar,
                navLocation,
                navContact
        );
        navigationHandler.setupNavigation(); // Thiết lập listener cho thanh nav

        // Khởi tạo FirstAidScreen manager
        firstAidScreenManager = new FirstAidScreen(this, firstAidScreen);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            stepCountTextView.setText("0");
            Toast.makeText(this, "Step counter sensor not available", Toast.LENGTH_LONG).show();
            isSensorPresent = false;
        }

        findViewById(R.id.reset_button).setOnClickListener(v -> {
            stepCount = 0;
            stepCountTextView.setText(String.valueOf(stepCount));
            Toast.makeText(MainActivity.this, "Step count reset", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.save_button).setOnClickListener(v -> saveStepCount());

        setupCalendar();

        // Hiển thị màn hình chính khi khởi động
        navigationHandler.showScreen(0);
    }

    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(this, currentMonth);
        calendarGrid.setAdapter(calendarAdapter);

        updateMonthYearLabel();

        calendarGrid.setOnItemClickListener((parent, view, position, id) -> {
            calendarAdapter.setSelectedPosition(position);
            Date selectedDate = (Date) calendarAdapter.getItem(position);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate);
            Toast.makeText(MainActivity.this, "Selected: " + formattedDate, Toast.LENGTH_SHORT).show();
        });

        prevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            calendarAdapter.refreshDays(currentMonth);
            updateMonthYearLabel();
        });

        nextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            calendarAdapter.refreshDays(currentMonth);
            updateMonthYearLabel();
        });
    }

    private void updateMonthYearLabel() {
        monthYearText.setText("Tháng " + (currentMonth.get(Calendar.MONTH) + 1) + " năm " + currentMonth.get(Calendar.YEAR));
    }

    private void saveStepCount() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        Toast.makeText(this, "Saved " + stepCount + " steps for " + currentDate, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            stepCountTextView.setText(String.valueOf(stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Phương thức để lấy NavigationHandler nếu cần tương tác từ các file khác
    public NavigationHandler getNavigationHandler() {
        return navigationHandler;
    }

    // Phương thức để load dữ liệu sơ cứu và thiết lập ListView (được gọi từ NavigationHandler)
    public void loadFirstAidDataAndSetupList() {
        firstAidScreenManager.loadDataAndSetupList();
    }
}