package offek.com.showapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnUserLogin, btnArtistLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        G.w = metrics.widthPixels;
        G.h = metrics.heightPixels;

        btnUserLogin = (Button)findViewById(R.id.btnUserLogin);
        btnUserLogin.setOnClickListener(this);
        btnArtistLogin = (Button)findViewById(R.id.btnArtistLogin);
        btnArtistLogin.setOnClickListener(this);
    }

    public  void onClick( View v ) {
        Intent userLogin = new Intent( LoginActivity.this, LoginDetailActivity.class);
        switch( v.getId() ) {

            case R.id.btnUserLogin:
                G.isUser = true;
                break;

            case R.id.btnArtistLogin:
                G.isUser = false;
                break;
        }
        startActivity(userLogin);
    }

}