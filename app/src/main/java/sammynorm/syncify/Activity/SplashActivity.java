package sammynorm.syncify.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ssaurel on 02/12/2016.
 */
public class SplashActivity extends AppCompatActivity {


    //TO add animation when completed
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("userName", "");
        System.out.println(username);
        if (username != "") {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (username == "") {
            Intent i = new Intent(this, UserNameSelect.class);
            startActivity(i);
        }
    }
}
