package com.uwo.crystalli.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractSequentialList;
import java.util.concurrent.ExecutionException;


public class ConnectHere extends AppCompatActivity implements View.OnClickListener {

    private final String LOG_TAG = AddMediaActivity.class.getSimpleName();
    private Integer mEnteredId;

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
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.host_btn:

                //We've actually made this a serial task
                // The user will have to wait until the server sends them the host ID.
                Integer hostId = addHost();

                //Also some work done in onPostExecute()
                if (hostId != null) {
                    ((GlobalApplicationState) this.getApplication()).setHost(true);
                    Intent intent = new Intent(this, PlayerActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.guest_btn:

                EditText editText = (EditText) findViewById(R.id.host_id_edit_text);
                try {
                    mEnteredId = Integer.parseInt(editText.getText().toString());
                    ConnectToHostTask connectToHostTask = new ConnectToHostTask();
                    connectToHostTask.execute(mEnteredId);

                } catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "Error", e);
                    String toastMessage = "Please enter a valid ID (all numbers)";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    private Integer addHost() {
        AddHostTask addHostTask = new AddHostTask();
        addHostTask.execute();
        try {
            return addHostTask.get();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        }
    }
    /*
    private boolean connectToHost(Integer enteredId) {
        try {

            ConnectToHostTask connectToHostTask = new ConnectToHostTask();
            String result = connectToHostTask.execute(enteredId).get();

            String toastMessage;

            if (result.equals("TRUE")) { //Host exists
                toastMessage = "Connected to host " + Integer.toString(enteredId);
                ((GlobalApplicationState) getApplication()).setHostId(enteredId);
                Toast toast = Toast.makeText(getApplicationContext(),
                         toastMessage, Toast.LENGTH_SHORT);
                toast.show();
                return true;

            } else if (result.equals("FALSE")) { //Host does not exist
                toastMessage = "That host doesn't exist! Please try again.";
                Toast toast = Toast.makeText(getApplicationContext(),
                        toastMessage, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        } catch (Exception e) {

            String toastMessage = "Something went wrong ):";
            Toast toast = Toast.makeText(getApplicationContext(),
                    toastMessage, Toast.LENGTH_SHORT);
            toast.show();

            Log.e(LOG_TAG, "Error", e);
            return false;
        }

        return false;
    }
    */

    //This sends a request to add a new host and returns the hostId if successful
    public class AddHostTask extends AsyncTask<Void, Void, Integer> {

        private final String LOG_TAG = AddHostTask.class.getSimpleName();

        @Override
        protected Integer doInBackground(Void... v) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the Youtube query
                final String REQ_URL = "http://jukebox1234.herokuapp.com/hosts";

                URL url = new URL(REQ_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer;
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                // Read the input stream into a String
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));



                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String hostResultString = buffer.toString();
                Log.v(LOG_TAG, hostResultString);

                if (hostResultString == null) return null;

                try {
                    return getHostIdFromJson(hostResultString);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        private Integer getHostIdFromJson(String hostIdString)
                throws JSONException {

            JSONObject hostResult = new JSONObject(hostIdString);
            Integer hostId = hostResult.getInt("hostId");

            return hostId;
        }


        //TODO: Might not need this since this is a serial task anyways - we can do this
        // in onCreate()
        protected void onPostExecute(Integer hostId) {

            String toastMessage;

            if (hostId != null) {
                toastMessage = "Successfully set as host " + Integer.toString(hostId);
                ((GlobalApplicationState) getApplication()).setHostId(hostId);
            } else {
                toastMessage = "Could not connect. Please try again later. ): ";
            }

            Toast toast = Toast.makeText(getApplicationContext(),
                    toastMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class ConnectToHostTask extends AsyncTask<Integer, Void, String> {

        private final String LOG_TAG = AddHostTask.class.getSimpleName();

        @Override
        protected String doInBackground(Integer... params) {

            Integer hostId = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the Youtube query
                final String REQ_URL = "http://jukebox1234.herokuapp.com/hosts/";

                URL url = new URL(REQ_URL + Integer.toString(hostId));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer;
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                // Read the input stream into a String
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String resultString = buffer.toString();
                Log.v(LOG_TAG, resultString);

                if (resultString == null) return null;

                try {
                    return resultString;

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                String toastMessage;
                String success = s.trim();

                if (success.equals("TRUE")) { //Host exists
                    Log.v(LOG_TAG, "success equals TRUE");
                    toastMessage = "Connected to host " + Integer.toString(mEnteredId);
                    ((GlobalApplicationState) getApplication()).setHostId(mEnteredId);
                    ((GlobalApplicationState) getApplication()).setHost(false);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    startActivity(intent);

                } else if (success.equals("FALSE")) { //Host does not exist
                    Log.v(LOG_TAG, "success equals FALSE");
                    toastMessage = "That host doesn't exist! Please try again.";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

            } catch (Exception e) {

                String toastMessage = "Something went wrong ):";
                Toast toast = Toast.makeText(getApplicationContext(),
                        toastMessage, Toast.LENGTH_SHORT);
                toast.show();

                Log.e(LOG_TAG, "Error", e);
            }
        }
    }
}
