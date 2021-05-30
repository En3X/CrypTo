package com.enex.cryp_to;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LinearLayout layout;
    ListView newslist;
    TextView newsholderview;
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    int dimen,margin;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // toolbar configuration
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toolbar configuration ends here
        layout = findViewById(R.id.scrollerView);
        dimen = dpToPx(150,this);
        margin = dpToPx(20,this);
        holderImageSetup(layout,dimen,margin);
        // Contact developer control
        queue = Volley.newRequestQueue(this);
        fetchNewsData();
        newslist = findViewById(R.id.newsList);

    }

    public void enterNewsDataInList(ArrayList<String> dataList){
        newsholderview = findViewById(R.id.newstextview);
        newsholderview.setText("Crypto News");
        adapter = new ArrayAdapter<String>(this,R.layout.list_view,R.id.listOfNews,dataList);
        newslist.setAdapter(adapter);
        newslist.getDivider().setColorFilter(Color.WHITE, PorterDuff.Mode.LIGHTEN);
    }

    public void fetchNewsData(){
        News news = new News(this,queue,20);
        new Thread(news).start();
    }
    public class News implements Runnable{
        Context context;
        RequestQueue queue;
        int lengthOfNews;
        public News(Context context, RequestQueue queue,int lengthOfNews) {
            this.context = context;
            this.queue = queue;
            this.lengthOfNews = lengthOfNews;
        }
        String title;
        ArrayList<String> dataList,sourcelist;
        ArrayAdapter<String> arrayAdapter;
        @Override
        public void run() {
            String[] source = new String[lengthOfNews];
            String[] newsDataArray = new String[lengthOfNews];
            Random rand = new Random();
            int randomNum = rand.nextInt((10 - 1) + 1) + 1;


            String apiurl = "https://cryptopanic.com/api/v1/posts/?auth_token=bde4b75c734b5340f15130617bea538744d64dc7&public=true&page="+randomNum;
            JsonObjectRequest jsonData = new JsonObjectRequest(Request.Method.GET, apiurl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray result = response.getJSONArray("results");
                                if (result.length()>0){
                                    for (int i=0;i<lengthOfNews;i++){
                                        JSONObject data = result.getJSONObject(i);
                                        title = "#"+(int)(i+1)+" "+ data.getString("title");
                                        newsDataArray[i] = title;
                                        source[i] = "Source: "+ data.getString("domain");
                                    }
                                    dataList = new ArrayList<>(Arrays.asList(newsDataArray));
                                    enterNewsDataInList(dataList);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "Error: " + error);
                    if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        toast("No internet connection!");

                        exitDialog("No internet connection",
                                "It seems like you are not connected to internet.\nMost of the features of the app relies upon internet to work." +
                                        "You can still use application but might not be able to get info and data on any thing.\n" +
                                        "If you are connected to internet and the error keeps occuring, please contact developer!");

                    } else {
                        toast("Error: " + error.getMessage());
                    }
                }
            });
            queue.add(jsonData);        }
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
            toast("Instagram app not found! Opening instagram in browser.");
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/" + username)));
        }
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
            }
        });
        builder.setNeutralButton("Contact Developer",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDeveloperIg();
            }
        });
        builder.show();
    }

    public void gotoAboutpage(){
        toast("About us page is yet to be made!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("itemid","Id: "+item.getItemId());
        /*
            Item ids
                - Setting: 2131296678
                - Contact developer: 2131296369
                -About app: 2131296270
         */
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.contactDeveloper:
                gotoDeveloperIg();
                break;
            case R.id.aboutApp:
                gotoAboutpage();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public void toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
    public void holderImageSetup(LinearLayout layout,int size,int margin){
        Context context = this;
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               String [] holders = {"holder_bitcoin","holder_ethereum",
                       "holder_xrp","holder_cardano","holder_doge","holder_tether","holder_palkadot",
                       "holder_btc_cash","holder_litecoin","holder_uniswap","holder_chainlink","holder_stellar",
                       "holder_usdcoin","holder_monero"};
               String[] currency = {
                       "bitcoin","ethereum","xrp","cardano","dogecoin","tether","polkadot","bitcoin-cash",
                       "litecoin","uniswap","chainlink","stellar","usd-coin","monero"
               };

               for (int i=0;i<holders.length;++i){
                   int drawable = getResources().getIdentifier(holders[i],"drawable",getPackageName());
                   ImageView view = new ImageView(context);
                   view.setImageResource(drawable);
                   layout.addView(view);
                   int finalI = i;
                   view.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
//                           toast("Selected "+goTo[finalI]);
                           Intent intent = new Intent(context,DataActivity.class);
                           intent.putExtra("currency",currency[finalI]);
                            startActivity(intent);
                       }
                   });
                   view.requestLayout();
                   view.getLayoutParams().height = size;
                   view.getLayoutParams().width = size;
                   // Setting parameters
                   LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                   lp.setMargins(0,0,margin,0);
                   view.setLayoutParams(lp);
                   view.setScaleType(ImageView.ScaleType.FIT_XY);
               }
           }
       });
    }
}