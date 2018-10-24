package sammynorm.syncify.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import sammynorm.syncify.R;

public class UserNameSelect extends AppCompatActivity implements View.OnClickListener {
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_select);
        text = findViewById(R.id.editText);
        text.setOnClickListener(this);
    }

    public void imageButtonOnClick(View v) {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String txtStr = text.getText().toString();
        if (!txtStr.equals("")) {
            //TODO add firebase condition if U/N exists
            settings.edit().putString("userName", text.getText().toString()).commit(); //Need this set immediately
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }
    }


    @Override
    public void onClick(View v) {
        text.getText().clear();
    }
}
