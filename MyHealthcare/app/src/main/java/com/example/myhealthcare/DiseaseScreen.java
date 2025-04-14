package com.example.myhealthcare;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

public class DiseaseScreen {

    private AppCompatActivity activity;
    private LinearLayout diseaseScreenLayout;
    private ListView diseaseListView;
    private ArrayList<DiseaseItem> diseaseData;
    private DiseaseAdapter diseaseAdapter;

    private static final String BASE_URL = "http://192.168.2.9:3000";
    private static final String DISEASE_API = BASE_URL + "/api/benh-co-ban";

    public DiseaseScreen(AppCompatActivity activity, LinearLayout diseaseScreenLayout) {
        this.activity = activity;
        this.diseaseScreenLayout = diseaseScreenLayout;
        this.diseaseListView = diseaseScreenLayout.findViewById(R.id.disease_list);
    }

    public void loadDataAndSetupList() {
        fetchDiseaseDataFromServer();

        ImageButton backButton = diseaseScreenLayout.findViewById(R.id.back_button_disease);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getNavigationHandler().showScreen(0);
                }
            });
        }
    }

    private void fetchDiseaseDataFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL(DISEASE_API);
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

                    diseaseData = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        DiseaseItem item = new DiseaseItem(
                                obj.getString("ten"),
                                obj.getString("trieu_chung"),
                                obj.getString("nguyen_nhan"),
                                obj.getString("cach_dieu_tri"),
                                obj.getString("url_anh")
                        );
                        diseaseData.add(item);
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        diseaseAdapter = new DiseaseAdapter(activity, diseaseData);
                        diseaseListView.setAdapter(diseaseAdapter);
                    });
                } else {
                    Log.e("DiseaseScreen", "Lỗi response code: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("DiseaseScreen", "Lỗi fetch dữ liệu từ server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static class DiseaseItem {
        String ten;
        String trieuChung;
        String nguyenNhan;
        String cachDieuTri;
        String urlAnh;

        DiseaseItem(String ten, String trieuChung, String nguyenNhan, String cachDieuTri, String urlAnh) {
            this.ten = ten;
            this.trieuChung = trieuChung;
            this.nguyenNhan = nguyenNhan;
            this.cachDieuTri = cachDieuTri;
            this.urlAnh = urlAnh;
        }
    }

    private static class DiseaseAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<DiseaseItem> data;

        DiseaseAdapter(Context context, ArrayList<DiseaseItem> data) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.popup_benh, parent, false);
            }
            TextView tenBenhTextView = convertView.findViewById(R.id.ten_benh_text);
            TextView trieuChungTextView = convertView.findViewById(R.id.trieu_chung_text);
            Button chiTietButton = convertView.findViewById(R.id.chi_tiet_benh_button);

            DiseaseItem item = data.get(position);
            tenBenhTextView.setText(item.ten);
            trieuChungTextView.setText("Triệu chứng: " + item.trieuChung);

            chiTietButton.setOnClickListener(v -> showChiTietBenhPopup(item));

            return convertView;
        }

        private void showChiTietBenhPopup(DiseaseItem item) {
            // Tạo và hiển thị dialog chi tiết bệnh (tương tự popup sơ cứu)
            // Bạn cần tạo layout popup_chi_tiet_benh.xml
            // và hiển thị thông tin đầy đủ của bệnh (tên, triệu chứng, nguyên nhân, cách điều trị, ảnh)
            // Sử dụng AlertDialog.Builder hoặc Dialog tùy chỉnh
            Log.d("DiseaseAdapter", "Xem chi tiết bệnh: " + item.ten);
        }
    }
}