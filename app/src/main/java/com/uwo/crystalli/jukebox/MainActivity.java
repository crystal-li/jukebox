package com.uwo.crystalli.jukebox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;

import android.provider.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

   /* public final static String EXTRA_MESSAGE = "com.uwo.crystalli.jukebox.MESSAGE";

    public void sendMessage(View view) {

        Intent intent = new Intent(this, ConnectHere.class);
        EditText editText = (EditText) findViewById(R.id.Enter_Your_Name);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    } */ //Send input message to next screen

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


    /*Context context = getApplicationContext();
    CharSequence text = "Hello toast!";
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();*/
}

