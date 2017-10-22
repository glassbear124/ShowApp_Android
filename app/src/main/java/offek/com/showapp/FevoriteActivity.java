package offek.com.showapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class FevoriteActivity extends AppCompatActivity  {


    LinearLayout lnScrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fevorite);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        G.w = metrics.widthPixels;
        G.h = metrics.heightPixels;

        lnScrl = (LinearLayout)findViewById(R.id.lnScrl);
        readJsonData();
    }

    ArrayList<CarouSelInfo> arrSearchInfo = new ArrayList<CarouSelInfo>();

    void readJsonData() {
        String url = C.base + "fetch_favorites.php?member_id="+HomeActivity.member_id;
        Log.v("loadFavsURL",url);
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("loadFavs Result",response);
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            for( int i = 0 ; i < size; i++ ) {
                                JSONObject obj = arr.getJSONObject(i);
                                CarouSelInfo info = new CarouSelInfo(obj);
                                arrSearchInfo.add(info);

                                View musicCell = getLayoutInflater().inflate(R.layout.cell_search, null);
                                info.fillViewForCellSearch(musicCell, getBaseContext(), C.search_type_artist );
                                musicCell.setTag(info);
                                musicCell.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CarouSelInfo info = (CarouSelInfo) view.getTag();
                                        Intent intent = new Intent( FevoriteActivity.this, MainActivity.class );
                                        Bundle b = new Bundle();
                                        int id = 1;
                                        if( info.artId != 0 )
                                            id = info.artId;
                                        b.putInt("artist_id", id);
                                        if (info.artType == 2)
                                            b.putInt("is_stage", 1);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });


                                lnScrl.addView(musicCell);
//                                arrSearchCell.add(musicCell);
                            }

                            if (size < 1) {
                                CarouSelInfo info = new CarouSelInfo();
                                info.name = "אין אמנים שאהבת";
                                info.url = "h134";
                                info.major_tag1 = "";
                                info.major_tag2 = "";
                                info.date_format = "";
                                info.date = "";
                                info.hour = "";
                                info.bio = "";


                                View noResultCell = getLayoutInflater().inflate(R.layout.cell_search, null);
                                info.fillViewForCellSearch(noResultCell, getBaseContext(), C.search_type_stage);

                                lnScrl.addView(noResultCell);
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
}