package sammynorm.syncify.Activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.skyfishjy.library.RippleBackground;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;

public class UserRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_roomv2);
        setupToolbar();
        setupView();
    }

    public void setupToolbar(){
        PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        View v = getLayoutInflater().inflate(R.layout.activity_user_roomv2, null);
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        TextView txtview = (TextView) findViewById(R.id.toolbar_titleUserName);
        txtview.setText(playerUpdates.connectedTo.getUsername());
        setSupportActionBar(myToolbar);
    }

    public void setupView(){
       final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final TextView userName = findViewById(R.id.userName);
        TextView songName = findViewById(R.id.songNametxtView);
        TextView artistName = findViewById(R.id.artistNametxtView);

        songName.setText(playerUpdates.playerStateCall.track.name);
        artistName.setText((playerUpdates.playerStateCall.track.artist.name));
        playerUpdates.spotifyAppRemoteCall.getImagesApi()
                        .getImage(playerUpdates.playerStateCall.track.imageUri)
                        .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
            @Override
            public void onResult(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                userName.setText(playerUpdates.connectedTo.getUsername());

            }});
    }
}
