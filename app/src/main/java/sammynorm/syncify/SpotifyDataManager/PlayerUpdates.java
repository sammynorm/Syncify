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
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerState;

import java.util.logging.Handler;

import sammynorm.syncify.Activity.HomeActivity;
import sammynorm.syncify.Activity.UserRoom;
import sammynorm.syncify.Model.FireBaseUtil;
import sammynorm.syncify.Model.User;

import static android.content.ContentValues.TAG;

public class PlayerUpdates {

    private static final PlayerUpdates instance = new PlayerUpdates();
    public boolean firstCall = true;
    public boolean loggedIn = false;
    public PlayerApi playerApi;
    public PlayerState playerStateCall;
    public User connectedTo;
    public SpotifyAppRemote spotifyAppRemoteCall;
    private Context context;
    private String id;
    private String songName;
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
        this.context = context;
        FireBaseUtil.addRequestObserver(id);
        //Instantiate PlayerState since player may not set in time.
        playerApi.subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
            @Override
            public void onEvent(PlayerState playerState) {
                playerStateCall = playerState;
                //Create method for Posting Song details to Firebase
                //Also prevent duplicate entries for firebase
                if (!playerState.toString().equals(duplicateChecker)) {
                    duplicateChecker = playerState.toString();
                    //Remove extra character

                    FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused, playerState.track.imageUri.raw, false);
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
        connectedTo = user;
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
            if(firstCall){
                ((UserRoom) context).firstUpdateDelay();
            }
        } else {
            playerApi.play(songName).setResultCallback(new CallResult.ResultCallback<Empty>() {
                @Override
                public void onResult(Empty empty) {
                    if (isPaused) {
                        playerApi.pause();
                        playerApi.seekTo(songTime);

                    } else {
                        playerApi.resume();
                        playerApi.seekTo(songTime);
                    }
                    if(firstCall){
                        System.out.println("Got to firstUpdateDelay");
                        ((UserRoom) context).firstUpdateDelay();
                    }
                }

            }
            );
        }
        ((UserRoom) context).uiUpdateForce();

    }

    public void initialisePlayerAPI(Context context) {
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(final SpotifyAppRemote spotifyAppRemote) {
                        spotifyAppRemoteCall = spotifyAppRemote;
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
                        FireBaseUtil.updateFireBaseSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused, playerState.track.imageUri.raw, wasRemoteUserRequest);
                    }
                })
                .setErrorCallback(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        // =(
                    }
                });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void resetListeners(String userId) {
        FireBaseUtil.clearRemoteUserListener(userId);
        connectedTo = null;
        firstCall = true;
    }

}
