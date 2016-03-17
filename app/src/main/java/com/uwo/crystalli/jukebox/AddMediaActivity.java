package com.uwo.crystalli.jukebox;

import android.net.Uri;
import android.os.AsyncTask;
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

public class AddMediaActivity extends AppCompatActivity {

    private final String LOG_TAG = AddMediaActivity.class.getSimpleName();
    private ArrayAdapter<Media> mSearchResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        SearchView searchMediaView = (SearchView) findViewById(R.id.media_search_view);

        searchMediaView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateSearchResults(query);
                //TODO: Handle if the query fails
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchResults(newText);
                return true;
            }
        });

        mSearchResultsAdapter =
                new ArrayAdapter<Media>(
                        this, // The current context (this activity)
                        R.layout.list_item_search_result, // The name of the layout ID.
                        R.id.list_item_search_result_textview, // The ID of the textview to populate.
                        new ArrayList<Media>());

        // Get a reference to the ListView, and attach this adapter to it.
        final ListView searchResultsListView =
                (ListView) findViewById(R.id.media_search_results_listview);
        searchResultsListView.setAdapter(mSearchResultsAdapter);
        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Media video = ((Media) searchResultsListView.getItemAtPosition(i));
                addMediaToJukebox(video);
            }
        });
    }

    private void updateSearchResults(String query) {
        SearchYoutubeTask searchYoutubeTask = new SearchYoutubeTask();
        searchYoutubeTask.execute(query);
    }

    private void addMediaToJukebox(Media video) {
        addMediaToJukeboxTask addMediaTask = new addMediaToJukeboxTask();
        addMediaTask.execute(video);
    }

    //TODO: Is there a better way to abstract out HTTP requests? Put it in a class?
            //This sends a POST request to the Jukebox API to add media from Youtube
            // and returns the response code.
    public class addMediaToJukeboxTask extends AsyncTask<Media, Void, Integer> {

        private final String LOG_TAG = addMediaToJukeboxTask.class.getSimpleName();

        @Override
        protected Integer doInBackground(Media... v) {

            Media video = v[0]; //TODO: This is stupid.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //HTTP request parameters
            String title = video.title;
            String videoId = video.getVideoId();
            Integer hostId = ((GlobalApplicationState) getApplication()).getHostId();
            String type = "youtube";
            String extra = video.thumbUrl;

            try {
                // Construct the URL for the Youtube query
                //TODO: change this and put API on remote server
                final String JUKEBOX_BASE_URL = "http://jukebox1234.herokuapp.com/media/";
                final String TITLE_PARAM = "title";
                final String TYPE_PARAM = "type";
                final String VIDEO_ID_PARAM = "videoId";
                final String EXTRA_PARAM = "extra";
                final String HOST_ID_PARAM = "hostId";

                URL url = new URL(JUKEBOX_BASE_URL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                // Add POST parameters
                List<AbstractMap.SimpleEntry> params = new ArrayList<AbstractMap.SimpleEntry>();
                params.add(new AbstractMap.SimpleEntry(TITLE_PARAM, title));
                params.add(new AbstractMap.SimpleEntry(TYPE_PARAM, type));
                params.add(new AbstractMap.SimpleEntry(VIDEO_ID_PARAM, videoId));
                params.add(new AbstractMap.SimpleEntry(EXTRA_PARAM, extra));
                params.add(new AbstractMap.SimpleEntry(HOST_ID_PARAM, hostId));

                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                outputStream.close();

                return urlConnection.getResponseCode();

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

        private String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (AbstractMap.SimpleEntry pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getKey().toString(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
            }

            return result.toString();
        }

        protected void onPostExecute(Integer responseCode) {
                String toastMessage;

                if (responseCode == 200) {
                    toastMessage = "Successfully added to queue :)";
                } else {
                    toastMessage = "Can't add to queue ):";
                }

                Toast toast = Toast.makeText(getApplicationContext(),
                        toastMessage, Toast.LENGTH_SHORT);
                toast.show();
        }
    }

    public class SearchYoutubeTask extends AsyncTask<String, Void, ArrayList<Media>> {

        private final String LOG_TAG = SearchYoutubeTask.class.getSimpleName();

        private ArrayList<Media> getDataFromJson(String resultJsonString)
                throws JSONException {

            //TODO: change all the json param names to variables like below
            // These are the names of the JSON objects that need to be extracted.
            // final String _ITEMS = "items";
            // final String _SNIPPET = "snippet";

            JSONObject resultJson = new JSONObject(resultJsonString);
            JSONArray videoJsonArray = resultJson.getJSONArray("items");

            ArrayList<Media> videoResultsList = new ArrayList<Media>();

            for(int i = 0; i < videoJsonArray.length(); i++) {

                // Extract the fields we need from the JSON object and
                // construct a videoResult object
                JSONObject videoObject = videoJsonArray.getJSONObject(i);
                String videoTitle = videoObject.getJSONObject("snippet").getString("title");
                String videoId = videoObject.getJSONObject("id").getString("videoId");
                String thumbUrl = videoObject.getJSONObject("snippet")
                        .getJSONObject("thumbnails")
                        .getJSONObject("high")
                        .getString("url");

                //TODO: Here we don't need a dbID
                videoResultsList.add(new Media(videoId, videoTitle, thumbUrl, "n/a"));
            }

            return videoResultsList;
        }

        @Override
        protected ArrayList<Media> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videoResultsString = null;

            //HTTP request parameters
            int maxResults = 10;
            String typeString = "video";
            //TODO: put in API Key properly
            String apiKey = ((GlobalApplicationState) getApplication()).getYoutubeApiKey();
            String partString = "id, snippet";
            String queryString = params[0];

            try {
                // Construct the URL for the Youtube query
                final String YOUTUBE_BASE_URL =
                        "https://www.googleapis.com/youtube/v3/search?";
                final String PART_PARAM = "part";
                final String MAX_RESULTS_PARAM = "maxResults";
                final String QUERY_PARAM = "q";
                final String TYPE_PARAM = "type";
                final String API_KEY_PARAM = "key";

                Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                        .appendQueryParameter(PART_PARAM, partString)
                        .appendQueryParameter(MAX_RESULTS_PARAM, Integer.toString(maxResults))
                        .appendQueryParameter(QUERY_PARAM, queryString)
                        .appendQueryParameter(TYPE_PARAM, typeString)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

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

            try {
                return getDataFromJson(videoResultsString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(ArrayList<Media> videos) {
            mSearchResultsAdapter.clear();
            if (videos != null) {
                Log.v(LOG_TAG, videos.toString());
                mSearchResultsAdapter.addAll(videos);
            }
        }
    }

}
