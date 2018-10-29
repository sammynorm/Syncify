package sammynorm.syncify.SpotifyDataManager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sammynorm.syncify.Activity.HomeActivity;
import sammynorm.syncify.Model.FireBaseUtil;
import sammynorm.syncify.Model.User;

import static android.content.ContentValues.TAG;

public class UserUpdates {
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    public String id;
    private Call mCall;
    JSONObject jsonObject;
    JSONObject jsonImageURL;


    public void checkUserExists(String mAccessToken, final String userName, Context context) {
        if (mAccessToken == null) {
            Log.d(TAG, "Empty token");
        }

        //Make API call to spotify for all the user Deets
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    //2 Json Objects because there are nested json items in here :/

                    jsonObject = new JSONObject(response.body().string());
                    String str;

                    //cant be converted to json without removing these brackets
                        str = jsonObject.getString("images");
                        str = str.replace("[", "");
                        str = str.replace("]", "");
                    if(str.equals("")){
                        str = "null!";
                    } else {
                        jsonImageURL = new JSONObject(str);
                        str = jsonImageURL.getString("url");
                    }

                    //Set ID for Subscription
                    setId(jsonObject.getString("id"));
                    System.out.println("DoesUserExistbyID");
                    FireBaseUtil.doesUserExistByID(jsonObject.getString("id"), userName, jsonObject.getString("display_name"), str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //Maybe change this later -- It only is here because it's functions run faster and ends up running with no ID
        System.out.println("setScriberOn");
        setSubscriberOn(context);
    }


    //This is set for the SetSubscriberMethod Below
    public void setId(String id) {
        this.id = id;
    }

    //This Feeds Users data to Firebase
    private void setSubscriberOn(final Context context) {
        final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        //Delay So that DB isn't locked from other method
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                playerUpdates.mySpotifyPlayerSubscription(id, context);
            }
        }, 1000);
    }


    //This will be for Suggestions
    public List<User> getUserList(String query) {
        return FireBaseUtil.getUserListFromQuery(query);
    }

    public void subscribeToSearchedUser(Context context, String query, String username) {
        if (!query.toLowerCase().equals(username.toLowerCase())) {
            FireBaseUtil.subscribeToRemoteUserIfExists(context, query);
        }
    }

    public void fireBaseUserNameCheckCallback(Context context) {
        HomeActivity hm = new HomeActivity();
        hm.onUserExistsReceiver(context);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
