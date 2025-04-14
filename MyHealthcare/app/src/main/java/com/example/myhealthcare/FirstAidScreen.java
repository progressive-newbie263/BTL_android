package com.example.myhealthcare;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FirstAidScreen {

    private AppCompatActivity activity;
    private LinearLayout firstAidScreenLayout;
    private ListView firstAidListView;
    private ArrayList<FirstAidItem> firstAidData;
    private FirstAidAdapter firstAidAdapter;

    // Sử dụng địa chỉ IP thực của máy chủ backend
    private static final String BASE_URL = "http://192.168.2.9:3000";
    private static final String FIRST_AID_API = BASE_URL + "/api/so-cuu";

    public FirstAidScreen(AppCompatActivity activity, LinearLayout firstAidScreenLayout) {
        this.activity = activity;
        this.firstAidScreenLayout = firstAidScreenLayout;
        this.firstAidListView = firstAidScreenLayout.findViewById(R.id.first_aid_list);
    }

    public void loadDataAndSetupList() {
        fetchFirstAidDataFromServer();

        ImageButton backButton = firstAidScreenLayout.findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getNavigationHandler().showScreen(0);
                }
            });
        }
    }

    private void fetchFirstAidDataFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL(FIRST_AID_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    reader.close();
                    JSONArray jsonArray = new JSONArray(responseBuilder.toString());

                    firstAidData = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        FirstAidItem item = new FirstAidItem(
                                obj.getString("ten"),
                                obj.getString("trieu_chung"),
                                obj.getString("viec_nen_lam"),
                                obj.getString("viec_khong_nen_lam"),
                                obj.getString("cach_xu_ly"),
                                obj.getString("url_anh")
                        );
                        firstAidData.add(item);
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        firstAidAdapter = new FirstAidAdapter(activity, firstAidData);
                        firstAidListView.setAdapter(firstAidAdapter);
                    });
                } else {
                    Log.e("FirstAidScreen", "Lỗi response code: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("FirstAidScreen", "Lỗi fetch dữ liệu từ server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static class FirstAidItem {
        String ten;
        String trieuChung;
        String viecNenLam;
        String viecKhongNenLam;
        String cachXuLy;
        String urlAnh;

        FirstAidItem(String ten, String trieuChung, String viecNenLam, String viecKhongNenLam, String cachXuLy, String urlAnh) {
            this.ten = ten;
            this.trieuChung = trieuChung;
            this.viecNenLam = viecNenLam;
            this.viecKhongNenLam = viecKhongNenLam;
            this.cachXuLy = cachXuLy;
            this.urlAnh = urlAnh;
        }
    }

    private static class FirstAidAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<FirstAidItem> data;

        FirstAidAdapter(Context context, ArrayList<FirstAidItem> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.flashcard_so_cuu, parent, false);
            }
            TextView tenBenhTextView = convertView.findViewById(R.id.ten_benh_text);
            Button huongDanButton = convertView.findViewById(R.id.huong_dan_button);
            ImageView anhBenhImageView = convertView.findViewById(R.id.anh_benh_image_view);

            FirstAidItem item = data.get(position);
            tenBenhTextView.setText(item.ten);

            // Sử dụng Glide để tải ảnh từ URL
            Glide.with(context)
                    .load(item.urlAnh)
                    .placeholder(R.drawable.ic_health) // Ảnh placeholder khi đang tải
                    .error(R.drawable.ic_health)       // Ảnh hiển thị khi có lỗi tải
                    .into(anhBenhImageView);

            huongDanButton.setOnClickListener(v -> showHuongDanPopup(item));

            return convertView;
        }

        private void showHuongDanPopup(FirstAidItem item) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_so_cuu);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView trieuChungTextView = dialog.findViewById(R.id.trieu_chung_text);
            TextView viecNenLamTextView = dialog.findViewById(R.id.viec_nen_lam_text);
            TextView viecKhongNenLamTextView = dialog.findViewById(R.id.viec_khong_nen_lam_text);
            TextView cachXuLyTextView = dialog.findViewById(R.id.cach_xu_ly_text);
            Button closeButton = dialog.findViewById(R.id.close_button);

            trieuChungTextView.setText(item.trieuChung);
            viecNenLamTextView.setText(item.viecNenLam);
            viecKhongNenLamTextView.setText(item.viecKhongNenLam);
            cachXuLyTextView.setText(item.cachXuLy);

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }
}