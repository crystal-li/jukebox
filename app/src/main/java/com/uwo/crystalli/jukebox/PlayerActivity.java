package com.uwo.crystalli.jukebox;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Queue;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Set up button onClick events
        ImageButton addMediaBtn = (ImageButton) findViewById(R.id.add_media_btn);
        addMediaBtn.setOnClickListener(this);

        //TODO: enable play/skip based on settings or host/guest state
        ImageButton playBtn = (ImageButton) findViewById(R.id.play_btn);
        playBtn.setEnabled(false);
        //playBtn.setOnClickListener(this);

        ImageButton skipBtn = (ImageButton) findViewById(R.id.skip_btn);
        skipBtn.setEnabled(false);
        //skipBtn.setOnClickListener(this);

        ImageButton viewQueueBtn = (ImageButton) findViewById(R.id.view_queue_btn);
        viewQueueBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_media_btn:
                Intent addMediaIntent = new Intent(this, AddMediaActivity.class);
                startActivity(addMediaIntent);
                break;

            case R.id.play_btn:
                //TODO: implement
                break;

            case R.id.skip_btn:
                //TODO: implement
                break;

            case R.id.view_queue_btn:
                Intent queueIntent = new Intent(this, QueueActivity.class);
                startActivity(queueIntent);
                break;

        }
    }
}
