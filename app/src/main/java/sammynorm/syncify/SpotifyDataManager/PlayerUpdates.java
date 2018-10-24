package sammynorm.syncify.SpotifyDataManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

import sammynorm.syncify.Activity.HomeActivity;
import sammynorm.syncify.Model.FireBaseUtil;
import sammynorm.syncify.Model.User;

import static android.content.ContentValues.TAG;

public class PlayerUpdates {

    Boolean doesUserExist;
    private static final PlayerUpdates instance = new PlayerUpdates();
    String id;

    private PlayerApi playerApi;
    private String duplicateChecker;
    private ConnectionParams connectionParams =
            new ConnectionParams.Builder(HomeActivity.CLIENT_ID)
                    .setRedirectUri(HomeActivity.REDIRECT_URI)
                    .showAuthView(true)
                    .build();

    private PlayerUpdates() {}

    public static PlayerUpdates getInstance() {
        return instance;
    }

    //Feeds data to Firebase
    public void mySpotifyPlayerSubscription(final String id, Context context) {
        this.id = id;
        FireBaseUtil.addSongDetailsRequestObserver(id);
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(final SpotifyAppRemote spotifyAppRemote) {
                        playerApi = spotifyAppRemote.getPlayerApi();
                        playerApi.subscribeToPlayerState()
                                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                                    @Override
                                    public void onEvent(PlayerState playerState) {
                                        //Create method for Posting Song details to Firebase
                                        //Also prevent duplicate entries for firebase
                                        if (!playerState.toString().equals(duplicateChecker)) {
                                            duplicateChecker = playerState.toString();
                                            //Remove extra character
                                            FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused);
                                        }
                                    }
                                })
                                .setErrorCallback(new ErrorCallback() {
                                    @Override
                                    public void onError(Throwable throwable) {
                                        Log.d(TAG, throwable.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                });
    }


    //Receives data from firebase
    public void setPlayBack(User user) {
        final String songName = user.getSongPlayingStr();
        final long songTime = user.getSongTime();
        final Boolean isPaused = user.getSongState();

        playerApi.pause();

        playerApi.play(songName).setResultCallback(new CallResult.ResultCallback() {
                    @Override
                    public void onResult(Object o) {
                        playerApi.seekTo(songTime);
                        if(isPaused){
                            playerApi.pause();
                        } else {
                            playerApi.resume();
                        }
                    }
                })
                .setErrorCallback(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        // =(
                    }
                });
    }

    public void forceUpdateSongDetails(){
        playerApi.getPlayerState()
                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused);
                    }
                })
                .setErrorCallback(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        // =(
                    }
                });
    }

}
