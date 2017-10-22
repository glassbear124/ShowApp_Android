package offek.com.showapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.telephony.CarrierConfigManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArtistManageActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isAdmin, isArtist, isStage;
    int manage_id = -1;
    RelativeLayout rnMain;

    View btnBack, btnX;
    View txtAddShow, txtEditShow, txtEditAbout, txtAddMusic, txtAddArtist, txtStage, txtEditArtist, txtEditStage;
    View txtContactUs;

    LinearLayout lnEdit;
    TextView txtComment, manageTitleText;
    EditText editText;
    LinearLayout lnHeartDetail;

    LinearLayout lnScrl;
    EditText editArtist;
    ArrayList<TextView> arrCells = new ArrayList<TextView>();
    ArrayList<CarouSelInfo> arrCarouSel = new ArrayList<CarouSelInfo>();

    int nLimit = 300;
    View btnSave, btnCancel;

    int target; // 0: addshow, 1:editshow, 2: txtEditAbout, 3: add music video, 4: editArtist, 5: editStage

    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_manage);

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if(b != null) {

            value = b.getInt("isAdmin");
            if (value == 1)
                isAdmin = true;

            value = b.getInt("isArtist");
            if (value == 1)
                isArtist = true;

            value = b.getInt("isStage");
            if (value == 1)
                isStage = true;

            value = b.getInt("manage_id");
            if (value > 0)
                manage_id = value;

            Log.e("stage?","Stage: "+isStage);
        }

//        isAdmin = false;    // test code

        rnMain = (RelativeLayout)findViewById(R.id.rnMain);
        lnHeartDetail = (LinearLayout)findViewById(R.id.lnHeartDetail);


        btnBack = findViewById(R.id.btnBack); btnBack.setOnClickListener(this);
        btnX = findViewById(R.id.btnX); btnX.setOnClickListener(this);

        txtAddArtist = findViewById(R.id.txtAddArtist); txtAddArtist.setOnClickListener(this);
        txtStage = findViewById(R.id.txtStage); txtStage.setOnClickListener(this);

        txtEditArtist = findViewById(R.id.txtEditArtist); txtEditArtist.setOnClickListener(this);
        txtEditStage = findViewById(R.id.txtEditStage); txtEditStage.setOnClickListener(this);

        txtAddShow = findViewById(R.id.txtAddShow); txtAddShow.setOnClickListener(this);
        txtEditShow = findViewById(R.id.txtEditShow); txtEditShow.setOnClickListener(this);
        txtEditAbout = findViewById(R.id.txtEditAbout); txtEditAbout.setOnClickListener(this);
        txtAddMusic = findViewById(R.id.txtAddMusic); txtAddMusic.setOnClickListener(this);
        txtContactUs = findViewById(R.id.txtContactUs); txtContactUs.setOnClickListener(this);
        manageTitleText = (TextView)findViewById(R.id.manageTitleText);
        manageTitleText.setText("ניהול "+HomeActivity.full_name);


        if( isAdmin != true ) {
            txtAddArtist.setVisibility(View.GONE);
            txtStage.setVisibility(View.GONE);
            txtEditArtist.setVisibility(View.GONE);
            txtEditStage.setVisibility(View.GONE);

            if (isArtist) {

            }
            if (isStage) {
                txtAddMusic.setVisibility(View.GONE);
            }

        } else {
            txtContactUs.setVisibility(View.GONE);
            txtEditAbout.setVisibility(View.GONE);
        }

        lnEdit = (LinearLayout)findViewById(R.id.lnEdit);
        btnSave = findViewById(R.id.btnSave); btnSave.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel); btnCancel.setOnClickListener(this);
        txtComment = (TextView)findViewById(R.id.txtComment);
        editText = (EditText)findViewById(R.id.editText);
        int length = editText.getText().length();
        if( length > 0 ) {
            txtComment.setVisibility(View.VISIBLE);
            counterShow(length);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if( length == 0 ) {
                    txtComment.setVisibility(View.GONE);
                } else {
                    counterShow(length);
                }
            }
        });

        lnScrl = (LinearLayout)findViewById(R.id.lnScrl);
        editArtist = (EditText)findViewById(R.id.editArtist);
        editArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if( str.length() > 1 && target == 5)
                    readJsonWithSearchLoc( str );
                else if (str.length() > 1)
                    readJonsSearchArtist( str );
                else {
                    for( TextView cell : arrCells ) {
                        cell.setText("");
                    }
                }
            }
        });

        arrCells.clear();
        for( int i = 0; i < 15; i++ ) {
            View heartCell = getLayoutInflater().inflate(R.layout.cell_heart, null);
            TextView txtCell = (TextView) heartCell.findViewById(R.id.txtCell);
            txtCell.setText("");
            arrCells.add(txtCell);

            heartCell.setTag(i);
            heartCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int i = (int)view.getTag();

                    if( target == 0 ) {
                        Intent intent = new Intent( ArtistManageActivity.this, AddShowActivity.class);
                        CarouSelInfo info = arrCarouSel.get(i);
                        Bundle b = new Bundle();
                        b.putInt("artId", info.id); intent.putExtras(b);
                        startActivity(intent);
                    } else if( target == 1 ) {

                        if( i < arrCarouSel.size() ) {
                            Intent intent = new Intent(ArtistManageActivity.this, ShowListActivity.class);
                            CarouSelInfo info = arrCarouSel.get(i);
                            Bundle b = new Bundle();
                            b.putInt("artId", info.id); intent.putExtras(b);
                            startActivity(intent);
                        }

                    } else if( target == 2 ) {
                        if( i < arrCarouSel.size() ) {
                            lnHeartDetail.setVisibility(View.GONE);
                            CarouSelInfo info = arrCarouSel.get(i);
                            addEditText( info.id );
                        }
                    } else if( target == 3 ) {
                        showAddMusicVideoAlert();
                    }
                    else if ( target == 4 ) {
                        Intent intent = new Intent( ArtistManageActivity.this, AddArtistActivity.class);
                        CarouSelInfo info = arrCarouSel.get(i);
                        Bundle b = new Bundle();
                        b.putInt("artId", info.id); intent.putExtras(b);
                        Log.v("editArtist ID", ""+b.getInt("artId"));
                        startActivity(intent);
                    }
                    else if ( target == 5 ) {
                        Intent intent = new Intent( ArtistManageActivity.this, AddShowActivity.class);
                        CarouSelInfo info = arrCarouSel.get(i);
                        Bundle b = new Bundle();
                        b.putInt("isStage", 1);
                        b.putInt("stageId", info.id); intent.putExtras(b);
                        Log.v("editStage ID", ""+b.getInt("stageId"));
                        startActivity(intent);
                    }
                }
            });
            lnScrl.addView(heartCell);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lnHeartDetail.getVisibility() == View.VISIBLE) {
            if (target == 5) {
                readJsonWithSearchLoc( editText.getText().toString() );
            }
            else {
                readJonsSearchArtist( editText.getText().toString() );
            }
        }
    }

    void readJsonWithSearchLoc( String str ) {

        for( TextView cell : arrCells ) {
            cell.setText("");
        }

        str = str.replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");

        // http://216.172.178.47/~intopi/ShowApp/find_location.php?search=Beijing
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "find_location.php?search=" + str;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            if( size > 15 )
                                size = 15;

                            arrCarouSel.clear();
                            for( int i = 0; i < size; i++ ) {
                                JSONObject obj = arr.getJSONObject(i);
                                TextView txtCell = arrCells.get(i);
                                txtCell.setText( obj.getString("name"));
                                txtCell.setTag(Integer.parseInt(obj.getString("id")));
                                CarouSelInfo info = new CarouSelInfo();
                                info.name = obj.getString("name");
                                info.id = obj.getInt("id");
                                arrCarouSel.add(info);

                            }

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

    void readJonsSearchArtist( String str ) {

        for( TextView cell : arrCells ) {
            cell.setText("");
        }

        str = str.replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");

        // http://216.172.178.47/~intopi/ShowApp/find_artist.php?search=Reef
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "find_artist.php?search=" + str;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            if( size > 15 )
                                size = 15;

                            arrCarouSel.clear();
                            for( int i = 0; i < size; i++ ) {
                                JSONObject obj = arr.getJSONObject(i);
                                TextView txtCell = arrCells.get(i);

                                CarouSelInfo info = new CarouSelInfo();
                                info.name = obj.getString("name");
                                info.id = obj.getInt("id");
                                arrCarouSel.add(info);
                                txtCell.setText( info.name );
                            }

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

    void readJsonWithEditText( int artId ) {
        // http://216.172.178.47/~intopi/ShowApp/fetch_about.php?artist_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String editingStageBio = "0";
        if (isStage)
            editingStageBio = "1";
        String url = C.base + "fetch_about.php?artist_id=" + String.format("%d", artId)+"&isStage="+editingStageBio;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            String bio = obj.getString("bio");
                            if (bio != "null")
                                editText.setText(bio);

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

    void addEditText( int ix ) {
        lnEdit.setVisibility(View.VISIBLE);
        G.showKeyboard( getBaseContext(), editText );
        readJsonWithEditText(ix);
    }

    void removeEditText() {
        editText.setText("");
        lnEdit.setVisibility(View.GONE);
    }

    void counterShow( int length ) {
        txtComment.setVisibility(View.VISIBLE);
        if( length == nLimit ) {
            txtComment.setTextColor(Color.RED);
        } else {
            txtComment.setTextColor(Color.BLUE);
            txtComment.setText( String.format("Type Your Question Here... %d Characters Remining", nLimit-length));
        }
    }

    void showSearchLoc() {
        rnMain.setVisibility(View.GONE);
        lnHeartDetail.setVisibility(View.VISIBLE);
        G.showKeyboard( getBaseContext(), editArtist );
    }

    public  void onClick( View v ) {
        if( v == txtAddShow ) {
            if( isAdmin == true ) {
                target = 0; showSearchLoc();
            } else {
                Intent i = new Intent( ArtistManageActivity.this, AddShowActivity.class);
                if (isStage) {
                    Bundle b = new Bundle();
                    b.putInt("stageShow", 1);
                    b.putInt("notAdmin", 1); i.putExtras(b);
                }
                else if (isArtist) {
                    Bundle b = new Bundle();
                    b.putInt("artId", manage_id); i.putExtras(b);
                }
                startActivity(i);
            }
        }
        else if( v == txtEditShow ) {
            if( isAdmin == true ) {
                target = 1; showSearchLoc();
            } else {
                Intent i = new Intent(ArtistManageActivity.this, ShowListActivity.class);
                if (isStage) {
                    Bundle b = new Bundle();
                    b.putInt("stageShow", 1); i.putExtras(b);
                    b.putInt("artId", manage_id); i.putExtras(b);
                }
                else {
                    Bundle b = new Bundle();
                    b.putInt("artId", manage_id); i.putExtras(b);
                }
                startActivity(i);
            }
        }
        else if( v == txtEditAbout ) {
            if( isAdmin == true ) {
                //target = 2; showSearchLoc();
            } else {
                /*rnMain.setVisibility(View.GONE);
                addEditText(manage_id);*/
                if (isArtist) {
                    Intent intent = new Intent( ArtistManageActivity.this, AddArtistActivity.class);
                    //CarouSelInfo info = arrCarouSel.get(0);
                    Bundle b = new Bundle();
                    b.putInt("artId", HomeActivity.manage_id);
                    b.putInt("notAdmin", 1); intent.putExtras(b);
                    Log.v("editArtist ID", ""+b.getInt("artId"));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent( ArtistManageActivity.this, AddShowActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("isStage", 1);
                    b.putInt("stageId", HomeActivity.manage_id);
                    b.putInt("notAdmin", 1); intent.putExtras(b);
                    Log.v("editStage ID", ""+b.getInt("stageId"));
                    startActivity(intent);
                }

            }
        } else if( v == txtAddMusic ) {
            if( isAdmin == true ) {
                target = 3; showSearchLoc();
            } else {
                showAddMusicVideoAlert();
            }
        } else if( btnBack == v ) {
            onBackPressed();
        } else if( v == btnCancel || btnSave == v ) {
            if (v == btnSave) {
                uploadBio();
            }
            else {
                rnMain.setVisibility(View.VISIBLE);
                removeEditText();
                G.hideKeyboard( getBaseContext(), v );
            }
        } else if( v == txtAddArtist ) {
            Intent i = new Intent( ArtistManageActivity.this, AddArtistActivity.class);
            startActivity(i);
        }
        else if( v == txtStage ) {
            Intent i = new Intent( ArtistManageActivity.this, AddShowActivity.class);
            Bundle b = new Bundle();
            b.putInt("isStage", 1); i.putExtras(b);
            startActivity(i);
        }
        else if( btnX == v ) {
            editArtist.setText("");
            G.hideKeyboard( getBaseContext(), v );

            lnHeartDetail.setVisibility(View.GONE);
            rnMain.setVisibility(View.VISIBLE);
        }
        else if( txtContactUs == v ) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plan");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support.showapp@gmail.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "תמיכה ShowApp");
//            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        else if( txtEditArtist == v ) {
            target = 4; showSearchLoc();
        }
        else if( txtEditStage == v ) {
            target = 5; showSearchLoc();
        }
    }

    @Override
    public void onBackPressed() {
        if (btnX.getVisibility() == View.VISIBLE) {
            if (lnHeartDetail.getVisibility() == View.VISIBLE) {
                editArtist.setText("");
                G.hideKeyboard(getBaseContext(), btnX);

                lnHeartDetail.setVisibility(View.GONE);
                rnMain.setVisibility(View.VISIBLE);
            }
            else {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }

    }

    void showAddMusicVideoAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ArtistManageActivity.this);
        alertDialog.setTitle("הוסף קליפ מוסיקה");
        LinearLayout ln = (LinearLayout) getLayoutInflater().inflate(R.layout.addmusic, null);
        final EditText editUrl = (EditText) ln.findViewById(R.id.editUrl);
        final EditText editSongName = (EditText) ln.findViewById(R.id.editSongName);
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
        editUrl.setFilters(new InputFilter[] { filter });

        alertDialog.setView(ln);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = editUrl.getText().toString();
                        String songName = editSongName.getText().toString();
                        Log.e( url, songName);
                        uploadSong(songName, url);
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    void uploadBio() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(ArtistManageActivity.this);
            dialog.setMessage(ArtistManageActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";
            String bio = editText.getText().toString().replace(" ", "{^");
            String artId = "";

            if (isArtist) {
                CarouSelInfo info = arrCarouSel.get(0);
                Bundle b = new Bundle();
                artId = String.valueOf(info.id);
            }

            if (isStage)
                url = C.base+"update_bio.php?stage_id="+HomeActivity.manage_id+"&bio="+bio;
            else
                url = C.base+"update_bio.php?artist_id="+artId+"&bio="+bio;
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
                                        rnMain.setVisibility(View.VISIBLE);
                                        removeEditText();
                                        G.hideKeyboard( getBaseContext(), btnSave );

                                        Log.v("ADD STAGE", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(ArtistManageActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
                                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //do nothing

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
                                        rnMain.setVisibility(View.VISIBLE);
                                        removeEditText();
                                        G.hideKeyboard( getBaseContext(), btnSave );

                                        String error = obj.getString("Error");

                                        if (error != null) {
                                            new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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
                    rnMain.setVisibility(View.VISIBLE);
                    removeEditText();
                    G.hideKeyboard( getBaseContext(), btnSave );
                    new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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
            new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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

    void uploadSong(String name, String url) {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(ArtistManageActivity.this);
            dialog.setMessage(ArtistManageActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);


            String songName = name.toString().replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");
            CarouSelInfo info = arrCarouSel.get(0);
            Bundle b = new Bundle();
            String artId = String.valueOf(info.id);


            url = C.base+"add_music_video.php?artist_id="+artId+"&song_name="+songName+"&song_url="+url;
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
                                        rnMain.setVisibility(View.VISIBLE);
                                        removeEditText();
                                        G.hideKeyboard( getBaseContext(), btnSave );

                                        Log.v("ADD STAGE", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(ArtistManageActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
                                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //do nothing

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
                                        rnMain.setVisibility(View.VISIBLE);
                                        removeEditText();
                                        G.hideKeyboard( getBaseContext(), btnSave );

                                        String error = obj.getString("Error");

                                        if (error != null) {
                                            new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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
                    rnMain.setVisibility(View.VISIBLE);
                    removeEditText();
                    G.hideKeyboard( getBaseContext(), btnSave );
                    new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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
            new android.app.AlertDialog.Builder(ArtistManageActivity.this)
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