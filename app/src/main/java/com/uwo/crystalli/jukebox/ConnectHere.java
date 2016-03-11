package com.uwo.crystalli.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


public class ConnectHere extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_here);



        Button HostBtn = (Button) findViewById(R.id.host_btn);

        HostBtn.setOnClickListener(this);

        Button GuestBtn = (Button) findViewById(R.id.guest_btn);
        GuestBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.host_btn:

                Context context = getApplicationContext();
                CharSequence text = "Successfully Connected as Host :)";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                ((GlobalApplicationState) this.getApplication()).setHost(true);
                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
                break;

            case R.id.guest_btn:

                context = getApplicationContext();
                text = "Successfully Connected as Guest :)";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();

                ((GlobalApplicationState) this.getApplication()).setHost(false);
                intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
