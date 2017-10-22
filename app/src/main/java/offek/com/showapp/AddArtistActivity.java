package offek.com.showapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AddArtistActivity extends AppCompatActivity implements View.OnClickListener {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    View btnCancel;
    Button btnRandom;
    ToggleButton popularButton;
    TextView btnArtist, btnRemove, txtPopularLabel;
    EditText editName, editEmail, editPassword, editUrl, editDesc;

    View lnTags;
    TextView txtMainTag1, txtMainTag2, txtMinorTag1, txtMinorTag2, txtMinorTag3;

    boolean isAdmin = true;

    int artId = -1;
    String artistManageId = "";

    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        lnTags = findViewById(R.id.lnTags);

        btnArtist = (TextView)findViewById(R.id.btnArtist); btnArtist.setOnClickListener(this);
        btnRemove = (TextView)findViewById(R.id.btnRemove); btnRemove.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel); btnCancel.setOnClickListener(this);

        editName = (EditText)findViewById(R.id.editName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editUrl = (EditText)findViewById(R.id.editUrl);
        editDesc = (EditText)findViewById(R.id.editDesc);

        txtMainTag1 = (TextView)findViewById(R.id.txtMainTag1); txtMainTag1.setOnClickListener(this);
        txtMainTag2 = (TextView)findViewById(R.id.txtMainTag2); txtMainTag2.setOnClickListener(this);

        txtMinorTag1 = (TextView)findViewById(R.id.txtMinorTag1); txtMinorTag1.setOnClickListener(this);
        txtMinorTag2 = (TextView)findViewById(R.id.txtMinorTag2); txtMinorTag2.setOnClickListener(this);
        txtMinorTag3 = (TextView)findViewById(R.id.txtMinorTag3); txtMinorTag3.setOnClickListener(this);

        btnRandom = (Button)findViewById(R.id.randomizePassword); btnRandom.setOnClickListener(this);
        txtPopularLabel = (TextView)findViewById(R.id.txtPopularLabel);
        popularButton = (ToggleButton)findViewById(R.id.PopularButton);

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
        editPassword.setFilters(new InputFilter[] { filter });
        editUrl.setFilters(new InputFilter[] { filter });
        editUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    if (editUrl.getText().toString().length() > 0) {
                        if (!editUrl.getText().toString().startsWith("http")) {
                            editUrl.setText("http://"+editUrl.getText().toString());
                        }
                    }
            }
        });

        Bundle b = getIntent().getExtras();
        if(b != null) {
            if (b.getInt("notAdmin") > 0)
                isAdmin = false;
            artId = b.getInt("artId");
            readJsonDateWithStageInfo();
            btnArtist.setText("ערוך אמן");
            btnRemove.setVisibility(View.VISIBLE);
        }

        if (!isAdmin) {
            editName.setVisibility(View.GONE);
            editEmail.setVisibility(View.GONE);
            editPassword.setVisibility(View.GONE);
            btnRandom.setVisibility(View.GONE);
            txtPopularLabel.setVisibility(View.GONE);
            popularButton.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        }
    }

    public  void onClick( View v ) {
        if( v == btnCancel ) {
            onBackPressed();
        } else if( v == btnArtist ) {
            //onBackPressed();
            if (artId > 0)
                editArtist();
            else
                addArtist();
        }
        else if ( v == btnRemove) {
            removeArtist();
        }
        else if( v == txtMainTag1 || v == txtMainTag2 ||
                 v == txtMinorTag1 || v == txtMinorTag2 ||txtMinorTag3 == v ) {

            /*final TextView txtView = (TextView)v;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Title");

            final EditText input = new EditText(this);
            input.setHint("Please input Tag.");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            txtView.setText(input.getText().toString());
                        }
                    });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();*/

            final TextView txtView = (TextView)v;
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            final String[] array = {"Eclectic Rock","Post Punk","Punk","RnB","Soul","Surf Rock","אינדי","אינדי אלקטרוני","אינדי פופ","אינדי רוק","אינדסטריאל","אינסטרומנטלי","אלטרנטיבי","אלקטרוני","אמביינט","אסיד ג'אז","אסיד האוס","אקוסטי","בלוז","ג'אז","דאב","היפהופ","טראנס","טראפ","ים תיכוני","מוזיקה אתיופית","מטאל","נויז רוק","סטונר רוק","ספוקן וורד","ספרדית","פאנק Funk","פולק","פוסט רוק","פופ","פופ ישראלי","פסיכאדלי","צרפתית","קאנטרי","קלאסי","ראפ","רגאיי","רוק","רוק אלטרנטיבי","רוק ישראלי"};
            builder.setTitle("בחר תגית").setItems(array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    /*switch (which) {
                        case 0:
                            // case item 1 do...
                            break;
                        case 1:
                            //  case item 2 do...
                            break;
                        case 2:
                            //  case item 3 do...
                            break;
                        default:
                            break;
                    }*/
                    txtView.setText(array[which].toString());
                    if (!txtMinorTag1.getText().toString().equalsIgnoreCase("בחר"))
                        txtMinorTag2.setVisibility(View.VISIBLE);
                    if (!txtMinorTag2.getText().toString().equalsIgnoreCase("בחר"))
                        txtMinorTag3.setVisibility(View.VISIBLE);

                }
            }).show();

        }
        else if ( v == btnRandom) {
            int random = (int )(Math.random() * 9 + 6);
            String pass = randomString(random);
            editPassword.setText(pass);

        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    void readJsonDateWithStageInfo() {
        Log.v("Performing", "readJsonDateWithStageInfo");
        // http://216.172.178.47/~intopi/ShowApp/fetch_show_info.php?artist_id=1&show_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_artist_info.php?artist_id=" + String.format("%d", artId);
        Log.d("Fetching Artist Info", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            ShowInfo show = new ShowInfo(obj);

                            if (obj.getString("member_id") != null && obj.getString("member_id") != "null")
                                artistManageId = obj.getString("member_id");
                            if (obj.getString("name") != null && obj.getString("name") != "null")
                                editName.setText(obj.getString("name"));
                            if (obj.getString("email") != null && obj.getString("email") != "null")
                                editEmail.setText(obj.getString("email"));
                            if (obj.getString("password") != null && obj.getString("password") != "null")
                                editPassword.setText(obj.getString("password"));
                            if (obj.getString("picture_url") != null && obj.getString("picture_url") != "null")
                                editUrl.setText(obj.getString("picture_url"));
                            if (obj.getString("bio") != null && obj.getString("bio") != "null")
                                editDesc.setText(obj.getString("bio"));
                            if (obj.getString("major_tag1") != null && obj.getString("major_tag1") != "null")
                                txtMainTag1.setText(obj.getString("major_tag1"));
                            if (obj.getString("major_tag2") != null && obj.getString("major_tag2") != "null")
                                txtMainTag2.setText(obj.getString("major_tag2"));
                            if (obj.getString("minor_tag1") != null && obj.getString("minor_tag1") != "null") {
                                txtMinorTag1.setText(obj.getString("minor_tag1"));
                                txtMinorTag1.setVisibility(View.VISIBLE);
                                if (obj.getString("minor_tag1").length() == 0)
                                    txtMinorTag1.setVisibility(View.VISIBLE);
                            }
                            if (obj.getString("minor_tag2") != null && obj.getString("minor_tag2") != "null") {
                                txtMinorTag2.setText(obj.getString("minor_tag2"));
                                txtMinorTag2.setVisibility(View.VISIBLE);
                                if (obj.getString("minor_tag2").length() == 0)
                                    txtMinorTag2.setText("בחר");
                            }
                            if (obj.getString("minor_tag3") != null && obj.getString("minor_tag3") != "null") {
                                txtMinorTag3.setText(obj.getString("minor_tag3"));
                                txtMinorTag3.setVisibility(View.VISIBLE);
                                if (obj.getString("minor_tag3").length() == 0)
                                    txtMinorTag3.setText("בחר");
                            }
                            if (obj.getString("is_popular") != null && obj.getString("is_popular") != "null") {
                                int value = Integer.parseInt(obj.getString("is_popular"));
                                if (value > 0)
                                    popularButton.setChecked(true);
                                else
                                    popularButton.setChecked(false);
                            }

                            /*txtLoc.setText( show.place_string );
                            location_id = show.place_id;
                            editFirst.setText( String.valueOf(show.price) );
                            editEmail.setText(show.order_phone);
                            editUrl.setText(show.order_url);
                            editDesc.setText( show.description );
                            txtDate.setText( String.format("תאריך נבחר: %s %s", show.date_format, show.hour ));
                            unixtime = Long.parseLong(show.date_unix);*/


                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    class AddArtistPhase extends AsyncTask<String, Void, Boolean> {


        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddArtistActivity.this);
            dialog.setMessage(AddArtistActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
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

                            //session = object.getString("session_id");
                            //is_new_user = object.getBoolean("newPackage");
                            ////Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session);
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
                    new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
                    new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
                Log.v("ADD ARTIST", "Success!");
                dialog.cancel();
                new android.app.AlertDialog.Builder(AddArtistActivity.this)
                        .setTitle("בוצע בהצלחה")
                        .setMessage("")
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                                finish();
                            }
                        })
                        .show();

                //String url = "http://mitvrd.com/mobile/checkPackageType.php?userId="+userId;
                ////Log.e("Will Fetch", "URLofLogin: "+url);
                //Continue... FINISHED
            }



        }
    }

    void addArtist() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddArtistActivity.this);
            dialog.setMessage(AddArtistActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";
            String artistName = editName.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            String aboutText =  editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            try {
                aboutText = URLEncoder.encode(aboutText.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String mainTag1 = txtMainTag1.getText().toString().replace(" ", "{^");
            String mainTag2 = txtMainTag2.getText().toString().replace(" ", "{^");
            String minorTag1 = txtMinorTag1.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String minorTag2 = txtMinorTag2.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String minorTag3 = txtMinorTag3.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String image_url = editUrl.getText().toString();
            try {
                image_url = URLEncoder.encode(editUrl.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String isPopular;
            if (popularButton.isChecked())
                isPopular = "1";
            else
                isPopular = "0";

            url = C.base+"add_artist.php?artist_name=" + artistName + "&manage_email=" + editEmail.getText().toString()+"&manage_password="+editPassword.getText().toString()+"&image_url="+image_url+"&about="+aboutText+"&main_tag1="+mainTag1+"&main_tag2="+mainTag2+"&minor_tag1="+minorTag1+"&minor_tag2="+minorTag2+"&minor_tag3="+minorTag3+"&is_popular="+isPopular;
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

                                        Log.v("ADD ARTIST", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(AddArtistActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
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
                                            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
                    new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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

    void editArtist() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddArtistActivity.this);
            dialog.setMessage(AddArtistActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";

            String artistName = editName.getText().toString().replace(" ", "{^").replace(" ", "{^").replace("'","\\'").replace("׳","\\'");
            String aboutText =  editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            try {
                aboutText = URLEncoder.encode(aboutText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String mainTag1 = txtMainTag1.getText().toString().replace(" ", "{^");
            String mainTag2 = txtMainTag2.getText().toString().replace(" ", "{^");
            String minorTag1 = txtMinorTag1.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String minorTag2 = txtMinorTag2.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String minorTag3 = txtMinorTag3.getText().toString().replace(" ", "{^").replace("בחר", "");;
            String isPopular;
            if (popularButton.isChecked())
                isPopular = "1";
            else
                isPopular = "0";

            String image_url = editUrl.getText().toString();
            try {
                image_url = URLEncoder.encode(editUrl.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            url = C.base+"update_artist.php?artist_id="+ artId +"&artist_name=" + artistName +"&manage_id=" + artistManageId + "&manage_email=" + editEmail.getText().toString()+"&manage_password="+editPassword.getText().toString()+"&image_url="+image_url+"&about="+aboutText+"&main_tag1="+mainTag1+"&main_tag2="+mainTag2+"&minor_tag1="+minorTag1+"&minor_tag2="+minorTag2+"&minor_tag3="+minorTag3+"&is_popular="+isPopular;
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

                                        Log.v("EDIT ARTIST", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(AddArtistActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
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
                                            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
                    new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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

    void removeArtist() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddArtistActivity.this);
            dialog.setMessage(AddArtistActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוחק...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";

            url = C.base+"delete_artist.php?artist_id="+ artId;
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

                                        //Log.v("ADD STAGE", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(AddArtistActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
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
                                            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
                    new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
            new android.app.AlertDialog.Builder(AddArtistActivity.this)
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
