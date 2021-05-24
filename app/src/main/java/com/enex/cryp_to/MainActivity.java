package com.enex.cryp_to;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    LinearLayout layout;
    int dimen,margin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.scrollerView);
        dimen = dpToPx(150,this);
        margin = dpToPx(20,this);
        holderImageSetup(layout,dimen,margin);
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