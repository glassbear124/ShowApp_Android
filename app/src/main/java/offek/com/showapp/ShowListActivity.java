package offek.com.showapp;

import android.accessibilityservice.AccessibilityService;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShowListActivity extends AppCompatActivity {

    ListView lstView;
    ArrayList<ShowInfo> shows = new ArrayList<ShowInfo>();
    CustomAdapter myAdapter;

    boolean isStage = false;


    public class CustomAdapter extends BaseAdapter {

        Context context;
        private LayoutInflater inflater=null;
        public CustomAdapter() {
            // TODO Auto-generated constructor stub
            context= getBaseContext();
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return shows.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView txtDate;
            TextView txtTitle;
            TextView txtContent;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.cell_main, null);
            rowView.setTag(position);

            View ln = rowView.findViewById(R.id.lnDetail);
            ln.setVisibility(View.GONE);

            View txtBuy = rowView.findViewById(R.id.txtBuy);
            txtBuy.setVisibility(View.GONE);

            ShowInfo show = shows.get(position);

            TextView txtLoc = (TextView)rowView.findViewById(R.id.txtLoc);
            if (isStage)
                txtLoc.setText(show.artist_name_temp);
            else
                txtLoc.setText(show.place_string);
            TextView txtTime = (TextView)rowView.findViewById(R.id.txtTime);
            txtTime.setText(show.hour);
            TextView txtPrice = (TextView)rowView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("%.2f",show.price));

            TextView txtDay = (TextView)rowView.findViewById(R.id.txtDay);
            txtDay.setText(String.format( "%02d", Integer.parseInt(show.day) ));
            TextView txtMonth = (TextView)rowView.findViewById(R.id.txtMonth);
            String monthString = "";
            switch (Integer.parseInt(show.month)) {
                case 1:
                    monthString = "ינואר";
                    break;
                case 2:
                    monthString = "פברואר";
                    break;
                case 3:
                    monthString = "מרץ";
                    break;
                case 4:
                    monthString = "אפריל";
                    break;
                case 5:
                    monthString = "מאי";
                    break;
                case 6:
                    monthString = "יוני";
                    break;
                case 7:
                    monthString = "יולי";
                    break;
                case 8:
                    monthString = "אוגוסט";
                    break;
                case 9:
                    monthString = "ספטמבר";
                    break;
                case 10:
                    monthString = "אוקטובר";
                    break;
                case 11:
                    monthString = "נובמבר";
                    break;
                case 12:
                    monthString = "דצמבר";
                    break;
                default:
                    monthString = "לא ידוע";
                    break;
            }
            txtMonth.setText(monthString);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent( ShowListActivity.this, AddShowActivity.class);

                    int pos = (int)v.getTag();
                    ShowInfo show = shows.get(pos);

                    Bundle b = new Bundle();
                    b.putInt("artId", show.artId);
                    b.putInt("showId", show.showId);
                    if (isStage)
                        b.putInt("stageShow", 1);
                    b.putInt("notAdmin", 0);//<<Setting isAdmin to YES
                    i.putExtras(b);

                    startActivity(i);
                }
            });
            return rowView;
        }
    }

    void readJsonData( int _artId ) {
        // http://216.172.178.47/~intopi/ShowApp/fetch_shows.php?artist_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";
        if (isStage)
            url = C.base + "fetch_shows.php?artist_id=" + String.format("%d", _artId)+"&is_stage=1";
        else
            url = C.base + "fetch_shows.php?artist_id=" + String.format("%d", _artId);
        Log.e("Showsss",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            shows.clear();
                            for( int i = 0; i < size; i++ ) {
                                ShowInfo show = new ShowInfo( arr.getJSONObject(i) );
                                shows.add(show);
                            }

                            myAdapter.notifyDataSetChanged();

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

    int artId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlist);

        Bundle b = getIntent().getExtras();
        artId = -1; // or other values
        if(b != null) {
            artId = b.getInt("artId");
            if (b.getInt("stageShow") > 0)
                isStage = true;
        }


        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        readJsonData(artId);

        shows.clear();

        lstView = (ListView)findViewById(R.id.lstView);
        myAdapter = new CustomAdapter();
        lstView.setAdapter(myAdapter);

        myAdapter.notifyDataSetChanged();
    }

}
