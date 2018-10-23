package sammynorm.syncify.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import sammynorm.syncify.R;

public class UserNameSelect extends AppCompatActivity implements  View.OnClickListener {
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
        String txtStr =  text.getText().toString();
        if(!txtStr.equals("")) //Says this is always null? It's not m8
        {
            //From here need to see if the username already exists in firebase and then commit
            settings.edit().putString("userName", text.getText().toString()).commit(); //Need this set immediately, stop telling me to do in bg plz
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }
    }

    //Cant skip this step!
 /*   @Override
    public void onBackPressed() { }*/

    @Override
    public void onClick(View v) {
        text.getText().clear(); //or you can use editText.setText("");
    }
}
