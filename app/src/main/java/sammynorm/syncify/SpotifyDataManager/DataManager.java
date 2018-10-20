package sammynorm.syncify.SpotifyDataManager;

import android.support.annotation.NonNull;
import android.util.Log;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sammynorm.syncify.Model.FireBaseUtil;

import static android.content.ContentValues.TAG;

public class DataManager {
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;


    public void checkUserExists(String mAccessToken)
    {
        if(mAccessToken == null) {
            Log.d(TAG,"Empty token");
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        final FireBaseUtil fbUtil = new FireBaseUtil();

        mCall.enqueue(new Callback(){
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {

                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    FireBaseUtil.doesUserExist(jsonObject.getString("id"), jsonObject.getString("display_name" ));
                } catch (JSONException e) {
                    Log.d(TAG, "Failed to parse data: " + e);
                }
            }
        });
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
