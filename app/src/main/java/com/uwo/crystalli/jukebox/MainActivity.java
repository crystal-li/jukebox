package com.uwo.crystalli.jukebox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: This should go in the connection activity later */
        //TODO: Maybe PlayerActivity and ConnectionActivity should be fragments (?)

        Boolean isHost = ((GlobalApplicationState) this.getApplication()).isHost();

        if (isHost == null) {
            Intent intent = new Intent(this, ConnectHere.class);
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
}
