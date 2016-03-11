package com.uwo.crystalli.jukebox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_connection, menu);
        return true;
    }

    private final String LOG_TAG = PlayerActivity.class.getSimpleName();
    YouTubePlayer mYoutubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Boolean isHost = ((GlobalApplicationState) this.getApplication()).isHost();

        //TODO: what if it's null? shouldn't be but just in case?
        if (savedInstanceState == null && isHost) {
            addYoutubePlayerFragment();
        }

        //Set up button onClick events
        ImageButton addMediaBtn = (ImageButton) findViewById(R.id.add_media_btn);
        addMediaBtn.setOnClickListener(this);

        ImageButton playBtn = (ImageButton) findViewById(R.id.play_btn);
        if (!isHost) playBtn.setVisibility(View.GONE);
        else if (isHost) playBtn.setOnClickListener(this);

        ImageButton skipBtn = (ImageButton) findViewById(R.id.skip_btn);
        if (!isHost) skipBtn.setVisibility(View.GONE);
        else if (isHost) skipBtn.setOnClickListener(this);

        ImageButton viewQueueBtn = (ImageButton) findViewById(R.id.view_queue_btn);
        viewQueueBtn.setOnClickListener(this);

        ImageButton refreshBtn = (ImageButton) findViewById(R.id.refresh_btn);
        if (isHost) refreshBtn.setVisibility(View.GONE);
        else if (!isHost) refreshBtn.setOnClickListener(this);
    }

    private void addYoutubePlayerFragment() {
        // TODO: change media fragment dynamically depending on the type of media
        YouTubePlayerSupportFragment youtubePlayerFragment = new YouTubePlayerSupportFragment();
        //TODO: figure out a better way to store API keys
        String apiKey = ((GlobalApplicationState) this.getApplication()).getYoutubeApiKey();
        youtubePlayerFragment.initialize(apiKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                //TODO: peek first and then cue video
                //      and find some way to peek every xx seconds if
                //      the queue is empty
                mYoutubePlayer = youTubePlayer;
                Log.v(LOG_TAG, "Successfuly initialized Youtube player");

                GetNextMediaTask getNextMediaTask = new GetNextMediaTask();
                getNextMediaTask.execute();
                setPlayerEventListeners();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                //TODO: Figure out what to actually do if it fails...
                String toastMessage = "): Failed to initialize Youtube player.";
                Toast toast = Toast.makeText(getApplicationContext(),
                        toastMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_media, youtubePlayerFragment)
                .commit();
    }

    private void setPlayerEventListeners() {

        mYoutubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {

                @Override
                public void onPlaying() {
                    String toastMessage = "video playing";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onPaused() {
                    String toastMessage = "video paused";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onStopped() {
                    String toastMessage = "video stopped";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onBuffering(boolean b) {

                }

                @Override
                public void onSeekTo(int i) {

                }
            });

        mYoutubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {

            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {
                mYoutubePlayer.play();
                PopMediaTask popMediaTask = new PopMediaTask();
                popMediaTask.execute();
            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {
                //TODO: Maybe I should pop when the video starts!
            }

            @Override
            public void onVideoEnded() {
                Log.v(LOG_TAG, "video ended");

                GetNextMediaTask getNextMediaTask = new GetNextMediaTask();
                getNextMediaTask.execute();
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add_media_btn:
                Intent addMediaIntent = new Intent(this, AddMediaActivity.class);
                startActivity(addMediaIntent);
                break;

            case R.id.play_btn:
                //TODO: make a service that constantly checks if the queue is no longer empty?
                // Need some way so that player can start playing a video when the queue was
                // empty on startup
                /*
                if (!mYoutubePlayer.isPlaying()) {
                    GetNextMediaTask getNextMediaTask = new GetNextMediaTask();
                    getNextMediaTask.execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "The video is already playing!",
                            Toast.LENGTH_SHORT);
                }
                */
                break;

            case R.id.skip_btn:
                //TODO: implement
                break;

            case R.id.view_queue_btn:
                Intent queueIntent = new Intent(this, QueueActivity.class);
                startActivity(queueIntent);
                break;

            case R.id.refresh_btn:
                try {
                    VideoResult video = new GetNextMediaTask()
                            .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR).get();
                    if (video != null) {
                        TextView nowPlayingTxtView = (TextView) findViewById(R.id.now_playing_textview);
                        nowPlayingTxtView.setText(video.title);
                        new DownloadImageTask()
                                .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, video.thumbUrl);
                    } else {
                        String toastMessage = "There's nothing in the queue!";
                        Toast toast = Toast.makeText(getApplicationContext(),
                                toastMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error ", e);
                }
                break;
        }
    }

    //TODO: move all of the async tasks into a separate file
    public class GetNextMediaTask extends AsyncTask<Void, Void, VideoResult> {

        private final String LOG_TAG = GetNextMediaTask.class.getSimpleName();

        private VideoResult getDataFromJson(String resultJsonString)
                throws JSONException {

            //TODO: change all the json param names to variables like below
            // These are the names of the JSON objects that need to be extracted.
            // final String _ITEMS = "items";
            // final String _SNIPPET = "snippet";

            JSONObject videoObject = new JSONObject(resultJsonString);

            // Extract the fields we need from the JSON object and
            // construct a videoResult object
            String videoTitle = videoObject.getString("title");
            String videoId = videoObject.getString("videoId");
            //TODO: This should change depending on the media "type"
            String thumbUrl = videoObject.getString("extra");

            return (new VideoResult(videoId, videoTitle, thumbUrl));
        }

        @Override
        protected VideoResult doInBackground(Void... params) {

            Log.v(LOG_TAG, "GetNextMediaTask started");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videoResultString = null;

            try {
                //TODO: Save this somewhere else.
                final String get_next_req_string = "http://jukebox1234.herokuapp.com/media/next";

                URL url = new URL(get_next_req_string);

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
                videoResultString = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
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
                if (videoResultString == null) return null;
                else return getDataFromJson(videoResultString);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(VideoResult video) {
            Log.v(LOG_TAG, "GetNextMediaTask finished");
            Boolean isHost = ((GlobalApplicationState) getApplication()).isHost();

            if (isHost) {
                if (video != null) {
                    mYoutubePlayer.cueVideo(video.getId());
                    //Log.v(LOG_TAG, "play()");
                    //mYoutubePlayer.play();
                } else {
                    String toastMessage = "There's nothing in the queue!";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    public class PopMediaTask extends AsyncTask<Void, Void, Void> {

         private final String LOG_TAG = PopMediaTask.class.getSimpleName();

         @Override
         protected Void doInBackground(Void... params) {
         Log.v(LOG_TAG, "PopMediaTask started");

             HttpURLConnection urlConnection = null;
             BufferedReader reader = null;

             // Will contain the raw JSON response as a string.
             String resultString = null;

             try {
                 // Construct the URL for the Youtube query
                 final String pop_media_req = "http://jukebox1234.herokuapp.com/media/pop";

                 URL url = new URL(pop_media_req);

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
                 resultString = buffer.toString();
                 Log.v(LOG_TAG, resultString);

             } catch (IOException e) {
                 Log.e(LOG_TAG, "Error ", e);
                 // If the code didn't successfully get the data, there's no point in attempting
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
             return null;
         }

         protected void onPostExecute(Void v) {
             Log.v(LOG_TAG, "PopMediaTask finished");
         }
     }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap img = null;

            try {
                InputStream in = new URL(url).openStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return img;
        }

        protected void onPostExecute(Bitmap img) {
            ImageView albumImgView = (ImageView) findViewById(R.id.album_artwork_img_view);
            albumImgView.setImageBitmap(img);
        }
    }
}
