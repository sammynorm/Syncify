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

import static android.content.ContentValues.TAG;

public class PlayerUpdates {

    private PlayerApi playerApi;
    private String duplicateChecker;
    private ConnectionParams connectionParams =
            new ConnectionParams.Builder(HomeActivity.CLIENT_ID)
                    .setRedirectUri(HomeActivity.REDIRECT_URI)
                    .showAuthView(true)
                    .build();

    public void subscribeToPlayer(final String id, Context context) {
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

                                            //Remove extra characters
                                            FireBaseUtil.updateSongInfo(id, playerState.track.uri, playerState.playbackPosition, playerState.isPaused);
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

    public void getSongPlaying(Context context) {
        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                //Assign to Global Var
                Log.d("MainActivity", "Connected! Yay!");
                playerApi = spotifyAppRemote.getPlayerApi();
                playerApi.getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        //This works
                        System.out.println(playerState.track);
                    }
                })
                        .setErrorCallback(new ErrorCallback() {
                            @Override
                            public void onError(Throwable throwable) {
                                // =(
                            }
                        });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);

                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }
}
