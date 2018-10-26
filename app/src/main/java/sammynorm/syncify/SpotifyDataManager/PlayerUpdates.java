package sammynorm.syncify.SpotifyDataManager;

import android.content.Context;
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

    private static final PlayerUpdates instance = new PlayerUpdates();
    public boolean firstCall = true;
    public User connectedTo;
    String id;
    String songName;
    private PlayerApi playerApi;
    public PlayerState playerStateCall;
    public SpotifyAppRemote spotifyAppRemoteCall;
    private String duplicateChecker;
    private ConnectionParams connectionParams =
            new ConnectionParams.Builder(HomeActivity.CLIENT_ID)
                    .setRedirectUri(HomeActivity.REDIRECT_URI)
                    .showAuthView(true)
                    .build();

    private PlayerUpdates() {
    }

    public static PlayerUpdates getInstance() {
        return instance;
    }

    //Feeds data to Firebase
    public void mySpotifyPlayerSubscription(final String id, Context context) {
        this.id = id;
        FireBaseUtil.addRequestObserver(id);;
                        //Instantiate PlayerState since player may not set in time.
                        playerApi.subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
                                    @Override
                                    public void onEvent(PlayerState playerState) {
                                        playerStateCall = playerState;
                                        System.out.println(playerState.track.name);
                                        //Create method for Posting Song details to Firebase
                                        //Also prevent duplicate entries for firebase
                                        if (!playerState.toString().equals(duplicateChecker)) {
                                            duplicateChecker = playerState.toString();
                                            //Remove extra character
                                            FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused, false);
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

    //Receives data from firebase
    public void setPlayBack(User user) {
        boolean isSongAlreadyLoaded = false;
        if (songName != null && songName.equals(user.getSongPlayingStr())) { // <-- logic for making sure song doesn't play a little bit of the start
            isSongAlreadyLoaded = true;
        } else {
            songName = user.getSongPlayingStr();
        }
        final long songTime = user.getSongTime();
        final Boolean isPaused = user.getSongState();

        if (isSongAlreadyLoaded) {
            if (isPaused) {
                playerApi.pause();
                playerApi.seekTo(songTime);

            } else {
                playerApi.seekTo(songTime);
                playerApi.resume();
            }
        } else {
            playerApi.play(songName).setResultCallback(new CallResult.ResultCallback() {
                @Override
                public void onResult(Object o) {
                    if (isPaused) {
                        playerApi.pause();
                        playerApi.seekTo(songTime);

                    } else {
                        playerApi.resume();
                        playerApi.seekTo(songTime);
                    }
                }
            }).setErrorCallback(new ErrorCallback() {
                        @Override
                        public void onError(Throwable throwable) {
                            // =(
                        }
                    });
        }
    }

    public void initialisePlayerAPI(Context context){
        System.out.println("TEST1");
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(final SpotifyAppRemote spotifyAppRemote) {
                        spotifyAppRemoteCall = spotifyAppRemote;
                        System.out.println("TEST2");

                        playerApi = spotifyAppRemote.getPlayerApi();
                        playerApi.getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                            @Override
                            public void onResult(PlayerState playerState) {
                                playerStateCall = playerState;
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

                    public void forceUpdateSongDetails(final boolean wasRemoteUserRequest) {
        playerApi.getPlayerState()
                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused, wasRemoteUserRequest);
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
