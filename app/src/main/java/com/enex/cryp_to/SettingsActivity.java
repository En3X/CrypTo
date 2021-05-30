package com.enex.cryp_to;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

//        // Number of news setup
//        Spinner spinner = findViewById(R.id.newsSpinner);
//        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.number_news,R.layout.news_spinner);
////        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        arrayAdapter.setNotifyOnChange(true);
//        spinner.setAdapter(arrayAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("back", "onOptionsItemSelected: "+item.getItemId());
        switch (item.getItemId()){
            case R.id.home:
            case 16908332:
                finish();break;

        }
        return super.onOptionsItemSelected(item);
    }
}