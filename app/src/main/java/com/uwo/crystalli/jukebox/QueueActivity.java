package com.uwo.crystalli.jukebox;

import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {

    private final String LOG_TAG = QueueActivity.class.getSimpleName();
    private ArrayAdapter<VideoResult> QueueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        QueueAdapter =
                new ArrayAdapter<VideoResult>(
                        this, // The current context (this activity)
                        R.layout.populate_queue_items, // The name of the layout ID.
                        R.id.queue_list_item_textview, // The ID of the textview to populate.
                        new ArrayList<VideoResult>());


        // Get a reference to the ListView, and attach this adapter to it.
        final ListView searchResultsListView =
                (ListView) findViewById(R.id.queue_listview);
        searchResultsListView.setAdapter(QueueAdapter);

        }


    public void PopulateMediaQueueTask ()
    {
        PopulateMediaQueueTask PopulateQueueTask = new PopulateMediaQueueTask();
        PopulateQueueTask.execute();}

        //This sends a POST request to the Jukebox API to Pull item from queue
    // and returns the response code.
    public class PopulateMediaQueueTask extends AsyncTask<Void, Void, ArrayList<VideoResult>> {

        private final String LOG_TAG = PopulateMediaQueueTask.class.getSimpleName();

        private ArrayList<VideoResult> getDataFromJson(String resultJsonString)
                throws JSONException {


            JSONObject resultJson = new JSONObject(resultJsonString);
            JSONArray videoJsonArray = resultJson.getJSONArray("items");

            ArrayList<VideoResult> videoResultArrayList = new ArrayList<VideoResult>();

            for (int i = 0; i < videoJsonArray.length(); i++) {

                // Extract the fields we need from the JSON object and
                // construct a videoResult object
                JSONObject videoObject = videoJsonArray.getJSONObject(i);
                String videoTitle = videoObject.getJSONObject("snippet").getString("title");
                String videoId = videoObject.getJSONObject("id").getString("videoId");
                String thumbUrl = videoObject.getJSONObject("snippet")
                        .getJSONObject("thumbnails")
                        .getJSONObject("default")
                        .getString("url");

                videoResultArrayList.add(new VideoResult(videoId, videoTitle, thumbUrl));
            }

            return videoResultArrayList;
        }

        @Override
        protected ArrayList<VideoResult> doInBackground(Void... params) {
        Log.v(LOG_TAG, "working in the back :)");
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videoResultsString = null;


            try {
                // Construct the URL for the Jukebox query
                final String JUKEBOX_URL = "http://jukebox1234.herokuapp.com/media/";

                URL url = new URL(JUKEBOX_URL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                videoResultsString = buffer.toString();
                Log.v(LOG_TAG, videoResultsString);
                try {
                    return getDataFromJson(videoResultsString);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                }
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {reader.close();
                    }

                    catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }

                }}

            }


            protected void onPostExecute (ArrayList < VideoResult > videos) {
                QueueAdapter.clear();
                if (videos != null)
                    Log.v(LOG_TAG, videos.toString());
                else Log.v(LOG_TAG, "our result is null ):");
                QueueAdapter.addAll(videos);

            }

        }}

