package com.uwo.crystalli.jukebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: This should go in the connection activity later */
        //TODO: Maybe PlayerActivity and ConnectionActivity should be fragments (?)
        ((GlobalApplicationState) this.getApplication()).setHost(true);

        Boolean isHost = ((GlobalApplicationState) this.getApplication()).isHost();

        if (isHost == null) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }
        else if (isHost) {
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
        }
        else if (!isHost) {
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //TODO: get rid of this?
        if (id == R.id.action_connect) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
