package com.uwo.crystalli.jukebox;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;

// Youtube API Key: AIzaSyDJpskdkcvZ_6coBGE0hzznNr4sjbQGNno

public class AddMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        SearchView searchMediaView = (SearchView) findViewById(R.id.media_search_view);

        searchMediaView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //searchYoutubeAPI function



}
