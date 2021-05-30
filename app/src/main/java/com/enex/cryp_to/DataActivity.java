package com.enex.cryp_to;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class DataActivity extends AppCompatActivity {
    TextView dataIndicator;
    ImageView back;
    LineChart chart;
    String apiUrl,dataApiUrl;
    RequestQueue queue;
    TextView rank,name,symbol,price,change,marketCap,vwap,learnMoreUrl;
    CardView learnMore;
    public static final String TAG = "DataActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Intent intent = getIntent();
        String currencyLower = intent.getStringExtra("currency");
        String currency = capitalize(currencyLower);
        dataIndicator = findViewById(R.id.dataHeader);
        dataIndicator.setText("7 days data for "+currency);
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
        dataApiUrl = "https://api.coincap.io/v2/assets/"+currencyLower;


        name = findViewById(R.id.nameOfCurrency);
        name.setText(currency);
        rank = findViewById(R.id.rankOfCurrency);
        price = findViewById(R.id.priceOfCurrency);
        symbol = findViewById(R.id.symbolOfCurrency);
        marketCap = findViewById(R.id.marketCapCurrency);
        change = findViewById(R.id.changeOfCurrency);
        learnMoreUrl = findViewById(R.id.learnMoreUrl);
        learnMore = findViewById(R.id.learnMoreCard);
        vwap = findViewById(R.id.vwapOfCurrency);
        fetchData();
        setupTextData();

    }

    public static String capitalize(String str){
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public void gotoDeveloperIg(){
        String username = "_maneesh_pandey";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://instagram.com/_u/" + username));
            intent.setPackage("com.instagram.android");
            startActivity(intent);
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            showToast("Instagram app not found! Opening instagram in browser.");
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/" + username)));
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public void fetchData(){
        FetchData fetchData = new FetchData(queue,apiUrl);
        new Thread(fetchData).start();
    }
    public void setupChart(LineChart chart,String currency){
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setTouchEnabled(false);
        chart.setNoDataText("Chart will update once data is fetched from server!");
        // hiding grid
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setTextColor(Color.rgb(26,26,26));
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(true);
        chart.getAxisRight().setTextColor(Color.rgb(26,26,26));
        chart.getAxisLeft().setEnabled(true);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.rgb(26,26,26));

        chart.animateXY(700,700, Easing.EaseInOutBack);
    }

    public void manageData(ArrayList<Entry> dataList){
        LineDataSet set = new LineDataSet(dataList,"Price for last 7 days in USD");
        set.setColor(Color.rgb(255,255,255));
        set.setLineWidth(1f);
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
        String apiURL,temp;
        double price;
        DecimalFormat formatter = new DecimalFormat("#.##");
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
                        showToast("No internet connection!");
                        exitDialog("No internet connection",
                                "Data cannot be shown without internet connection. If you are connected to internet and the error keeps occuring, please contact developer!");
                    } else {
                        showToast("Error: " + error.getMessage());
                    }
                }
            }
            );
            queue.add(jsonObjectRequest);
        }
    }


    public void setupTextData(){
        TextData textData = new TextData(queue,dataApiUrl);
        new Thread(textData).start();
    }

    @SuppressLint("SetTextI18n")
    public void textDataEntry(int rankValue,
                              double priceValue,
                              double marketcapValue,
                              Double changeValue,
                              String nameValue,
                              String symbolValue,
                              String explorerValue,
                              double volumeAveragePrice){
        DecimalFormat f = new DecimalFormat("#.##");

        rank.setText("#"+rankValue);
        price.setText("$ "+f.format(priceValue));
        marketCap.setText("$ "+f.format(marketcapValue));
        change.setText(f.format(changeValue) + " %");
        vwap.setText("$ "+f.format(volumeAveragePrice));
        name.setText(nameValue);
        symbol.setText(symbolValue);
//        learnMoreUrl.setText(explorerValue);
        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(explorerValue));
                startActivity(intent);
            }
        });

    }


    public void aboutVwap(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About VWAP");
        builder.setIcon(R.drawable.ic_baseline_info_dark);
        builder.setCancelable(false);
        builder.setMessage("According to Wikipedia,\n\nhttps://en.wikipedia.org/wiki/Volume-weighted_average_price\n\n" +
                "In finance, volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day). It is a measure of the average price at which a stock is traded over the trading horizon.\n"
                );
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Visit Wiki Page",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://en.wikipedia.org/wiki/Volume-weighted_average_price"));
                startActivity(intent);
            }
        });
        builder.show();

    }

    public void exitDialog(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_baseline_warning_24);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.setNeutralButton("Contact Developer",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDeveloperIg();
                finish();
            }
        });
        builder.show();
    }

    public class TextData implements Runnable{
        RequestQueue queue;
        String apiURL;

        public TextData(RequestQueue queue, String apiURL) {
            this.queue = queue;
            this.apiURL = apiURL;
        }

        double price, marketcap,vwap;
        double hour24change;
        int rank;
        String symbol,urlExplorer,name;
        @Override
        public void run() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>(

                ) {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("data");
                            price = object.getDouble("priceUsd");
                            hour24change = object.getDouble("changePercent24Hr");
                            marketcap = object.getDouble("marketCapUsd");
                            rank = object.getInt("rank");
                            vwap = object.getDouble("vwap24Hr");
//                            Log.i("dataText","Data: "+price+" Cap: "+f.format("%.2f",marketcap));
                            symbol = object.getString("symbol");
                            name = object.getString("name");
                            urlExplorer = object.getString("explorer");
                            textDataEntry(rank,price,marketcap,hour24change,name,symbol,urlExplorer,vwap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error", "Error: " + error);
                        if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
//                            showToast("It seems like you do not have internet connection!");
                            showToast("No internet connection!");

                            exitDialog("No internet connection",
                                    "Data cannot be shown without internet connection. If you are connected to internet and the error keeps occuring, please contact developer!");
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