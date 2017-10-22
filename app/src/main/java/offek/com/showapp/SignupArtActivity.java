package offek.com.showapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignupArtActivity extends AppCompatActivity implements View.OnClickListener {

    public static ProgressDialog dialog;

    Button btnSignup;
    TextView btnStage, btnArtist;

    EditText editUsername, editPhone,editArtName,editEmail,editBio;

    boolean isStage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_art);

        btnSignup = (Button)findViewById(R.id.btnSignup); btnSignup.setOnClickListener(this);
        btnStage = (TextView) findViewById(R.id.btnStage); btnStage.setOnClickListener(this);
        btnArtist = (TextView)findViewById(R.id.btnArtist); btnArtist.setOnClickListener(this);

        editUsername = (EditText)findViewById(R.id.editUsername1);
        editPhone = (EditText)findViewById(R.id.editPhone1);
        editArtName = (EditText)findViewById(R.id.editArtName1);
        editEmail = (EditText)findViewById(R.id.editEmail1);
        editBio = (EditText)findViewById(R.id.editBio1);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        editEmail.setFilters(new InputFilter[] { filter });
        editPhone.setFilters(new InputFilter[] { filter });

        editBio.setMaxEms(300);
    }

    public  void onClick( View v ) {

        switch( v.getId() ) {

            case R.id.btnArtist:
                btnStage.setBackgroundResource(R.drawable.round_left);
                btnStage.setTextColor(Color.GRAY);

                btnArtist.setBackgroundResource(R.drawable.round_fill_right);
                btnArtist.setTextColor( Color.WHITE );
                isStage = false;

                editArtName.setHint("שם האמן/הרכב");
                editBio.setHint("אודות האמן/הרכב");


                break;
            case R.id.btnStage:
                btnArtist.setBackgroundResource(R.drawable.round_right);
                btnArtist.setTextColor(Color.GRAY);

                btnStage.setBackgroundResource(R.drawable.round_fill_left);
                btnStage.setTextColor( Color.WHITE );
                isStage = true;

                editArtName.setHint("שם הבמה");
                editBio.setHint("כתובת ואודות הבמה");


                break;
            case R.id.btnSignup:

                if (editUsername.getText().toString().length() > 0 && editPhone.getText().toString().length() > 0 && editArtName.getText().toString().length() > 0 && editEmail.getText().toString().length() > 0 && editBio.getText().toString().length() > 0) {
                    if (!editEmail.getText().toString().contains("@")) {
                        new AlertDialog.Builder(SignupArtActivity.this)
                                .setTitle("שגיאה")
                                .setMessage("אנא הכנס כתובת מייל מלאה")
                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //do nothing
                                    }
                                })
                                .show();
                    } else {
                        sendRegisterInfo();
                    }
                }
                else {
                    new AlertDialog.Builder(SignupArtActivity.this)
                            .setTitle("שגיאה")
                            .setMessage("אנא מלא את כל התיבות")
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }

                break;

        }

    }

    void sendRegisterInfo() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(SignupArtActivity.this);
            dialog.setMessage(SignupArtActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("שולח...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            //String url = "";
            String name = editUsername.getText().toString().replace(" ", "+");
            String stageOrArtistName = editArtName.getText().toString().replace(" ", "{^");
            String aboutText = editBio.getText().toString().replace(" ", "{^");

            String is_stage;
            if (isStage)
                is_stage = "1";
            else
                is_stage = "0";


            String url = C.base+"sendArtistMail.php?name="+name+"&stageOrArtistName="+stageOrArtistName+"&aboutText="+aboutText+"&is_stage="+is_stage+"&phone="+editPhone.getText().toString()+"&email="+editEmail.getText().toString();
            Log.v("url",url);
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (dialog != null) { 		dialog.cancel(); 	}
                                JSONArray arr = new JSONArray(response);
                                int size = arr.length();
                                for( int i = 0; i < size; i++ ) {

                                    JSONObject obj = arr.getJSONObject(i);
                                    if (!obj.has("Error")) {

                                        Log.v("ADD STAGE", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(SignupArtActivity.this)
                                                .setTitle("נשלח בהצלחה")
                                                .setMessage("תודה על ההרשמה. ניצור איתך קשר בהקדם! למידע נוסף ניתן לפנות ל: showapp.israel@gmail.com")
                                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //do nothing
                                                        finish();
                                                    }
                                                })
                                                .show();
                                        //session = object.getString("session_id");
                                        //is_new_user = object.getBoolean("newPackage");
                                        ////Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session);
                                        //return true;

                                    }
                                    else {

                                        //System.out.println("FATAL ERROR!! "+object.getString("Error"));
                                        String error = obj.getString("Error");

                                        if (error != null) {
                                            new android.app.AlertDialog.Builder(SignupArtActivity.this)
                                                    .setTitle("שגיאה")
                                                    .setMessage(error)
                                                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //do nothing
                                                        }
                                                    })
                                                    .show();
                                        }

                                        //return false;
                                    }
                                }

                            } catch ( JSONException e ) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (dialog != null) { 		dialog.cancel(); 	}
                    new android.app.AlertDialog.Builder(SignupArtActivity.this)
                            .setTitle("שגיאה")
                            .setMessage(R.string.ErrorFetchingData)
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
            });
            queue.add(stringRequest);
            //new AddArtistPhase().execute(url);
        }
        else {
            new android.app.AlertDialog.Builder(SignupArtActivity.this)
                    .setTitle("שגיאה")
                    .setMessage(R.string.ErrorFetchingData)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
