package offek.com.showapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private boolean hasLocation = false;
    public boolean isLoggedIn = false;
    public static String member_id = "";
    public static String full_name = "";
    private String member_type = "";
    private String session_id = "";

    public static final String PREFS_NAME = "com.intopi.showapp";

    private boolean isActivityInFront;
    private boolean doNotifyDataSetChangedOnce = false;

    public static CalendarDay startDate;
    public static CalendarDay endDate;
    double latitude = 0;
    double longitude = 0;
    int radius = 0;

    LinearLayout lnHomeButtonSet, lnSearchButtonSet;
    LinearLayout lnHomeDetail, lnSearchDetail;

    ToggleButton btnHeart;

    LinearLayout lnHello;
    View vLine2;

    EditText editSearch;

    RelativeLayout btnFestival, btnArtist, btnStage, btnLocation;

    RelativeLayout btnFestival1, btnArtist1, btnStage1, btnTag, btnShow;
    ImageView imgFestival, imgArtist, imgStage, imgTag, imgShow;
    TextView txtFestival1, txtArtist1, txtStage1, txtTag, txtShow, locationLabel;
    TextView txtLoc, txtStage, txtArtist, txtFestival, txtHello, txtWhatdoyou, todaysShowsLabel, popularArtistsLabel;
    static TextView datesLabel;


    PagerContainer mContainer;
    ViewPager pager;
    PagerAdapter adapter;
    ArrayList<CarouSelInfo> topCarousel = new ArrayList<CarouSelInfo>();

    PagerContainer mContainer2;
    ViewPager pager2;
    PagerAdapter adapter2;
    ArrayList<CarouSelInfo> bottomCarousel = new ArrayList<CarouSelInfo>();

    SoftKeyboard sf;

    int mCount1, mCount2;
    public Timer T;

    boolean isClickHeart;
    public static int loginType = 0; //0 = not logged in. 1 = user logged in. 2 = artist logged in. 3 = admin logged in
    public static int manage_id = -1;
    View btnX;
    ProgressBar activityIndicator;

    int searchType = C.search_type_artist;

    ArrayList<View> arrSearchCell = new ArrayList<View>();
    ArrayList<CarouSelInfo> arrSearchInfo = new ArrayList<CarouSelInfo>();

    LinearLayout lnLocTime;

    static CalendarDay date1, date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.v("OnCreate", "CREATED NEW");

        //int dateMulti = Integer.parseInt(Date);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(1455598067*1000L);//<<Value from PHP *1000
        Date dateObject = cal.getTime();
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        Log.d("gotDateYou: ",date+" and date millis: "+dateObject);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        if (!checkLocationPermission()) {
            Log.v("WTF IS MY LOCATION", "!!!!");
            readJsonData();
        }
        else
            mGoogleApiClient.connect();

        if (G.w == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            G.w = metrics.widthPixels;
            G.h = metrics.heightPixels;
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btnX = findViewById(R.id.btnX);
        btnX.setOnClickListener(this);

        activityIndicator = (ProgressBar) findViewById(R.id.activityIndicator);
        vLine2 = findViewById(R.id.vLine2);

        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (length > 2) {
                    readJsonDateForSearch(editable.toString(), searchType);
                }
            }
        });

        lnHomeButtonSet = (LinearLayout) findViewById(R.id.lnHomeButtonSet);
        lnSearchButtonSet = (LinearLayout) findViewById(R.id.lnSearchButtonSet);
        lnHomeDetail = (LinearLayout) findViewById(R.id.lnHomeDetail);
        lnSearchDetail = (LinearLayout) findViewById(R.id.lnSearchDetail);

        lnSearchButtonSet.setVisibility(View.GONE);
        lnSearchDetail.setVisibility(View.GONE);

        arrSearchCell.clear();
        btnHeart = (ToggleButton) findViewById(R.id.btnHeart);
        btnHeart.setOnClickListener(this);

        if (!isLoggedIn) {
            btnHeart.setTextOn(getString(R.string.login));
            btnHeart.setTextOff(getString(R.string.login));
            btnHeart.setChecked(btnHeart.isChecked()); // Required to update text

            Drawable top = getResources().getDrawable(R.drawable.profile_32);
            btnHeart.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        }

        lnHello = (LinearLayout) findViewById(R.id.lnHello);
        lnLocTime = (LinearLayout) findViewById(R.id.lnLocTime);

        btnArtist = (RelativeLayout) findViewById(R.id.btnArtist);
        btnArtist.setOnClickListener(this);
        btnFestival = (RelativeLayout) findViewById(R.id.btnFestival);
        btnFestival.setOnClickListener(this);
        btnLocation = (RelativeLayout) findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(this);
        btnStage = (RelativeLayout) findViewById(R.id.btnStage);
        btnStage.setOnClickListener(this);

        btnFestival1 = (RelativeLayout) findViewById(R.id.btnFestival1);
        btnFestival1.setOnClickListener(this);
        btnArtist1 = (RelativeLayout) findViewById(R.id.btnArtist1);
        btnArtist1.setOnClickListener(this);
        btnStage1 = (RelativeLayout) findViewById(R.id.btnStage1);
        btnStage1.setOnClickListener(this);
        btnTag = (RelativeLayout) findViewById(R.id.btnTag);
        btnTag.setOnClickListener(this);
        btnShow = (RelativeLayout) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);

        imgFestival = (ImageView) findViewById(R.id.imgFestival);
        imgArtist = (ImageView) findViewById(R.id.imgArtist);
        imgStage = (ImageView) findViewById(R.id.imgStage);
        imgTag = (ImageView) findViewById(R.id.imgTag);
        imgShow = (ImageView) findViewById(R.id.imgShow);

        txtFestival1 = (TextView) findViewById(R.id.txtFestival1);
        txtArtist1 = (TextView) findViewById(R.id.txtArtist1);
        txtStage1 = (TextView) findViewById(R.id.txtStage1);
        txtTag = (TextView) findViewById(R.id.txtTag);
        txtShow = (TextView) findViewById(R.id.txtShow);
        datesLabel = (TextView) findViewById(R.id.datesLabel);
        datesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar();
            }
        });
        locationLabel = (TextView) findViewById(R.id.locationLabel);
        locationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocations(false);
            }
        });


        Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSansHebrew-Regular.ttf");


        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtStage = (TextView) findViewById(R.id.txtStage);
        txtArtist = (TextView) findViewById(R.id.txtArtist);
        txtFestival = (TextView) findViewById(R.id.txtFestival);
        txtHello = (TextView) findViewById(R.id.txtHello);
        txtWhatdoyou = (TextView) findViewById(R.id.txtWhatdoyou);
        todaysShowsLabel = (TextView) findViewById(R.id.todaysShowsLabel);
        popularArtistsLabel = (TextView) findViewById(R.id.popularArtistsLabel);

        txtLoc.setTypeface(typeface);
        txtStage.setTypeface(typeface);
        txtArtist.setTypeface(typeface);
        txtFestival.setTypeface(typeface);
        txtHello.setTypeface(typeface);
        todaysShowsLabel.setTypeface(typeface);
        popularArtistsLabel.setTypeface(typeface);


        txtFestival1.setTypeface(typeface);
        txtArtist1.setTypeface(typeface);
        txtStage1.setTypeface(typeface);
        txtTag.setTypeface(typeface);
        txtShow.setTypeface(typeface);
        txtWhatdoyou.setTypeface(typeface);

        readJsonDateForSearch(editSearch.getText().toString(), searchType);

        updateMemberUI();
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        sf = new SoftKeyboard(rootLayout, im);
        sf.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isClickHeart == false)
                            showKeyboard(View.VISIBLE);
                    }
                });
            }
        });

        initSearchBtnSet();

        imgArtist.setImageResource(R.drawable.btn_artist);
        txtArtist1.setTextColor(C.tintColor);
        btnArtist1.setEnabled(false);

        mCount1 = 0;
        mCount2 = 0;
        T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mCount1++;
                mCount2++;
                if (mCount1 == 6000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pager != null) {
                                if (adapter != null && adapter2 != null) {
                                    adapter.notifyDataSetChanged();
                                    adapter2.notifyDataSetChanged();
                                }
                                int ix = pager.getCurrentItem();
                                if (ix >= topCarousel.size() - 1)
                                    ix = 0;
                                else
                                    ix++;
                                pager.setCurrentItem(ix, true);
                            }

                        }
                    });
                    mCount1 = 0;
                }

                if (mCount2 == 7000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pager2 != null) {
                                if (adapter != null && adapter2 != null) {
                                    adapter.notifyDataSetChanged();
                                    adapter2.notifyDataSetChanged();
                                }
                                int ix = pager2.getCurrentItem();
                                //Log.e("Bottom carousel will scroll","size is: "+bottomCarousel.size()+" and index (-1) is: "+ix);
                                if (ix >= bottomCarousel.size() - 1)
                                    ix = 0;
                                else
                                    ix++;
                                pager2.setCurrentItem(ix, true);
                            }

                        }
                    });
                    mCount2 = 0;
                }
            }
        }, 1, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            showKeyboard(View.VISIBLE);
            String searchKey = data.getStringExtra("searchKey");
            editSearch.setText(searchKey);
            initSearchBtnSet();
            imgTag.setImageResource(R.drawable.btn_sharp);
            txtTag.setTextColor(C.tintColor);
            btnTag.setEnabled(false);
            searchType = C.search_type_tag;
            readJsonDateForSearch(searchKey, searchType);
        }
        else if (resultCode == Activity.RESULT_FIRST_USER) {
            //Log.v("Got date resulttt","date1: "+date1.getDate().toString());
            updateSearchDates();
        }
    }

    void updateMemberUI() {
        Intent myIntent = getIntent();
        if (myIntent.getExtras() != null) {
            Log.v("Got extras: ",""+myIntent.getExtras());
            String reason = myIntent.getExtras().getString("reason");
            if (reason != null) {
                if (reason.equals("login")) {
                    isLoggedIn = true;

                    full_name = myIntent.getExtras().getString("full_name");
                    txtHello.setText("היי " + full_name + ",");
                    btnHeart.setTextOn(getString(R.string.account));
                    btnHeart.setTextOff(getString(R.string.account));
                    btnHeart.setChecked(btnHeart.isChecked()); // Required to update text

                    loginType = Integer.parseInt(myIntent.getExtras().getString("member_type"));
                    member_id = myIntent.getExtras().getString("member_id");
                    if (myIntent.getExtras().getString("manage_id") != null && Integer.parseInt(myIntent.getExtras().getString("manage_id")) > 0)
                        manage_id = Integer.parseInt(myIntent.getExtras().getString("manage_id"));
                }
                else if (reason.equals("logout")) {
                    isLoggedIn = false;

                    txtHello.setText("היי " + "אורח" + ",");
                    btnHeart.setTextOn(getString(R.string.login));
                    btnHeart.setTextOff(getString(R.string.login));
                    btnHeart.setChecked(btnHeart.isChecked()); // Required to update text

                    loginType = 0;
                    member_id = "";
                }
            }
            else {
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String member_id = preferences.getString("member_id", "");
                String session_id = preferences.getString("session_id", "");
                Log.v("Have data?", "ID: "+member_id+", "+preferences.getString("session_id", ""));
                if(!member_id.equalsIgnoreCase("") && !session_id.equalsIgnoreCase(""))
                {
                    Log.v("Have data?", "ID: "+member_id+", "+preferences.getString("session_id", ""));
                    String url = C.base+"autoLogin.php?member_id="+member_id+"&session_id="+session_id;
                    Log.v("autoLogin url",url);
                    new LoginPhase1().execute(url);
                }
            }

        }
        else {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String member_id = preferences.getString("member_id", "");
            String session_id = preferences.getString("session_id", "");
            Log.v("Have data?", "ID: "+member_id+", "+preferences.getString("session_id", ""));
            if(!member_id.equalsIgnoreCase("") && !session_id.equalsIgnoreCase(""))
            {
                Log.v("Have data?", "ID: "+member_id+", "+preferences.getString("session_id", ""));
                String url = C.base+"autoLogin.php?member_id="+member_id+"&session_id="+session_id;
                Log.v("autoLogin url",url);
                new LoginPhase1().execute(url);
            }
        }
    }

    /*void performBackgroundLogin(String ID, String Session) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";
        if (hasLocation)
            url = C.base + "load_home_carousels.php?lon="+currentLongitude+"&lat="+currentLatitude+"&radius=35";
        else
            url = C.base + "load_home_carousels.php";
        Log.v("Arraysc",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONArray arr1 = arr.getJSONArray(0);
                            JSONArray arr2 = arr.getJSONArray(1);

                            Log.v("Arraysc","arr1="+arr1.length()+" and arr2="+arr2.length());

                            if (arr2 == null || arr2.length() <= 0) {
                                JSONObject emptyObject = new JSONObject();
                                emptyObject.put("name","אין אירועים באיזורך היום");
                                Log.e("topc","making new from empty");
                                CarouSelInfo info = new CarouSelInfo(emptyObject);
                                bottomCarousel.add(info);
                            }

                            int size = arr1.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr1.getJSONObject(i);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                Log.e("arraysc","wtf");
                                bottomCarousel.add(info);
                            }

                            size = arr2.length();
                            for (int j = 0; j < size; j++) {
                                JSONObject obj = arr2.getJSONObject(j);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                Log.e("arraysc","wtf2");
                                topCarousel.add(info);
                            }
                            initializeviews();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("arraysc","WHAAAT");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }*/

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //buildGoogleApiClient();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //mGoogleMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void readJsonData() {
        Log.e("calling readJsonData", "will it crash now?");
        topCarousel.clear();
        bottomCarousel.clear();

        // http://216.172.178.47/~intopi/ShowApp/load_home_carousels.php
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";
        if (hasLocation)
            url = C.base + "load_home_carousels.php?lon="+currentLongitude+"&lat="+currentLatitude+"&radius=22";
        else
            url = C.base + "load_home_carousels.php";
        Log.v("load home carousels url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONArray arr1 = arr.getJSONArray(0);
                            JSONArray arr2 = arr.getJSONArray(1);

                            Log.v("Arraysc","arr1="+arr1.length()+" and arr2="+arr2.length());

                            if (arr2 == null || arr2.length() <= 0) {
                                JSONObject emptyObject = new JSONObject();
                                emptyObject.put("name","אין אירועים באיזורך היום");
                                Log.e("topc","making new from empty");
                                CarouSelInfo info = new CarouSelInfo(emptyObject);
                                info.isEmpty = true;
                                topCarousel.add(info);
                            }

                            int size = arr1.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr1.getJSONObject(i);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                bottomCarousel.add(info);
                            }

                            size = arr2.length();
                            for (int j = 0; j < size; j++) {
                                JSONObject obj = arr2.getJSONObject(j);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                topCarousel.add(info);
                            }
                            initializeviews();

                        } catch (JSONException e) {
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

    public void readJsonDateForSearch(String keyword1, int type) {
        // http://216.172.178.47/~intopi/ShowApp/search.php?search=PSY&type=shows&default=NO
        if (btnX.getVisibility() == View.VISIBLE) {
            activityIndicator.setVisibility(View.VISIBLE);
        }
        String keyword = keyword1.replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");;

        if (keyword.endsWith("{^")) {
            activityIndicator.setVisibility(View.GONE);
            return;
        }

        String strType = G.getSearchType(type);for (View cell : arrSearchCell) {
            lnSearchDetail.removeView(cell);
        }

        arrSearchInfo.clear();
        arrSearchCell.clear();
        lnSearchDetail.invalidate();

        Log.d("Empty?","searchInfo: "+arrSearchInfo.size());
        Log.d("Empty?","searchCell: "+arrSearchCell.size());

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url;
        long firstUnixDate = 0;
        long secondUnixDate = 0;
        if (date1 != null)
            firstUnixDate = date1.getCalendar().getTimeInMillis() / 1000;
        if (date2 != null)
            secondUnixDate = date2.getCalendar().getTimeInMillis() / 1000;

        if (keyword.length() > 2)
            url = C.base + "search.php?search=" + keyword + "&type=" + strType + "&default=NO";
        else {
            if (editSearch.getText().length() > 2) {
                activityIndicator.setVisibility(View.GONE);
                return;
            }
            url = C.base + "search.php?" + "type=" + strType + "&default=YES";
        }

        if (firstUnixDate > 1) {
            url = url + "&firstDate=" + firstUnixDate;
            if (secondUnixDate > 1)
                url = url + "&secondDate=" + (secondUnixDate + 86400);
            else
                url = url + "&secondDate=" + (firstUnixDate + 86400);
        }


        if (Math.abs(latitude) > 0 && Math.abs(longitude) > 0 && radius > 0) {
            url = url + "&lon="+longitude + "&lat="+latitude + "&radius="+radius;
        }

        Log.v("search url = ",url);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            if (size > 0) {
                                for (View cell : arrSearchCell) {
                                    lnSearchDetail.removeView(cell);
                                }

                                arrSearchInfo.clear();
                                arrSearchCell.clear();
                                lnSearchDetail.invalidate();
                            }
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                arrSearchInfo.add(info);

                                View musicCell = getLayoutInflater().inflate(R.layout.cell_search, null);
                                info.fillViewForCellSearch(musicCell, getBaseContext(), searchType);

                                musicCell.setTag(info);
                                musicCell.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CarouSelInfo info = (CarouSelInfo) view.getTag();
                                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                        Bundle b = new Bundle();
                                        int id = -1;
                                        int type = 1;
                                        if (info.artId != 0)
                                            id = info.artId;

                                        if (searchType == C.search_type_show) {
                                            b.putInt("open_shows", 1);
                                            if (info.artId <= 0) {
                                                return;
                                            }
                                        }
                                        if (searchType == C.search_type_stage) {
                                            if (id < 0 && info.id != 0)
                                                id = info.id;
                                            type = 2;
                                            b.putInt("is_stage", 1);
                                        }

                                        if (searchType == C.search_type_festival) {
                                            b.putInt("is_festival", 1);
                                        }

                                        b.putInt("artist_id", id);
                                        b.putInt("artist_type", type);

                                        intent.putExtras(b);
                                        startActivityForResult(intent, 1);
                                    }
                                });

                                lnSearchDetail.addView(musicCell);
                                arrSearchCell.add(musicCell);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (arrSearchCell.size() < 1 && editSearch.getText().length() > 2) {
                            CarouSelInfo info = new CarouSelInfo();
                            info.name = "אין תוצאות";
                            info.url = "h134";
                            info.major_tag1 = "";
                            info.major_tag2 = "";
                            info.date_format = "";
                            info.date = "";
                            info.hour = "";
                            info.bio = "";

                            arrSearchInfo.add(info);

                            View musicCell = getLayoutInflater().inflate(R.layout.cell_search, null);
                            info.fillViewForCellSearch(musicCell, getBaseContext(), C.search_type_stage);

                            lnSearchDetail.addView(musicCell);
                            arrSearchCell.add(musicCell);
                        }
                        else if  (arrSearchCell.size() < 1 && searchType == C.search_type_show) {
                            CarouSelInfo info = new CarouSelInfo();
                            info.name = "אין תוצאות";
                            info.url = "h134";
                            info.major_tag1 = "";
                            info.major_tag2 = "";
                            info.date_format = "";
                            info.date = "";
                            info.hour = "";
                            info.bio = "";

                            arrSearchInfo.add(info);

                            View musicCell = getLayoutInflater().inflate(R.layout.cell_search, null);
                            info.fillViewForCellSearch(musicCell, getBaseContext(), C.search_type_stage);

                            lnSearchDetail.addView(musicCell);
                            arrSearchCell.add(musicCell);
                        }
                        activityIndicator.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activityIndicator.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void initSearchBtnSet() {

        imgFestival.setImageResource(R.drawable.btn_festival_white);
        txtFestival1.setTextColor(Color.WHITE);
        btnFestival1.setEnabled(true);

        imgArtist.setImageResource(R.drawable.btn_artist_white);
        txtArtist1.setTextColor(Color.WHITE);
        btnArtist1.setEnabled(true);

        imgStage.setImageResource(R.drawable.btn_stage_white);
        txtStage1.setTextColor(Color.WHITE);
        btnStage1.setEnabled(true);

        imgTag.setImageResource(R.drawable.btn_sharp_white);
        txtTag.setTextColor(Color.WHITE);
        btnTag.setEnabled(true);

        imgShow.setImageResource(R.drawable.btn_horn_white);
        txtShow.setTextColor(Color.WHITE);
        btnShow.setEnabled(true);

        lnLocTime.setVisibility(View.GONE);
    }

    public void showKeyboard(int visibility) {
        int val;
        if (visibility == View.GONE) {
            val = View.VISIBLE;
            activityIndicator.setVisibility(visibility);
        }
        else
            val = View.GONE;

        lnSearchButtonSet.setVisibility(visibility);
        lnSearchDetail.setVisibility(visibility);
        btnX.setVisibility(visibility);

        btnHeart.setVisibility(val);
        lnHello.setVisibility(val);
        lnHomeButtonSet.setVisibility(val);
        lnHomeDetail.setVisibility(val);
    }


    private void initializeviews() {

        if (adapter != null && adapter2 != null && topCarousel.size() > 0 && bottomCarousel.size() > 0 ) {
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }

        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        pager = mContainer.getViewPager();
        adapter = new MyPagerAdapter(this);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(20);
        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);
        pager.setCurrentItem(0, true);

        mContainer2 = (PagerContainer) findViewById(R.id.pager_container2);
        pager2 = mContainer2.getViewPager();
        adapter2 = new MyPagerAdapter(this);
        pager2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        pager2.setOffscreenPageLimit(adapter2.getCount());
        pager2.setPageMargin(20);
        pager2.setClipChildren(false);
        pager2.setCurrentItem(0, true);

        if (hasLocation) {
            todaysShowsLabel.setText("אירועים באיזור שלך היום");
        }
        else {
            todaysShowsLabel.setText("הפעל מיקום בשביל למצוא הופעות באיזורך");
        }

        if (adapter != null && adapter2 != null  ) {
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
    }

    void showCalendar() {
        Intent calendar = new Intent(HomeActivity.this, CalendarActivity.class);
        startActivityForResult(calendar,0);
        //startActivity(calendar);
    }

    public static void updateSearchWithDates(CalendarDay firstDate, CalendarDay secondDate, boolean today) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

        if (firstDate != null && firstDate.getCalendar().getTimeInMillis() > 0) {
            date1 = firstDate;
            if (today) { // TODAY SELECTED
                datesLabel.setText("תאריכים: היום");
            }
            else {
                if (secondDate != null && secondDate.getCalendar().getTimeInMillis() > 0) { // TWO DATES SELECTED
                    date2 = secondDate;
                    datesLabel.setText("תאריכים: "+dateFormat.format(date1.getDate())+" - "+dateFormat.format(date2.getDate()));
                }
                else { // JUST ONE DATE SELECTED
                    datesLabel.setText("תאריכים: "+dateFormat.format(date1.getDate()));
                }

            }
        }

        //Log.v("received date","date1: "+date1.getCalendar().getTimeInMillis() / 1000);
    }

    void updateSearchDates() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

        if (date1 != null && date1.getCalendar().getTimeInMillis() > 0) {
            if (date1.getDate().equals(CalendarDay.today().getDate()) && date2 == null) {
                datesLabel.setText("תאריכים: היום");
            }
            else {
                if (date2 != null && date2.getCalendar().getTimeInMillis() > 0) { // TWO DATES SELECTED
                    datesLabel.setText("תאריכים: "+dateFormat.format(date1.getDate())+" - "+dateFormat.format(date2.getDate()));
                }
                else { // JUST ONE DATE SELECTED
                    datesLabel.setText("תאריכים: "+dateFormat.format(date1.getDate()));
                    date2 = null;
                }
            }
        }
        else {
            datesLabel.setText("תאריכים: הכל");
        }


        readJsonDateForSearch(editSearch.getText().toString(), searchType);
    }

    void selectLocations(boolean isShortcut) {
        if (isShortcut) {
            latitude = currentLatitude;
            longitude = currentLongitude;
            radius = 35;
            readJsonDateForSearch(editSearch.getText().toString(), searchType);
        }
        else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            final String[] array = {"האזור שלי","כל הארץ","תל אביב והסביבה","אילת וערבה","באר שבע והסביבה","גליל עליון","גליל תחתון","חיפה והסביבה","ירושלים והסביבה","צפון הנגב","רמת הגולן", "השרון והסביבה"};
            builder.setTitle("בחר אזור").setItems(array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch (which) {
                        case 0:
                            // case item 1 do...
                            if (hasLocation) {
                                latitude = currentLatitude;
                                longitude = currentLongitude;
                                radius = 35;
                            }
                            else {
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setTitle("שגיאה")
                                        .setMessage("לא ניתן למצוא את מיקומך")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        })
                                        .show();
                                return;
                            }
                            break;
                        case 1: //All Country
                            latitude = 0;
                            longitude = 0;
                            radius = 0;
                            break;
                        case 2: //Tel Aviv
                            latitude = 32.0852999;
                            longitude = 34.78176759999999;
                            radius = 20;
                            break;
                        case 3: //Eilat
                            latitude = 29.7822863;
                            longitude = 34.9561592;
                            radius = 50;
                            break;
                        case 4: // Be'er Sheva
                            latitude = 31.252973;
                            longitude = 34.791462;
                            radius = 35;
                            break;
                        case 5: // Galil Elyon
                            latitude = 33.12256929999999;
                            longitude = 35.5703211;
                            radius = 35;
                            break;
                        case 6: //Galil Tachton
                            latitude = 32.7461638;
                            longitude = 35.5028379;
                            radius = 35;
                            break;
                        case 7: //Haifa
                            latitude = 32.7940463;
                            longitude = 34.989571;
                            radius = 30;
                            break;
                        case 8: //Jerusalem
                            latitude = 31.768319;
                            longitude = 35.21371;
                            radius = 25;
                            break;
                        case 9: //Negev
                            latitude = 30.7140861;
                            longitude = 34.8757476;
                            radius = 50;
                            break;
                        case 10: //Golan
                            latitude = 33.01558540000001;
                            longitude = 35.784354;
                            radius = 20;
                            break;
                        case 11: //Sharon
                            latitude = 32.305011;
                            longitude = 34.911731;
                            radius = 20;
                            break;

                        default:
                            latitude = 0;
                            longitude = 0;
                            radius = 0;
                            break;
                    }
                    locationLabel.setText("אזור: "+array[which].toString());

                    readJsonDateForSearch(editSearch.getText().toString(), searchType);

                }
            }).show();
        }
    }

    void showHeartButton() {
        int view = View.VISIBLE;
        int view2 = View.GONE;

        if (isClickHeart == true) {
            view = View.GONE;
            view2 = View.VISIBLE;
        }

        btnHeart.setVisibility(view);
        lnHello.setVisibility(view);
        lnHomeButtonSet.setVisibility(view);
        lnHomeDetail.setVisibility(view);
        vLine2.setVisibility(view);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnHeart:
                if (isLoggedIn) {
                    Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btnX:
                if ( !isClickHeart )
                    showKeyboard(View.GONE);
//                isClickHeart = false;
//                showHeartButton();
                G.hideKeyboard(getBaseContext(), v);
                for (View cell : arrSearchCell) {
                    lnSearchDetail.removeView(cell);
                }
                arrSearchInfo.clear();
                arrSearchCell.clear();
                lnSearchDetail.invalidate();
                editSearch.setText("");
                readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;

            case R.id.btnFestival:
            case R.id.btnFestival1:
                initSearchBtnSet();
                showKeyboard(View.VISIBLE);
                imgFestival.setImageResource(R.drawable.btn_festival);
                txtFestival1.setTextColor(C.tintColor);
                btnFestival1.setEnabled(false);
                searchType = C.search_type_festival;
                readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;

            case R.id.btnArtist:
            case R.id.btnArtist1:
                initSearchBtnSet();
                showKeyboard(View.VISIBLE);
                imgArtist.setImageResource(R.drawable.btn_artist);
                txtArtist1.setTextColor(C.tintColor);
                btnArtist1.setEnabled(false);
                searchType = C.search_type_artist;
                readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;

            case R.id.btnStage:
            case R.id.btnStage1:
                initSearchBtnSet();
                showKeyboard(View.VISIBLE);
                imgStage.setImageResource(R.drawable.btn_stage);
                txtStage1.setTextColor(C.tintColor);
                btnStage1.setEnabled(false);
                searchType = C.search_type_stage;
                readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;

            case R.id.btnTag:
                initSearchBtnSet();
                imgTag.setImageResource(R.drawable.btn_sharp);
                txtTag.setTextColor(C.tintColor);
                btnTag.setEnabled(false);
                searchType = C.search_type_tag;
                //final TextView txtView = (TextView)v;
                AlertDialog.Builder builder2;
                builder2 = new AlertDialog.Builder(this);
                final String[] array = {"Eclectic Rock","Post Punk","Punk","RnB","Soul","Surf Rock","אינדי","אינדי אלקטרוני","אינדי פופ","אינדי רוק","אינדסטריאל","אינסטרומנטלי","אלטרנטיבי","אלקטרוני","אמביינט","אסיד ג'אז","אסיד האוס","אקוסטי","אתני","בלוז","ג'אז","דאב","היפהופ","יווני","טראנס","טראפ","ים תיכוני","ישראלי","מוזיקה אתיופית","מוזיקת עולם","מטאל","נויז רוק","סטונר רוק","ספוקן וורד","ספרדית","פאנק Funk","פולק","פוסט רוק","פופ","פופ ישראלי","פסיכאדלי","צרפתית","קאנטרי","קלאסי","ראפ","רגאיי","רוק","רוק אלטרנטיבי","רוק ישראלי"};
                builder2.setTitle("חפש אמן על פי תגית").setItems(array, new DialogInterface.OnClickListener() {
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
                        editSearch.setText(array[which].toString());
                        //readJsonDateForSearch(editSearch.getText().toString(), searchType);

                    }
                }).show();
                //readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;

            case R.id.btnLocation:
                if (!hasLocation) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(this);
                    builder.setTitle("שגיאה")
                            .setMessage("לא ניתן למצוא את מיקומך")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .show();
                }
                else {
                    initSearchBtnSet();
                    showKeyboard(View.VISIBLE);
                    lnLocTime.setVisibility(View.VISIBLE);
                    imgShow.setImageResource(R.drawable.btn_horn);
                    txtShow.setTextColor(C.tintColor);
                    btnShow.setEnabled(false);
                    searchType = C.search_type_show;
                    locationLabel.setText("האזור שלי");
                    selectLocations(true);
                }
                break;
            case R.id.btnShow:
                initSearchBtnSet();
                showKeyboard(View.VISIBLE);
                lnLocTime.setVisibility(View.VISIBLE);
                imgShow.setImageResource(R.drawable.btn_horn);
                txtShow.setTextColor(C.tintColor);
                btnShow.setEnabled(false);
                searchType = C.search_type_show;
                readJsonDateForSearch(editSearch.getText().toString(), searchType);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (T != null) {
            T.cancel();
            T = null;
        }
        sf.unRegisterSoftKeyboardCallback();
    }


    @Override
    public void onBackPressed() {
        if (btnX.getVisibility() == View.VISIBLE) {
            if ( !isClickHeart )
                showKeyboard(View.GONE);
//                isClickHeart = false;
//                showHeartButton();
            G.hideKeyboard(getBaseContext(), btnX);
            for (View cell : arrSearchCell) {
                lnSearchDetail.removeView(cell);
            }
            arrSearchInfo.clear();
            arrSearchCell.clear();
            lnSearchDetail.invalidate();
            editSearch.setText("");
            readJsonDateForSearch(editSearch.getText().toString(), searchType);
        }
        else {
            if (isLoggedIn) {
                moveTaskToBack(true);
            }
            else {
                super.onBackPressed();
            }
        }

    }
    ////Starts here
    @Override
    protected void onResume() {
        super.onResume();

//        Log.e( "showapp", "onResume Before");
        if (adapter != null && adapter2 != null) {
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
//        Log.e( "showapp", "onResume After");

        isActivityInFront = true;
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        Log.e( "showapp", "onPause before");
        if( adapter != null && adapter2 != null ) {
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
//        Log.e( "showapp", "onPause After");

        isActivityInFront = false;
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            readJsonData();

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            hasLocation = true;

            //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            Log.e("location", "" + currentLatitude + " WORKS " + currentLongitude + "");
            readJsonData();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        readJsonData();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                readJsonData();
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            readJsonData();
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }
    //////////

    private  class MyPagerAdapter extends  PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public MyPagerAdapter(Context context) {
            if (!isActivityInFront)
                return;
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if (doNotifyDataSetChangedOnce) {
                doNotifyDataSetChangedOnce = false;
////                notifyDataSetChanged();
            }

            if (!isActivityInFront)
                return 0;

            if( this.equals(adapter) ) {
                return topCarousel.size();
            }
            else {
                return bottomCarousel.size();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (!isActivityInFront)
                return false;
            doNotifyDataSetChangedOnce = true;
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            if (!isActivityInFront)
                return null;

            doNotifyDataSetChangedOnce = true;

            View itemView = mLayoutInflater.inflate(R.layout.cell_home, container, false);
            View rnDetail = itemView.findViewById(R.id.rnDetail);
            rnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CarouSelInfo info = (CarouSelInfo) view.getTag();
                    Intent intent = new Intent( HomeActivity.this, MainActivity.class );

                    Bundle b = new Bundle();
                    int id = 1;
                    if (info.name != null && !info.name.equals("אין אירועים באיזורך היום")) {
                        if( info.artId != 0 )
                            id = info.artId;
                        b.putInt("artist_id", id);
                        b.putInt("artist_type", 1);
                        Log.e("Clicked carousel","With "+container);
                        if (container.getId() == R.id.showsPager) {
                            b.putInt("open_shows", 1);
                            Log.e("AHHHG","is adapter...");
                        }
                        intent.putExtras(b);

                        startActivityForResult(intent, 1);
                    }
                }
            });

            if( position > 0 ) {
                rnDetail.setVisibility(View.GONE);
            }

            CarouSelInfo info;
            if( this.equals(adapter) )
                info = topCarousel.get(position);
            else
                info = bottomCarousel.get(position);

            TextView txtUser = (TextView)itemView.findViewById(R.id.txtUser);
            TextView txtMeta = (TextView)itemView.findViewById(R.id.txtMeta);
            TextView pinkText = (TextView)itemView.findViewById(R.id.carouselPinkButton);

            txtUser.setText( info.name );
            txtMeta.setText( info.getMeta() );

            //Log.v("CMOON NOW", "text is: "+txtMeta.getText().toString());
            if (txtMeta.getText().toString().length() <= 1) {
                pinkText.setText(R.string.enter);
            }


            if( info.isEmpty ) {
                pinkText.setText("חיפוש");
                pinkText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Click empty","Click empty");
                        initSearchBtnSet();
                        showKeyboard(View.VISIBLE);
                        lnLocTime.setVisibility(View.VISIBLE);
                        imgShow.setImageResource(R.drawable.btn_horn);
                        txtShow.setTextColor(C.tintColor);
                        btnShow.setEnabled(false);
                        searchType = C.search_type_show;
                        readJsonDateForSearch(editSearch.getText().toString(), searchType);
                    }
                });
            }

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imgThumb);
            if( info.url.length() > 0 )
                Picasso.with(mContext).load(info.url).placeholder(R.drawable.image).fit().centerCrop().into(imageView);

            itemView.setTag(position);
            rnDetail.setTag(info);
            container.addView(itemView);
            return itemView;
        }

        int[] vLoc = new int[2];

        @Override
        public void finishUpdate(ViewGroup container) {
            doNotifyDataSetChangedOnce = true;

            int nCnt;
            if( this.equals(adapter) )
                nCnt = topCarousel.size();
            else
                nCnt = bottomCarousel.size();

            boolean isSelect = false;
            for( int i =0; i < nCnt; i++ ) {
                View v = container.getChildAt(i);

                if( v == null )
                    continue;

                v.getLocationOnScreen(vLoc);
                View rnDetail = v.findViewById(R.id.rnDetail);

                if( rnDetail != null ) {
                    if( vLoc[0] > 0 && vLoc[0] < G.w/2 && isSelect == false) {
                        rnDetail.setVisibility(View.VISIBLE);
                        isSelect = true;
                    } else
                        rnDetail.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            doNotifyDataSetChangedOnce = true;
            container.removeView((LinearLayout) object);
        }
    }

    class LoginPhase1 extends AsyncTask<String, Void, Boolean> {

        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                            Intent intent = getIntent();

                            member_id = object.getString("member_id");
                            //String userId = object.getString(member_id);
                            member_type = object.getString("member_type");
                            full_name = object.getString("full_name");
                            session_id = object.getString("session_id");


                            intent.putExtra("member_id", member_id);
                            intent.putExtra("member_type", member_type);
                            intent.putExtra("full_name", full_name);
                            intent.putExtra("reason", "login");
                            if (Integer.parseInt(object.getString("manage_id")) > 0) {
                                manage_id = Integer.parseInt(object.getString("manage_id"));
                                intent.putExtra("manage_id", manage_id);
                            }

                            //session = object.getString("session_id");
                            //is_new_user = object.getBoolean("newPackage");
                            Log.e("MEMBER IDD", "member_id: "+member_id+"\tSession: "+session_id);
                            return true;
                        }
                        else {
                            //if (dialog != null) { 		dialog.cancel(); 	}
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
                //if (dialog != null) { 		dialog.cancel(); 	}
                //Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
                //System.out.println("Error Fetching Data");

                /*if (error != null) {
                    new android.app.AlertDialog.Builder(HomeActivity.this)
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
                    new android.app.AlertDialog.Builder(HomeActivity.this)
                            .setTitle("שגיאה")
                            .setMessage(R.string.ErrorFetchingData)
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }*/
                Log.d("autoLogin","FAILED");
            }

            else {
                Log.v("LOGIN", "Success! ID: "+member_id);
                //dialog.cancel();

                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("session_id",session_id);
                editor.putString("member_id", member_id);
                editor.putString("member_type", member_type);
                editor.apply();

                updateMemberUI();


                //String url = "http://mitvrd.com/mobile/checkPackageType.php?userId="+userId;
                ////Log.e("Will Fetch", "URLofLogin: "+url);
                //Continue... FINISHED
            }
        }
    }
}
