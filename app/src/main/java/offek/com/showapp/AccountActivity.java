package offek.com.showapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "com.intopi.showapp";
    View btnX, lnCell1, lnCell2, lnCell3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnX = findViewById(R.id.btnX); btnX.setOnClickListener(this);
        lnCell1 = findViewById(R.id.lnCell1); lnCell1.setOnClickListener(this);
        lnCell2 = findViewById(R.id.lnCell2); lnCell2.setOnClickListener(this);
        lnCell3 = findViewById(R.id.lnCell3); lnCell3.setOnClickListener(this);

        if (HomeActivity.loginType <= 0) {
            ((ViewGroup) lnCell2.getParent()).removeView(lnCell2);
        }
    }

    public  void onClick( View v ) {

        if( v == lnCell1 ) {
            Intent intent = new Intent( AccountActivity.this, FevoriteActivity.class);
            startActivity(intent);

        } else if( v == lnCell2 ) {
            Intent intent = new Intent( AccountActivity.this, ArtistManageActivity.class);

            Bundle b = new Bundle();
            if (HomeActivity.loginType == 1) {
                b.putInt("isArtist", 1);
                b.putInt("manage_id", HomeActivity.manage_id);
            }
            if (HomeActivity.loginType == 2) {
                b.putInt("isStage", 1);
                b.putInt("manage_id", HomeActivity.manage_id);
            }
            if (HomeActivity.loginType == 3) {
                b.putInt("isAdmin", 1);
            }
            intent.putExtras(b);

            startActivity(intent);
        } else if( v == lnCell3 ) {

            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("session_id","");
            editor.putString("member_id", "");
            editor.putString("member_type", "");
            editor.apply();

            Intent i = new Intent(AccountActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("reason", "logout");

            LoginManager.getInstance().logOut();

            startActivity(i);
        }
        else if( btnX == v ) {
            onBackPressed();
        }

    }

}