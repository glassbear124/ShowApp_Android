package offek.com.showapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;

public class LoginDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "com.intopi.showapp";

    Button btnGreen, btnPink; //btnBlue

    LoginButton loginButton;
    CallbackManager callbackManager;

    View whiteLine;
    TextView txtBottom;
    static EditText editUsername;
    public static ProgressDialog dialog;
    private int userId = -1;
    private String member_id = "";
    private String member_type = "";
    private String full_name = "";
    private String session_id = "";
    private String manage_id = "";
    private String mail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_detail);

        //btnBlue = (Button)findViewById(R.id.btnBlue); btnBlue.setOnClickListener(this);
        editUsername = (EditText)findViewById(R.id.editUsername);
        btnGreen = (Button)findViewById(R.id.btnGreen); btnGreen.setOnClickListener(this);
        btnPink = (Button)findViewById(R.id.btnPink); btnPink.setOnClickListener(this);
        txtBottom = (TextView)findViewById(R.id.txtBottom);

        whiteLine = findViewById(R.id.whiteLine);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        if( G.isUser == false ) {
            whiteLine.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            editUsername.setHint(getResources().getString(R.string.hint_username));
            btnGreen.setText( getResources().getString(R.string.green_btn_artist));
            txtBottom.setText( getResources().getString(R.string.bottom_text_artist));
        }

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (preferences.getString("member_id", "").equals("") && preferences.getString("loginType", "").equals("email")) {
            editUsername.setText(preferences.getString("email", ""));
        }


        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("Facebook success","1");
                getUserData(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    public void getUserData(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        //JSONParser parser=new JSONParser();
                        //  Data data = new Gson().fromJson(json, Data.class);
                        long ID = 0;

                        try {
                            String name = response.getRawResponse();
                            Log.v("AAAAAAAAARRRRRRGGHH", object.toString()+"email = "+object.getString("email"));
                            loginWithFacebook(object.getString("name"), object.getString("id"), object.getString("email"));
                            //new SocialLogin(object.getString("email"), object.getString("name"), object.getString("first_name"), object.getString("last_name"), object.getString("id"), "facebook").execute();
                        }
                        catch (Exception e){
                            //Log.v("BUUUUUUUUUUUUUUUUUU", e.toString());
                            if (dialog != null) { 		dialog.cancel(); 	}
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public  void onClick( View v ) {

        switch( v.getId() ) {

            /*case R.id.btnBlue:

                break;*/

            case R.id.btnGreen:
                if (isNetworkAvailable()) {
                    EditText emailField = (EditText) findViewById(R.id.editUsername);
                    EditText passwordField = (EditText) findViewById(R.id.editPassword);
                    String url = "";
                    //String lang = Locale.getDefault().getLanguage().toString();
                    url = C.base+"MeMBERlOGGinSecuREandroiD.php?EMaIL=" + emailField.getText().toString() + "&PasSwo=" + md5(passwordField.getText().toString());
                    Log.v("ShowApp Login url",url);
                    new LoginPhase1().execute(url);
                    //Intent intent = new Intent( LoginDetailActivity.this, HomeActivity.class);
                    //startActivity(intent);
                }
                else {
                    new AlertDialog.Builder(LoginDetailActivity.this)
                            .setTitle(R.string.InternetError)
                            .setMessage(R.string.InternetRequiredToLogin)
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
                break;

            case R.id.btnPink:
                Intent signup;
                if( G.isUser == true )
                    signup = new Intent (LoginDetailActivity.this, SignupActivity.class);
                else
                    signup = new Intent (LoginDetailActivity.this, SignupArtActivity.class);
                startActivity(signup);
                break;
        }
    }

    void loginWithFacebook(String name, String social_id, final String email) {
        dialog = new ProgressDialog(LoginDetailActivity.this);
        dialog.setMessage(LoginDetailActivity.this.getString(R.string.PleaseWait));
        dialog.setTitle(R.string.LoggingIn);
        if (!dialog.isShowing())
            dialog.show();
        dialog.setCancelable(false);

        String name_string = name.replace(" ", "{^");
        String url = C.base+"socialLoginAndroid.php?email="+email+"&social_id="+social_id+"&name="+name_string;
        Log.d("Facebook URL:",url);
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

                                JSONObject object = arr.getJSONObject(i);
                                if (!object.has("Error")) {

                                    Log.v("Login Facebook", "Success!");
                                    if (dialog != null && dialog.isShowing())
                                        dialog.cancel();
                                    /*new android.app.AlertDialog.Builder(LoginDetailActivity.this)
                                            .setTitle("בוצע בהצלחה")
                                            .setMessage("")
                                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //do nothing
                                                    finish();
                                                }
                                            })
                                            .show();*/
                                    //session = object.getString("session_id");
                                    //is_new_user = object.getBoolean("newPackage");
                                    ////Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session);
                                    //return true;


                                    member_id = object.getString("member_id");
                                    userId = Integer.parseInt(member_id);
                                    member_type = object.getString("member_type");
                                    if (Integer.parseInt(member_type) > 0)
                                        manage_id = object.getString("manage_id");
                                    full_name = object.getString("full_name");
                                    session_id = object.getString("session_id");
                                    mail = object.getString("email");


                                    SharedPreferences preferences = LoginDetailActivity.this.getSharedPreferences(
                                            "com.intopi.showapp", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("session_id",session_id);
                                    editor.putString("member_id", member_id);
                                    editor.putString("member_type", member_type);
                                    editor.putString("email", mail);
                                    editor.putString("loginType", "facebook");
                                    if (Integer.parseInt(member_type) > 0) {
                                        editor.putString("manage_id", manage_id);
                                    }
                                    editor.apply();

                                    Intent intent = new Intent( LoginDetailActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("member_id", member_id);
                                    intent.putExtra("member_type", member_type);
                                    if (Integer.parseInt(member_type) > 0)
                                        intent.putExtra("manage_id", manage_id);
                                    intent.putExtra("full_name", full_name);
                                    intent.putExtra("reason", "login");
                                    startActivity(intent);


                                }
                                else {

                                    //System.out.println("FATAL ERROR!! "+object.getString("Error"));
                                    String error = object.getString("Error");

                                    if (error != null) {
                                        new android.app.AlertDialog.Builder(LoginDetailActivity.this)
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
                if (dialog != null && dialog.isShowing()) { 		dialog.cancel(); 	}
                new android.app.AlertDialog.Builder(LoginDetailActivity.this)
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
    }

    class LoginPhase1 extends AsyncTask<String, Void, Boolean> {


        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginDetailActivity.this);
            dialog.setMessage(LoginDetailActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle(R.string.LoggingIn);
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray channelInfo = new JSONArray(data);

                    for (int i = 0; i < channelInfo.length(); i++) {
                        JSONObject object = channelInfo.getJSONObject(i);
                        if (!object.has("Error")) {
                            member_id = object.getString("member_id");
                            userId = Integer.parseInt(member_id);
                            member_type = object.getString("member_type");
                            if (Integer.parseInt(member_type) > 0)
                                manage_id = object.getString("manage_id");
                            full_name = object.getString("full_name");
                            session_id = object.getString("session_id");
                            mail = object.getString("email");

                            //session = object.getString("session_id");
                            //is_new_user = object.getBoolean("newPackage");
                            Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session_id);
                            return true;
                        }
                        else {
                            if (dialog != null) { 		dialog.cancel(); 	}
                            //System.out.println("FATAL ERROR!! "+object.getString("Error"));
                            error = object.getString("Error");

                            return false;
                        }
                    }


                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            //if (dialog != null) { 		dialog.cancel(); 	}
            //adapter.notifyDataSetChanged();
            if(result == false) {
                if (dialog != null) { 		dialog.cancel(); 	}
                //Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
                //System.out.println("Error Fetching Data");

                if (error != null) {
                    new AlertDialog.Builder(LoginDetailActivity.this)
                            .setTitle("שגיאה")
                            .setMessage(error)
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
                else {
                    new AlertDialog.Builder(LoginDetailActivity.this)
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

            else {
                Log.v("LOGIN", "Success! ID: "+userId);
                dialog.cancel();

                SharedPreferences preferences = LoginDetailActivity.this.getSharedPreferences(
                        "com.intopi.showapp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("session_id",session_id);
                editor.putString("member_id", member_id);
                editor.putString("email", mail);
                editor.putString("loginType", "email");
                editor.putString("member_type", member_type);
                if (Integer.parseInt(member_type) > 0) {
                    editor.putString("manage_id", manage_id);
                }
                editor.apply();

                Intent intent = new Intent( LoginDetailActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("member_id", member_id);
                intent.putExtra("member_type", member_type);
                if (Integer.parseInt(member_type) > 0)
                    intent.putExtra("manage_id", manage_id);
                intent.putExtra("full_name", full_name);
                intent.putExtra("reason", "login");
                startActivity(intent);
                //String url = "http://mitvrd.com/mobile/checkPackageType.php?userId="+userId;
                ////Log.e("Will Fetch", "URLofLogin: "+url);
                //Continue... FINISHED
            }



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String md5(final String s) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(s.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
