package sammynorm.syncify.Activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.List;

import sammynorm.syncify.Model.FireBaseUtil;
import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;
import sammynorm.syncify.SpotifyDataManager.UserUpdates;
import sammynorm.syncify.View.HomeView;

public class HomeActivity extends AppCompatActivity implements HomeView, MaterialSearchBar.OnSearchActionListener, TextWatcher {

    public static final String CLIENT_ID = "f71718e83a9e44cbb83869874d5f97c3";
    public static final String REDIRECT_URI = "sync-login://callback";
    private static final int REQUEST_CODE = 1337;
    public String accessToken;
    SharedPreferences settings;
    PlayerUpdates playerUpdates;
    UserUpdates dm = new UserUpdates();
    private List<String> lastSearches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        playerUpdates = PlayerUpdates.getInstance();
        settings = getSharedPreferences("MyPrefsFile", 0);
        setupToolbar();
        if (!playerUpdates.loggedIn) {
            startLogin();
         }

        MaterialSearchBar searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        searchBar.addTextChangeListener(this);
        playerUpdates.initialisePlayerAPI(this);

    }

    public void startLogin() {
        //Build Auth Request with permissions and launch login activity
        if (accessToken == null) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN,
                    REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "user-library-read", "streaming", "user-read-playback-state"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
    }

    //LoginActivity Returns Success/Fail
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    playerUpdates.loggedIn = true;
                    setAccessToken(response.getAccessToken());
                    dm.checkUserExists(accessToken, settings.getString("userName", null), this);
                    settings.edit().putBoolean("isLoggedIn", true).apply();
                    break;
                case ERROR:
                    System.exit(0);
                    break;
                default:
            }
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(final CharSequence text) {
        String newText = text.toString();
        dm.subscribeToSearchedUser(this, newText, settings.getString("userName", null));
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }

    public void setupToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        CharSequence username = settings.getString("userName", null);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        TextView txtview =  findViewById(R.id.toolbar_title);
        setSupportActionBar(myToolbar);
        txtview.setText(username);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //TODO suggestions adapter

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void onUserExistsReceiver(Context context) {
        if (FireBaseUtil.doesUserExist) {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.nothing, R.anim.right_left);
            context.startActivity(new Intent(context, UserRoom.class), options.toBundle());
        } else {
            System.out.println(FireBaseUtil.doesUserExist + "Doesnt exist ");
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}


