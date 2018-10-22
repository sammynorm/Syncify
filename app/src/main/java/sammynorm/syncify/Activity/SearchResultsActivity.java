package sammynorm.syncify.Activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.UserUpdates;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getList();
    }

    public List getList(){
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            System.out.println("User posted no data");
        }
        // get data via the key
        String query = extras.getString("searchString");
        if(query != null) {
            UserUpdates userUpdates = new UserUpdates();
          return userUpdates.getUserList(query);
        }
        else{
            return null;
        }
    }
}
