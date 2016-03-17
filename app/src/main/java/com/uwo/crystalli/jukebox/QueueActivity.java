package com.uwo.crystalli.jukebox;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueueActivity extends AppCompatActivity {

    private final String LOG_TAG = QueueActivity.class.getSimpleName();
    private ArrayAdapter<Media> QueueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        QueueAdapter =
                new ArrayAdapter<Media>(
                        this, // The current context (this activity)
                        R.layout.populate_queue_items, // The name of the layout ID.
                        R.id.queue_list_item_textview, // The ID of the textview to populate.
                        new ArrayList<Media>());


        // Get a reference to the ListView, and attach this adapter to it.
        final ListView searchResultsListView =
                (ListView) findViewById(R.id.queue_listview);
        searchResultsListView.setAdapter(QueueAdapter);

        populateMediaQueueTask();

        }

    public void populateMediaQueueTask ()
    {
        PopulateMediaQueueTask PopulateQueueTask = new PopulateMediaQueueTask();
        Integer hostId = ((GlobalApplicationState) getApplication()).getHostId();
        PopulateQueueTask.execute(hostId);
    }

    //This sends a POST request to the Jukebox API to get all items from queue
    // for this hostId and returns the response code.
    public class PopulateMediaQueueTask extends AsyncTask<Integer, Void, ArrayList<Media>> {

        private final String LOG_TAG = PopulateMediaQueueTask.class.getSimpleName();

        private ArrayList<Media> getDataFromJson(String resultJsonString)
                throws JSONException {

            JSONArray videoJsonArray = new JSONArray(resultJsonString);

            ArrayList<Media> videoResultArrayList = new ArrayList<Media>();

            for (int i = 0; i < videoJsonArray.length(); i++) {

                // Extract the fields we need from the JSON object and
                // construct a videoResult object
                JSONObject videoObject = videoJsonArray.getJSONObject(i);
                String dbId = videoObject.getString("_id");
                String videoTitle = videoObject.getString("title");
                String videoId = videoObject.getString("videoId");
                String thumbUrl = videoObject.getString("extra");

                //TODO: add dbId
                Media videoResult = new Media(videoId, videoTitle, thumbUrl, dbId);
                videoResultArrayList.add(videoResult);
            }

            return videoResultArrayList;
        }

        @Override
        protected ArrayList<Media> doInBackground(Integer... params) {

            Integer hostId = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videoResultsString = null;

            try {
                // Construct the URL for the Jukebox query
                final String JUKEBOX_URL = "http://jukebox1234.herokuapp.com/media/host/";

                URL url = new URL(JUKEBOX_URL + Integer.toString(hostId));

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

        protected void onPostExecute (ArrayList < Media > videos) {
                QueueAdapter.clear();
                if (videos != null) {
                    QueueAdapter.addAll(videos);
                    Log.v(LOG_TAG, videos.toString());
                } else Log.v(LOG_TAG, "our result is null ):");
            }
        }
}


