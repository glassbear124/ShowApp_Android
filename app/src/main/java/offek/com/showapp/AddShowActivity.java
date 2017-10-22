package offek.com.showapp;

import android.accessibilityservice.AccessibilityService;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.text.InputType.TYPE_CLASS_TEXT;

public class AddShowActivity extends AppCompatActivity implements View.OnClickListener  {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    LinearLayout lnMain;
    View btnSelectDate, btnCancel;
    TextView btnAddshow, btnRemove;
    TextView txtDate;
    EditText editFirst, editDesc;
    EditText editEmail, editPassword, editUrl, editStagePhone;
    TextView txtLoc;

    TextView popularLabel;
    Button btnRandom;
    ToggleButton togglePopular;

    private Calendar calendar;
    private int year, month, day;
    private int hour, min;
    private long unixtime;
    private String dateFormat = "";
    private String hourFormat = "";
    private String dayFormat = "";
    private String monthFormat = "";

    View btnCheck, btnX;
    EditText editLoc;
    LinearLayout lnHeartDetail;
    public ProgressBar activityIndicator;

    String strLoc;

    // parameter
    int isStage, artId, showId, stageId, stageShow;
    String location_id = "";
    String show_artist_id = "";
    int changed_date = 0;
    int changed_location = 0;
    String stageManageId = "";
    boolean isAdmin = true;

    ArrayList<TextView> arrCells = new ArrayList<TextView>();

    public static ProgressDialog dialog;

    void readJsonDateWithShowInfo() {
        // http://216.172.178.47/~intopi/ShowApp/fetch_show_info.php?artist_id=1&show_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_show_info.php?artist_id=" + String.format("%d&show_id=%d", artId, showId);
        Log.d("Getting Show Info url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            ShowInfo show = new ShowInfo(obj);

                            txtLoc.setText( show.place_string );
                            location_id = show.place_id;
                            editFirst.setText( String.valueOf(show.price) );
                            editEmail.setText(show.order_phone);
                            editUrl.setText(show.order_url);
                            editDesc.setText( show.description );
                            txtDate.setText( String.format("תאריך נבחר: %s %s", show.date_format, show.hour ));
                            if (show.date_unix.length() > 0)
                                unixtime = Long.parseLong(show.date_unix);


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

    void readJsonDateWithStageInfo() {
        Log.v("Performing", "readJsonDateWithStageInfo");
        // http://216.172.178.47/~intopi/ShowApp/fetch_show_info.php?artist_id=1&show_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_stage_info.php?stage_id=" + String.format("%d", stageId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);
                            ShowInfo show = new ShowInfo(obj);

                            if (obj.getString("member_id") != null)
                                stageManageId = obj.getString("member_id");
                            if (obj.getString("name") != null)
                                editFirst.setText(obj.getString("name"));
                            if (obj.getString("address") != null)
                                txtLoc.setText(obj.getString("address"));
                            if (obj.getString("picture_url") != null)
                                editUrl.setText(obj.getString("picture_url"));
                            if (obj.getString("phone") != null)
                                editStagePhone.setText(obj.getString("phone"));
                            if (obj.getString("bio") != null)
                                editDesc.setText(obj.getString("bio"));
                            if (obj.getString("email") != null)
                                editEmail.setText(obj.getString("email"));
                            if (obj.getString("password") != null)
                                editPassword.setText(obj.getString("password"));
                            if (obj.getString("promoted") != null) {
                                int value = Integer.parseInt(obj.getString("promoted"));
                                if (value > 0)
                                    togglePopular.setChecked(true);
                                else
                                    togglePopular.setChecked(false);
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

    void readJsonWithSearchLoc( String str ) {

        for( TextView cell : arrCells ) {
            cell.setText("");
        }

        str = str.replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");;
        activityIndicator.setVisibility(View.VISIBLE);
        // http://216.172.178.47/~intopi/ShowApp/find_location.php?search=Beijing
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "find_location.php?search=" + str;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int size = 15;
                        try {
                            JSONArray arr = new JSONArray(response);
                            Log.v("locationsResponse",response);
                            size = arr.length();
                            if( size > 15 ) {
                                size = 15;
                            }
                            Log.v("locations size",size+" vs. real: "+arr.length());

                            for( int i = 0; i < size; i++ ) {
                                JSONObject obj = arr.getJSONObject(i);
                                TextView txtCell = arrCells.get(i);
                                txtCell.setText( obj.getString("name"));
                                txtCell.setTag(Integer.parseInt(obj.getString("id")));

                            }

                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
                        Log.v("searchNot","locations "+arrCells.size());
                        if (size <= 0 && editLoc.getText().length() > 1) {
                            Log.v("searchEmpty","locations");
                            TextView txtCell = arrCells.get(0);
                            txtCell.setText( editLoc.getText().toString());
                            txtCell.setTag(0);
                            arrCells.add(txtCell);
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

    void readJsonWithSearchArt( String str ) {

        for( TextView cell : arrCells ) {
            cell.setText("");
        }

        str = str.replace(" ", "{^").replace("''","").replace("'","").replace("&","").replace("?","").replace("׳","");;
        activityIndicator.setVisibility(View.VISIBLE);
        // http://216.172.178.47/~intopi/ShowApp/find_location.php?search=Beijing
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

                            for( int i = 0; i < size; i++ ) {
                                JSONObject obj = arr.getJSONObject(i);
                                TextView txtCell = arrCells.get(i);
                                txtCell.setText( obj.getString("name"));
                                txtCell.setTag(Integer.parseInt(obj.getString("id")));

                            }

                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
                        if (arrCells.size() <= 0 && editLoc.getText().length() > 1) {
                            Log.v("searchEmpty","locations");
                            TextView txtCell = new TextView(getApplicationContext());
                            txtCell.setText( editLoc.getText().toString());
                            txtCell.setTag(0);
                            arrCells.add(txtCell);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addshow);
        strLoc = "";

        isStage = artId = showId = stageId = -1;
        Bundle b = getIntent().getExtras();
        if(b != null) {
            if (b.getInt("notAdmin") > 0)
                isAdmin = false;
            stageShow = b.getInt("stageShow");
            if (stageShow > 0)
                show_artist_id = String.valueOf(b.getInt("artId"));
            artId = b.getInt("artId");
            showId = b.getInt("showId");
            isStage = b.getInt("isStage");
            stageId = b.getInt("stageId");


        }


        Log.d("stageId", ""+stageId);
        Log.d("artId", ""+artId);
        Log.d("showId", ""+showId);
        Log.d("isAdmin",""+isAdmin);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        lnMain = (LinearLayout)findViewById(R.id.lnMain);
        btnSelectDate = findViewById(R.id.btnSelectDate); btnSelectDate.setOnClickListener(this);
        btnAddshow = (TextView)findViewById(R.id.btnAddshow); btnAddshow.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel); btnCancel.setOnClickListener(this);
        btnRemove = (TextView)findViewById(R.id.btnRemove); btnRemove.setOnClickListener(this);
        btnRemove.setVisibility(View.GONE);

        txtDate = (TextView)findViewById(R.id.txtDate);

        txtLoc = (TextView)findViewById(R.id.txtLoc); txtLoc.setOnClickListener(this);
        editFirst = (EditText)findViewById(R.id.editFirst);

        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editUrl = (EditText)findViewById(R.id.editUrl);
        editStagePhone = (EditText)findViewById(R.id.editStagePhone);


        popularLabel = (TextView)findViewById(R.id.popularTextView);
        togglePopular = (ToggleButton)findViewById(R.id.popularToggleButton);
        btnRandom = (Button)findViewById(R.id.randomizePassword); btnRandom.setOnClickListener(this);


        editDesc = (EditText)findViewById(R.id.editDesc);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR);
        min  = calendar.get(Calendar.MINUTE);

        btnCheck = findViewById(R.id.btnCheck); btnCheck.setOnClickListener(this);
        btnX = findViewById(R.id.btnX); btnX.setOnClickListener(this);
        activityIndicator = (ProgressBar) findViewById(R.id.activityIndicator);
        editLoc = (EditText)findViewById(R.id.editLoc);
        editLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editLoc.getText().toString();
                if (stageShow > 0) {
                    if( str.length() > 2 ) {
                        readJsonWithSearchArt(str);
                    }
                }
                else {
                    if( str.length() > 2 ) {
                        readJsonWithSearchLoc(str);
                    }
                }
            }
        });

        lnHeartDetail = (LinearLayout)findViewById(R.id.lnHeartDetail);

        arrCells.clear();

        for( int i = 0; i < 15; i++ ) {
            View heartCell = getLayoutInflater().inflate(R.layout.cell_heart, null);
            final TextView txtCell = (TextView)heartCell.findViewById(R.id.txtCell);
            heartCell.setTag(i);
            heartCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView cell = (TextView)view.findViewById(R.id.txtCell);
                    strLoc = cell.getText().toString();
                    if( strLoc.length() > 0 ) {
                        if (stageShow > 0)
                            show_artist_id = String.valueOf(cell.getTag().toString());
                        else
                            location_id = String.valueOf(cell.getTag().toString());
                        Log.v("Got Location ID", "ID: "+location_id);
                        txtLoc.setText(strLoc);
                        txtLoc.setTextColor(Color.BLACK);
                        showSearch(View.GONE);
                        G.hideKeyboard(getBaseContext(), editLoc);

                        changed_location = 1;
                    }
                }
            });
            txtCell.setText("");
            arrCells.add(txtCell);
            lnHeartDetail.addView(heartCell);
        }

        if (!isAdmin) {
            txtLoc.setVisibility(View.GONE);
            editEmail.setVisibility(View.GONE);
            editPassword.setVisibility(View.GONE);
            btnRandom.setVisibility(View.GONE);
            popularLabel.setVisibility(View.GONE);
            togglePopular.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        }

        if( isStage > 0 ) {

            editStagePhone.setVisibility(View.VISIBLE);
            editStagePhone.setInputType(InputType.TYPE_CLASS_PHONE);
            editStagePhone.setHint("טלפון");


            btnSelectDate.setVisibility(View.GONE);
            txtDate.setVisibility(View.GONE);

            editFirst.setInputType(TYPE_CLASS_TEXT);
            editFirst.setHint("שם במה");
            txtLoc.setHint("כתובת");
            txtLoc.setText("כתובת");

            editDesc.setHint("אודות במה (מוגבל ל-300 תווים)");
            editDesc.setMaxEms(300);

            btnAddshow.setText("הוסף במה");
            if ( stageId > 0 ) {
                btnAddshow.setText("ערוך במה");
                btnRemove.setVisibility(View.VISIBLE);
                readJsonDateWithStageInfo();
            }

        } else {

            btnRandom.setVisibility(View.GONE);
            popularLabel.setVisibility(View.GONE);
            togglePopular.setVisibility(View.GONE);
            editPassword.setVisibility(View.GONE);

            editEmail.setInputType(InputType.TYPE_CLASS_PHONE);
            editEmail.setHint("טלפון להזמנה");
            editUrl.setHint("אתר להזמנה");

            editDesc.setHint("אודות הופעה (מוגבל ל-300 תווים)");
            editDesc.setMaxEms(300);

            if (stageShow > 0) {
                txtLoc.setText("חפש אמן");
                txtLoc.setVisibility(View.VISIBLE);
            }

            if( artId >= 0 && showId > 0 ) {
                btnAddshow.setText("ערוך הופעה");
                btnRemove.setVisibility(View.VISIBLE);
                txtLoc.setOnClickListener(null);
                readJsonDateWithShowInfo();
                Log.d("shouldShow Remove", "YES");
            }
        }

        /* To restrict Space Bar in Keyboard */
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



    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    boolean mFirstDate;

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                    if( mFirstDate == true ) {
                        mFirstDate = false;
                        // TODO Auto-generated method stub
                        mFirstTime = true;
                        year = arg1;
                        month = arg2+1;
                        day = arg3;
                        showDate();
                    }
                }
            };


    boolean mFirstTime;   //To track number of calls to onTimeSet()
    TimePickerDialog mTimePicker;

    private void showDate() {

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(mFirstTime == true) {
                    mFirstTime = false;
                    String str = String.format( "תאריך נבחר: %d/%02d/%02d %02d:%02d",
                            year, month, day, selectedHour, selectedMinute );
                    txtDate.setText(str);
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"), Locale.getDefault());
                    cal.set(year, month - 1, day, selectedHour, selectedMinute, 0);
                    //long timestamp = cal.getTime();
                    unixtime = cal.getTimeInMillis() / 1000;

                    dateFormat = String.format( "%02d/%02d/%d", day, month, year );
                    hourFormat = String.format( "%02d:%02d", selectedHour, selectedMinute );
                    dayFormat = String.format( "%02d", day );
                    monthFormat = String.format( "%02d", month );
                    Log.v("Got UNIX!", ""+unixtime+"\n"+dateFormat+" @ "+hourFormat);
                    changed_date = 1;

                }
            }
        }, hour, min, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public  void onClick( View v ) {
        if( v == btnSelectDate ) {
            mFirstDate = true;
            showDialog(999);
        } else if( v == btnCancel ) {
            onBackPressed();
        } else if( v == btnAddshow ) {
            if (isStage > 0) {
                if (stageId > 0) {
                    editStage();
                    Log.e("Clciked GOOO", "Edit stage");
                }
                else {
                    addStage();
                    Log.e("Clciked GOOO", "Add stage");
                }
            }
            else {
                if (artId >= 0 && showId > 0)
                    editShow();
                else
                    addShow();
            }
        } else if( v == txtLoc ) {
            editLoc.setText( strLoc );
            showSearch(View.VISIBLE);
            G.showKeyboard(getBaseContext(), editLoc);
        }
        else if( v == btnX || v == btnCheck ) {

            if( v == btnCheck ) {
                strLoc = editLoc.getText().toString();
                if( strLoc.length() > 0 ) {
                    txtLoc.setTextColor(Color.BLACK);
                    txtLoc.setText(editLoc.getText());
                    changed_location = 1;
                }
                else {
                    if (isStage > 0)
                        txtLoc.setText("כתובת");
                    else {
                        if (stageShow > 0) {
                            txtLoc.setText("חפש אמן");
                        } else {
                            txtLoc.setText("חפש במה");
                        }
                    }
                    txtLoc.setTextColor(0xFF757575);
                }
            }
            showSearch(View.GONE);
            G.hideKeyboard(getBaseContext(), editLoc);
        }
        else if ( v == btnRandom) {
            int random = (int )(Math.random() * 9 + 6);
            String pass = randomString(random);
            editPassword.setText(pass);

        }
        else if ( v == btnRemove) {
            if (isStage > 0)
                removeStage();
            else
                removeShow();
        }

    }

    void showSearch( int isShow ) {
        int isShowOther;
        if( isShow == View.VISIBLE )
            isShowOther = View.GONE;
        else
            isShowOther = View.VISIBLE;

        lnMain.setVisibility(isShowOther);

        btnX.setVisibility(isShow);
        btnCheck.setVisibility(isShow);
        lnHeartDetail.setVisibility(isShow);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (btnX.getVisibility() == View.VISIBLE) {
            activityIndicator.setVisibility(View.GONE);
            if (lnHeartDetail.getVisibility() == View.VISIBLE) {
                if (txtLoc.getText().toString().length() <= 0) {
                    if (isStage > 0)
                        txtLoc.setText("כתובת");
                    else {
                        if (stageShow > 0) {
                            txtLoc.setText("חפש אמן");
                        } else {
                            txtLoc.setText("חפש במה");
                        }
                    }
                }

                txtLoc.setTextColor(0xFF757575);
                showSearch(View.GONE);
                G.hideKeyboard(getBaseContext(), editLoc);
            }
            else {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }

    }

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    void addStage() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddShowActivity.this);
            dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";
            String stageName = editFirst.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            String aboutText =aboutText = editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            try {
                aboutText = URLEncoder.encode(aboutText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String location_name = txtLoc.getText().toString().replace(" ", "+");

            String isPopular;
            if (togglePopular.isChecked())
                isPopular = "1";
            else
                isPopular = "0";

            if (stageShow > 0)
                url = C.base+"add_stage.php?stage_name=" + stageName + "&manage_email=" + editEmail.getText().toString()+"&manage_password="+editPassword.getText().toString()+"&image_url="+editUrl.getText().toString()+"&about="+aboutText+"&location="+location_name+"&is_popular="+isPopular;
            else
                url = C.base+"add_stage.php?stage_name=" + stageName + "&manage_email=" + editEmail.getText().toString()+"&manage_password="+editPassword.getText().toString()+"&image_url="+editUrl.getText().toString()+"&phone="+editStagePhone.getText().toString()+"&about="+aboutText+"&location="+location_name+"&is_popular="+isPopular;
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
                                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                    new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
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

    void addShow() {
        if (txtDate.getText().length() > 15) {
            if (isNetworkAvailable()) {
                dialog = new ProgressDialog(AddShowActivity.this);
                dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
                dialog.setTitle("מוסיף...");
                if (!dialog.isShowing())
                    dialog.show();
                dialog.setCancelable(false);

                String url = "";
                String price = editFirst.getText().toString().replace(" ", "{^");
                String aboutText = editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
                try {
                    aboutText = URLEncoder.encode(aboutText, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String artistName = "";
                String stageName = "";
                if (stageShow > 0) {
                    stageName = HomeActivity.full_name.replace(" ", "{^");
                    artistName = txtLoc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
                }
                else
                    stageName = txtLoc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");

                String order_url = editUrl.getText().toString();
                try {
                    order_url = URLEncoder.encode(editUrl.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            /*if (order_url.contains("#")) {
                order_url = order_url.substring(0, order_url.indexOf("#"));
            }*/

                String isPopular;
                if (togglePopular.isChecked())
                    isPopular = "1";
                else
                    isPopular = "0";

                if (stageShow > 0)
                    url = C.base+"add_show_of_stage.php?artist_id="+show_artist_id+"&artist_name="+artistName+"&stage_name=" + stageName + "&about="+aboutText+"&location_id="+HomeActivity.manage_id+"&unixtime="+unixtime+"&date_format="+dateFormat+"&day="+dayFormat+"&month="+monthFormat+"&hour_format="+hourFormat+"&price="+price+"&order_phone="+editEmail.getText().toString()+"&order_url="+order_url+"&is_popular="+"0";
                else
                    url = C.base+"add_show.php?artist_id="+artId+"&stage_name=" + stageName + "&about="+aboutText+"&location_id="+location_id+"&unixtime="+unixtime+"&date_format="+dateFormat+"&day="+dayFormat+"&month="+monthFormat+"&hour_format="+hourFormat+"&price="+price+"&order_phone="+editEmail.getText().toString()+"&order_url="+order_url+"&is_popular="+isPopular;
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                                new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
                    .setTitle("שגיאה")
                    .setMessage("אנא הכנס תאריך")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .show();
        }

    }

    void  editShow() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddShowActivity.this);
            dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";
            String price = editFirst.getText().toString().replace(" ", "{^");
            String aboutText = editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            try {
                aboutText = URLEncoder.encode(aboutText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String artistName = "";
            String stageName = "";
            if (stageShow > 0) {
                stageName = HomeActivity.full_name.replace(" ", "{^");
                artistName = txtLoc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            }
            else
                stageName = txtLoc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");

            String order_url = editUrl.getText().toString();
            try {
                order_url = URLEncoder.encode(editUrl.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String isPopular;
            if (togglePopular.isChecked())
                isPopular = "1";
            else
                isPopular = "0";

            if (stageShow > 0)
                url = C.base+"update_show_of_stage.php?artist_id="+show_artist_id+"&show_id="+showId+"&stage_name=" + stageName + "&about="+aboutText+"&location_id="+HomeActivity.manage_id+"&unixtime="+unixtime+"&date_format="+dateFormat+"&day="+dayFormat+"&month="+monthFormat+"&hour_format="+hourFormat+"&price="+price+"&order_phone="+editEmail.getText().toString()+"&order_url="+order_url+"&is_popular="+0+"&changed_date="+changed_date;
            else
                url = C.base+"update_show.php?artist_id="+artId+"&show_id="+showId+"&stage_name=" + stageName + "&about="+aboutText+"&location_id="+location_id+"&unixtime="+unixtime+"&date_format="+dateFormat+"&day="+dayFormat+"&month="+monthFormat+"&hour_format="+hourFormat+"&price="+price+"&order_phone="+editEmail.getText().toString()+"&order_url="+order_url+"&is_popular="+isPopular+"&changed_date="+changed_date;
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
                                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                    new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
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

    void editStage() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddShowActivity.this);
            dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";
            String stageName = editFirst.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            String aboutText = editDesc.getText().toString().replace(" ", "{^").replace("'","\\'").replace("\n"," ").replace("׳","\\'");
            try {
                aboutText = URLEncoder.encode(aboutText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String location_name = txtLoc.getText().toString().replace(" ", "+");

            String isPopular;
            if (togglePopular.isChecked())
                isPopular = "1";
            else
                isPopular = "0";

            url = C.base+"update_stage.php?stage_id="+ stageId +"&stage_name=" + stageName +"&manage_id=" + stageManageId + "&manage_email=" + editEmail.getText().toString()+"&manage_password="+editPassword.getText().toString()+"&image_url="+editUrl.getText().toString()+"&phone="+editStagePhone.getText().toString()+"&about="+aboutText+"&location_id="+location_id+"&location="+location_name+"&is_popular="+isPopular + "&changed_location=" + changed_location;
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
                                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                    new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
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

    void removeStage() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddShowActivity.this);
            dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוחק...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";

            url = C.base+"delete_stage.php?stage_id="+ stageId;
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
                                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                    new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
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

    void removeShow() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(AddShowActivity.this);
            dialog.setMessage(AddShowActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוחק...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            String url = "";

            url = C.base+"delete_show.php?show_id="+ showId;
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
                                        new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                                            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
                    new android.app.AlertDialog.Builder(AddShowActivity.this)
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
            new android.app.AlertDialog.Builder(AddShowActivity.this)
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
