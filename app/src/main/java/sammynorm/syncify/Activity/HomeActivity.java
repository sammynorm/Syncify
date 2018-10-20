package sammynorm.syncify.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.DataManager;
import sammynorm.syncify.View.HomeView;

public class HomeActivity extends AppCompatActivity implements HomeView {

    public static final String CLIENT_ID = "f71718e83a9e44cbb83869874d5f97c3";
    private static final String REDIRECT_URI = "sync-login://callback";
    private static final int REQUEST_CODE = 1337;
    DataManager dm = new DataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startLogin();
    }

    public void startLogin() {
        //Build Auth Request with permissions and launch login activity
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private","user-library-read", "streaming", "user-read-playback-state"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    //LoginActivity Returns Success/Fail
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    //Check if user exists in DataManager/Firebase and Create if not
                    dm.checkUserExists(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    System.exit(0);
                    // Handle error response
                    break;
                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
