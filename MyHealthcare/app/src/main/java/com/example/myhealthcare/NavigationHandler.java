package com.example.myhealthcare;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class NavigationHandler {

    private AppCompatActivity activity;
    private LinearLayout homeScreen;
    private LinearLayout firstAidScreen;
    private LinearLayout hospitalScreen;
    private LinearLayout nutritionScreen;
    private LinearLayout diseaseScreen;

    private ImageView navProfile;
    private ImageView navHeart;
    private ImageView navCar;
    private ImageView navLocation;
    private ImageView navContact;

    private int currentScreen = 0;

    public NavigationHandler(AppCompatActivity activity,
                             LinearLayout homeScreen,
                             LinearLayout firstAidScreen,
                             LinearLayout hospitalScreen,
                             LinearLayout nutritionScreen,
                             LinearLayout diseaseScreen,
                             ImageView navProfile,
                             ImageView navHeart,
                             ImageView navCar,
                             ImageView navLocation,
                             ImageView navContact) {
        this.activity = activity;
        this.homeScreen = homeScreen;
        this.firstAidScreen = firstAidScreen;
        this.hospitalScreen = hospitalScreen;
        this.nutritionScreen = nutritionScreen;
        this.diseaseScreen = diseaseScreen;
        this.navProfile = navProfile;
        this.navHeart = navHeart;
        this.navCar = navCar;
        this.navLocation = navLocation;
        this.navContact = navContact;
    }

    public void setupNavigation() {
        navProfile.setOnClickListener(v -> showScreen(0));
        navHeart.setOnClickListener(v -> showScreen(1));
        navCar.setOnClickListener(v -> showScreen(2));
        navLocation.setOnClickListener(v -> showScreen(3));
        navContact.setOnClickListener(v -> showScreen(4));
    }

    public void showScreen(int screenIndex) {
        homeScreen.setVisibility(View.GONE);
        firstAidScreen.setVisibility(View.GONE);
        hospitalScreen.setVisibility(View.GONE);
        nutritionScreen.setVisibility(View.GONE);
        diseaseScreen.setVisibility(View.GONE);

        int blackColor = ContextCompat.getColor(activity, android.R.color.black);
        int purpleColor = ContextCompat.getColor(activity, R.color.purple);

        navProfile.setColorFilter(blackColor);
        navHeart.setColorFilter(blackColor);
        navCar.setColorFilter(blackColor);
        navLocation.setColorFilter(blackColor);
        navContact.setColorFilter(blackColor);

        switch (screenIndex) {
            case 0:
                homeScreen.setVisibility(View.VISIBLE);
                navProfile.setColorFilter(purpleColor);
                break;
            case 1:
                firstAidScreen.setVisibility(View.VISIBLE);
                navHeart.setColorFilter(purpleColor);
                // Gọi phương thức load dữ liệu và thiết lập list ở FirstAidScreen
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).loadFirstAidDataAndSetupList();
                }
                break;
            case 2:
                hospitalScreen.setVisibility(View.VISIBLE);
                navCar.setColorFilter(purpleColor);
                break;
            case 3:
                nutritionScreen.setVisibility(View.VISIBLE);
                navLocation.setColorFilter(purpleColor);
                break;
            case 4:
                diseaseScreen.setVisibility(View.VISIBLE);
                navContact.setColorFilter(purpleColor);
                break;
        }
        currentScreen = screenIndex;
    }
}