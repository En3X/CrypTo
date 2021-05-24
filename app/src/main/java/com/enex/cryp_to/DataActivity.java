package com.enex.cryp_to;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DataActivity extends AppCompatActivity {
    TextView dataIndicator;
    ImageView back;
    LineChart chart;
    String apiUrl;
    RequestQueue queue;
    public static final String TAG = "DataActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Intent intent = getIntent();
        String currencyLower = intent.getStringExtra("currency");
        String currency = capitalize(currencyLower);
        dataIndicator = findViewById(R.id.dataHeader);
        dataIndicator.setText("Data for "+currency);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chart = findViewById(R.id.graph);
        setupChart(chart,currency);
        queue = Volley.newRequestQueue(this);

        apiUrl = "https://api.coincap.io/v2/assets/"+currencyLower+"/history?interval=d1";

        fetchData();
    }
    public static String capitalize(String str){
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    protected void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public void fetchData(){
        FetchData fetchData = new FetchData(queue,apiUrl);
        new Thread(fetchData).start();
    }
    public void setupChart(LineChart chart,String currency){
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        // hiding grid
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);


        chart.animateXY(700,700, Easing.EaseInOutBack);
    }

    public void manageData(ArrayList<Entry> dataList){
        LineDataSet set = new LineDataSet(dataList,null);
        set.setColor(Color.rgb(235, 64, 52));
        set.setLineWidth(5f);
        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(set);
        LineData data = new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(14);
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public class FetchData implements Runnable{
        RequestQueue queue;
        String apiURL;
        double price;
        ArrayList<Entry> list = new ArrayList<>();
        public FetchData(RequestQueue queue,String apiURL) {
            this.queue = queue;
            this.apiURL = apiURL;
        }
        @Override
        public void run() {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>(

            ) {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONArray("data");
//                        Log.d("Length","Number of data: "+data.length());
                        for (int i=(data.length()-7);i<data.length();++i){
                            JSONObject objectData = data.getJSONObject(i);
                            price = objectData.getDouble("priceUsd");
                            list.add(new Entry(i,(float)price));
                        }
                        manageData(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "Error: " + error);
                    if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        showToast("It seems like you do not have internet connection!");
                    } else {
                        showToast("Error: " + error.getMessage());
                    }
                }
            }
            );
            queue.add(jsonObjectRequest);
        }
    }
}