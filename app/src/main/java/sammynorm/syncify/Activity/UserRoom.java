package sammynorm.syncify.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;
import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;

public class UserRoom extends AppCompatActivity {

    TextView songName;
    TextView userName;
    TextView artistName;
    TextView elapsedTimeTxtView;
    TextView totalTimeTxtView;
    SignSeekBar signSeekBar;
    ImageView imageView;
    ImageView exitBtn;
    PlayerState playerState;
    PlayerUpdates playerUpdates;
    ImagesApi imagesApi;
    Boolean activityActive;
    Long elapsedTimeLong;
    Long totalTimeLong;
    Timer timer;
    String connectedToID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_roomv2);
        final Context context = getApplicationContext();
        playerUpdates = PlayerUpdates.getInstance();
        playerState = playerUpdates.playerStateCall;
        playerUpdates.setContext(this);
        playerUpdates.initialisePlayerAPI(this);
        imageView = findViewById(R.id.imageView);
        userName = findViewById(R.id.toolbar_titleUserName);
        songName = findViewById(R.id.songNametxtView);
        artistName = findViewById(R.id.artistNametxtView);
        exitBtn = findViewById(R.id.exitBtn);
        elapsedTimeTxtView = findViewById(R.id.timeElapsed);
        totalTimeTxtView = findViewById(R.id.timeTotal);
        signSeekBar = findViewById(R.id.seek_bar);

        imagesApi = playerUpdates.spotifyAppRemoteCall.getImagesApi();
        setupToolbar();

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerUpdates.resetListeners(connectedToID);
                Intent i = new Intent(context, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.left_right, R.anim.slidein);
                context.startActivity(i, options.toBundle());
            }
        });
    }


    public void setupToolbar() {
        PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        View v = getLayoutInflater().inflate(R.layout.activity_user_roomv2, null);
        Toolbar myToolbar = v.findViewById(R.id.toolbar);
        TextView txtview = findViewById(R.id.toolbar_titleUserName);
        txtview.setText(playerUpdates.connectedTo.getUserName());
        connectedToID = playerUpdates.connectedTo.getUid();
        setSupportActionBar(myToolbar);
    }

    public void setView() {
        elapsedTimeLong = playerState.playbackPosition;
                totalTimeLong = playerState.track.duration;
                float progress = ((float) elapsedTimeLong / totalTimeLong) * 100;

                elapsedTimeTxtView.setText(convertToMinutes(elapsedTimeLong));
                totalTimeTxtView.setText(convertToMinutes(totalTimeLong));
                songName.setText(playerState.track.name);
                artistName.setText((playerState.track.artist.name));
                signSeekBar.setProgress(progress);

                if (!playerState.isPaused) {
                    startSeekBarMovement();
                }
                setImage();
    }

        public void setImage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(playerState.track.imageUri!=null) {
                    imagesApi.getImage(playerState.track.imageUri)
                            .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                @Override
                                public void onResult(Bitmap bitmap) {
                                    imageView.setImageBitmap(bitmap);
                                    userName.setText(playerUpdates.connectedTo.getUserName());
                                }});
                }
                else{
                    setImage();
                } }}, 5);
    }

    public void startSeekBarMovement() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTimeLong += 100;
                setElapsedTimeTxtView(elapsedTimeLong);
                signSeekBar.setProgress(getSeekBarProgress());
            }
        }, 100, 100);
    }

    public float getSeekBarProgress() {
        return ((float) elapsedTimeLong) / totalTimeLong * 100;
    }

    public void setElapsedTimeTxtView(final long elapsedTime) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                elapsedTimeTxtView.setText(convertToMinutes(elapsedTime));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiUpdateForce();
        activityActive = true;
    }

    public void uiUpdateForce() {
        try {
            String str_result = new UIUpdate().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        activityActive = false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        playerUpdates.resetListeners(connectedToID);
    }

    public void updateElapsedTimeUI(long songTime) {
        elapsedTimeLong = songTime;
        elapsedTimeTxtView.setText(convertToMinutes(songTime));
        signSeekBar.setProgress(getSeekBarProgress());
    }

    public void updateSongUI() {
        timer.cancel();
        uiUpdateForce();
    }

    public void updatePauseStateUI(boolean isPaused) {
        if (isPaused&&timer!=null) {
                timer.cancel();
        } else {
            startSeekBarMovement();
        }
    }

    @SuppressLint("DefaultLocale")//Always integers so this is irrelevant
    public String convertToMinutes(long t) {
        long minutes = t / (1000 * 60);
        long seconds = t / 1000 % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public class UIUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            playerUpdates.playerApi.getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                @Override
                public void onResult(PlayerState playerStateNew) {
                    playerState = playerStateNew;
                }
            });

            return "Executed";

        }

        @Override
        protected void onPostExecute(String result) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setView();
                }
            }, 50);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
