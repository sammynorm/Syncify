package sammynorm.syncify.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import sammynorm.syncify.R;
import sammynorm.syncify.SpotifyDataManager.UserUpdates;

public class UserNameSelect extends AppCompatActivity implements View.OnClickListener {
    EditText text;
    TextView errorMessageTxt;
    UserUpdates userUpdates;
    ProgressBar loadingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_select);
        text = findViewById(R.id.editText);
        errorMessageTxt = findViewById(R.id.errorTextView);
        loadingCircle = findViewById(R.id.loadingCircle);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                startUserNameExistanceCheck();
                return false;
            }
        });
        text.setOnClickListener(this);
    }

    public void startUserNameExistanceCheck(){
        userUpdates = new UserUpdates();
        String txtStr = text.getText().toString();
        if (!txtStr.equals("")) {
            loadingCircle.setVisibility(View.VISIBLE);
            userUpdates.checkDBIfUserNameAvailable(this, txtStr);
        }
        else if(txtStr.equals("")){
            errorMessageTxt.setText(R.string.usernameEmpty);
        }
    }

    @SuppressLint("ApplySharedPref")
    public void startHomeActivity(){
        loadingCircle.setVisibility(View.GONE);
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putString("userName", text.getText().toString()).commit(); //Need this set immediately
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    public void notifyUserNameTaken(){
        loadingCircle.setVisibility(View.GONE);
        errorMessageTxt.setText(R.string.usernameTaken);
    }

    public void imageButtonOnClick(View v) {
        startUserNameExistanceCheck();
    }


    @Override
    public void onClick(View v) {
        text.getText().clear();
    }
}
