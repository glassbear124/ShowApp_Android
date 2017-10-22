package offek.com.showapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thefinestartist.ytpa.utils.YouTubeUrlParser;


import junit.framework.ComparisonFailure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int artId, artType;

    LinearLayout lnMusic, lnConsert, lnAbout;
    TextView txtMusic, txtConsert, txtAbout;
    ArrayList<CellItem> items = new ArrayList<CellItem>();
    ArrayList<View> vItems = new ArrayList<View>();

    ToggleButton btnFollow;
    TextView txtFestival;

    ImageView imgArtist;
    TextView txtUserName, txtAddress, txtPhoneLabel, txtLikes, txtBio;
    TextView txtBigTag1, txtBigTag2;
    TextView txtSmallTag1, txtSmallTag2, txtSmallTag3;
    View musicBarLine;

    CarouSelInfo mInfo;

    boolean is_stage = false;
    boolean is_festival = false;

    void readJsonData1() {
        // http://216.172.178.47/~intopi/ShowApp/fetch_profile.php?artist_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_profile.php?artist_id" + String.format("=%d", artId) + "&member_id=" + HomeActivity.member_id + "&is_stage=" + String.format(is_stage ? "1" : "0");
        Log.v("URL:", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);
                    JSONObject obj = arr.getJSONObject(0);
                    mInfo = new CarouSelInfo(obj);
                    fillViewWithCarouSelInfo();

                    if (!is_stage)
                        readJsonData3();
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

    ArrayList<ShowInfo> shows = new ArrayList<ShowInfo>();

    void readJsonData2() {

        shows.clear();

        // http://216.172.178.47/~intopi/ShowApp/fetch_shows.php?artist_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_shows.php?artist_id" + String.format("=%d", artId) + "&is_stage=" + String.format(is_stage ? "1" : "0");
        ;
        Log.v("fetchShows", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                ShowInfo showInfo = new ShowInfo(obj);
                                shows.add(showInfo);
                            }

                            fillViewWithShowInfo();
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

    ArrayList<SongInfo> songs = new ArrayList<SongInfo>();

    void readJsonData3() {
        // http://216.172.178.47/~intopi/ShowApp/fetch_songs.php?artist_id=1
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        String url = C.base + "fetch_songs.php?artist_id" + String.format("=%d", artId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            songs.clear();
                            JSONArray arr = new JSONArray(response);
                            int size = arr.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                SongInfo info = new SongInfo(obj);
                                songs.add(info);
                            }
                            fillViewWithSongInfo();

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


    ShowInfo tmpInfo;

    void fillViewWithShowInfo() {
        int size = shows.size();
        for (int i = 0; i < size; i++) {
            ShowInfo showInfo = shows.get(i);
            CellItem item = new CellItem();

            item.isFree = showInfo.isFree();

            View child = getLayoutInflater().inflate(R.layout.cell_main, null);
            child.setTag(item);

            TextView txtBuy = (TextView) child.findViewById(R.id.txtBuy);
            if (item.isFree) {
                txtBuy.setText(getResources().getString(R.string.free));
                txtBuy.setBackgroundColor(Color.TRANSPARENT);
                txtBuy.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtBuy.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                txtBuy.setText(getResources().getString(R.string.purchase));
                txtBuy.setTextColor(Color.WHITE);
                txtBuy.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtBuy.setTypeface(Typeface.DEFAULT);
            }

            txtBuy.setTag(showInfo);
            txtBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tmpInfo = (ShowInfo) view.getTag();
                    if (tmpInfo.isFree())
                        return;

                    final Dialog myDialog = new Dialog(MainActivity.this, R.style.CustomTheme);
                    myDialog.setContentView(R.layout.actionsheet);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = myDialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);

                    View txtPhone = myDialog.findViewById(R.id.txtPhone);
                    if (tmpInfo.order_phone.length() < 1) {
                        txtPhone.setVisibility(View.GONE);
                    }
                    txtPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            if (tmpInfo.order_phone.length() > 0) {
                                String strTel = "tel:" + tmpInfo.order_phone;
                                Uri number = Uri.parse(strTel); // sample-> tel:123456789
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);
                            }
                        }
                    });

                    View txtUrl = myDialog.findViewById(R.id.txtUrl);
                    if (tmpInfo.order_url.length() < 1) {

                        txtUrl.setVisibility(View.GONE);
                    }
                    txtUrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            if (tmpInfo.order_url.length() > 0) {

                                String strUrl = tmpInfo.order_url;
                                if (strUrl.substring(0, 7).compareTo("http://") == 0 &&
                                        strUrl.substring(0, 8).compareTo("https://") == 0)
                                    strUrl = "http://" + tmpInfo.order_url;

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
                                startActivity(browserIntent);
                            }
                        }
                    });

                    View tvCancel = myDialog.findViewById(R.id.tvCancel);
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

                    LinearLayout rlActionSheet = (LinearLayout) myDialog.findViewById(R.id.rlActionSheet);

                    final Animation animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up);
                    rlActionSheet.startAnimation(animation1);
                    rlActionSheet.startAnimation(animation1);

                    myDialog.show();
                    myDialog.getWindow().setGravity(Gravity.BOTTOM);
                }
            });

            TextView txtLoc = (TextView) child.findViewById(R.id.txtLoc);
            if (!is_stage)
                txtLoc.setText(showInfo.place_string);
            else
                txtLoc.setText(showInfo.name);
            TextView txtTime = (TextView) child.findViewById(R.id.txtTime);
            txtTime.setText(getResources().getString(R.string.time_colon) + showInfo.hour);
            TextView txtPrice = (TextView) child.findViewById(R.id.txtPrice);
            txtPrice.setText(getResources().getString(R.string.price_colon) +
                    String.format("%d", (int) showInfo.price));
            if ((int) showInfo.price <= 0) {
                txtPrice.setVisibility(View.INVISIBLE);
            }

            TextView txtDay = (TextView) child.findViewById(R.id.txtDay);
            txtDay.setText(String.format("%02d", Integer.parseInt(showInfo.day)));
            TextView txtMonth = (TextView) child.findViewById(R.id.txtMonth);
            String monthString = "";
            switch (Integer.parseInt(showInfo.month)) {
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

            TextView txtDescription = (TextView) child.findViewById(R.id.txtDescription);
            if (showInfo.description.length() == 0)
                txtDescription.setText("אין מידע נוסף על הופעה זו");
            else
                txtDescription.setText(showInfo.description);


            View vDetail = (View) child.findViewById(R.id.vDetail);
            vDetail.setTag(i);
            vDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = (int) view.getTag();
                    CellItem item = items.get(i);
                    View v = vItems.get(i);
                    item.isExpand = false;
                    showDetail(v);
                }
            });

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CellItem item = (CellItem) view.getTag();
                    item.isExpand = true;
                    showDetail(view);
                }
            });

            showDetail(child);
            lnConsert.addView(child);
            items.add(item);
            vItems.add(child);
        }

        if (size < 1) {
            CarouSelInfo info = new CarouSelInfo();
            info.name = "אין הופעות בקרוב";
            info.url = "h134";
            info.major_tag1 = "";
            info.major_tag2 = "";
            info.date_format = "";
            info.date = "";
            info.hour = "";
            info.bio = "";


            View noResultCell = getLayoutInflater().inflate(R.layout.cell_search, null);
            info.fillViewForCellSearch(noResultCell, getBaseContext(), C.search_type_stage);
            CellItem item = new CellItem();
            View child = getLayoutInflater().inflate(R.layout.cell_main, null);

            if (!is_festival)
                lnConsert.addView(noResultCell);
            items.add(item);
            vItems.add(child);
        }
        txtAbout.callOnClick();
    }

    void fillViewWithSongInfo() {


        int size = songs.size();
        for (int i = 0; i < size; i++) {
            final SongInfo songInfo = songs.get(i);
            final View musicCell = getLayoutInflater().inflate(R.layout.cell_music, null);
            musicCell.setId(i);
            TextView txtSong = (TextView) musicCell.findViewById(R.id.txtSong);
            ImageView imgThumb = (ImageView) musicCell.findViewById(R.id.imgThumb);

            String youtubeId = YouTubeUrlParser.getVideoId(songInfo.url);
            if (youtubeId.length() > 0) {
                String thumbUrl = String.format("https://img.youtube.com/vi/%s/default.jpg", youtubeId);
                Picasso.with(getBaseContext()).load(thumbUrl).placeholder(R.drawable.image).fit().centerCrop().into(imgThumb);
            }

//            if (mInfo.url.length() > 1)
//                Picasso.with(getBaseContext()).load(mInfo.url).placeholder(R.drawable.image).into(imgThumb);

            musicCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("Video URL", "AAaahahha");
                    int tag = musicCell.getId();
                    SongInfo songInfo = songs.get(tag);
                    Log.v("Video URL", "" + songInfo.url);

                    String youtubeId = YouTubeUrlParser.getVideoId(songInfo.url);

                    Intent intent = new Intent(getBaseContext(), YouTubePlayerActivity.class);
                    intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, youtubeId);
                    intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);
                    intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);
                    intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
                    intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            txtSong.setText(songInfo.song_name);
            lnMusic.addView(musicCell);


            Bundle b = getIntent().getExtras();
            if (b.getInt("open_shows") > 0) {
                initButton();
                txtConsert.setTypeface(Typeface.DEFAULT_BOLD);
                txtConsert.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtConsert.setEnabled(false);
                lnConsert.setVisibility(View.VISIBLE);
                getIntent().putExtra("open_shows", "");
            }
        }
        if (size < 1) {
            CarouSelInfo info = new CarouSelInfo();
            info.name = "אין תוצאות";
            info.url = "h134";
            info.major_tag1 = "";
            info.major_tag2 = "";
            info.date_format = "";
            info.date = "";
            info.hour = "";
            info.bio = "";

            View noResultCell = getLayoutInflater().inflate(R.layout.cell_search, null);
            info.fillViewForCellSearch(noResultCell, getBaseContext(), C.search_type_stage);

            lnMusic.addView(noResultCell);
        }

        if (is_festival) {
            txtConsert.setText("ליין אפ");
        }

    }

    void fillViewWithCarouSelInfo() {
        if (mInfo.url.length() > 1)
            Picasso.with(getBaseContext()).load(mInfo.url).placeholder(R.drawable.image).fit().centerCrop().into(imgArtist);
        txtUserName.setText(mInfo.name);
        txtAddress.setText(mInfo.address);
        txtAddress.setOnClickListener(this);
        if (is_stage) {
            txtAddress.setVisibility(View.VISIBLE);
            if (mInfo.phone.length() > 0) {
                txtPhoneLabel.setText(mInfo.phone);
                txtPhoneLabel.setVisibility(View.VISIBLE);
                txtPhoneLabel.setOnClickListener(this);
            }
        }
        txtLikes.setText(String.format("%d", mInfo.likes));
        txtBio.setText(mInfo.bio);

        if (mInfo.likes >= 0)
            txtLikes.setText("" + mInfo.likes);

        if (mInfo.hasLiked > 0) {
            btnFollow.setChecked(true);
            btnFollow.setBackgroundResource(R.drawable.heart_fill_132);
        }

        if (mInfo.festival_lineup.length() > 0 && is_festival) {
            txtFestival.setText(mInfo.festival_lineup);
        }

        if (mInfo.major_tag1.length() > 0)
            txtBigTag1.setText(mInfo.major_tag1);
        else
            txtBigTag1.setVisibility(View.GONE);

        if (mInfo.major_tag2.length() > 0)
            txtBigTag2.setText(mInfo.major_tag2);
        else
            txtBigTag2.setVisibility(View.GONE);

        if (mInfo.minor_tag1.length() > 0)
            txtSmallTag1.setText(mInfo.minor_tag1);
        else
            txtSmallTag1.setVisibility(View.GONE);

        if (mInfo.minor_tag2.length() > 0)
            txtSmallTag2.setText(mInfo.minor_tag2);
        else
            txtSmallTag2.setVisibility(View.GONE);

        if (mInfo.minor_tag3.length() > 0)
            txtSmallTag3.setText(mInfo.minor_tag3);
        else
            txtSmallTag3.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgArtist = (ImageView) findViewById(R.id.imgArtist);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtPhoneLabel = (TextView) findViewById(R.id.txtPhoneLabel);
        txtLikes = (TextView) findViewById(R.id.txtLikes);
        txtBio = (TextView) findViewById(R.id.txtBio);

        txtBigTag1 = (TextView) findViewById(R.id.txtBigTag1);
        txtBigTag1.setOnClickListener(this);
        txtBigTag2 = (TextView) findViewById(R.id.txtBigTag2);
        txtBigTag2.setOnClickListener(this);

        txtSmallTag1 = (TextView) findViewById(R.id.txtSmallTag1);
        txtSmallTag1.setOnClickListener(this);
        txtSmallTag2 = (TextView) findViewById(R.id.txtSmallTag2);
        txtSmallTag2.setOnClickListener(this);
        txtSmallTag3 = (TextView) findViewById(R.id.txtSmallTag3);
        txtSmallTag3.setOnClickListener(this);

        txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtAbout.setOnClickListener(this);
        txtConsert = (TextView) findViewById(R.id.txtConsert);
        txtConsert.setOnClickListener(this);
        txtMusic = (TextView) findViewById(R.id.txtMusic);
        txtMusic.setOnClickListener(this);

        lnMusic = (LinearLayout) findViewById(R.id.lnMusic);
        lnConsert = (LinearLayout) findViewById(R.id.lnConsert);
        lnAbout = (LinearLayout) findViewById(R.id.lnAbout);

        txtFestival = (TextView) findViewById(R.id.txtFestival);

        lnAbout.setVisibility(View.GONE);
        lnMusic.setVisibility(View.GONE);

        btnFollow = (ToggleButton) findViewById(R.id.btnFollow);
        btnFollow.setOnClickListener(this);

        musicBarLine = findViewById(R.id.musicBarLine);

        Bundle b = getIntent().getExtras();
        artId = -1;
        artType = 1;
        if (b != null) {
            artId = b.getInt("artist_id");
            artType = b.getInt("artist_type");
            Log.e("SOOOO", "quals: " + b.getInt("open_shows"));
            if (b.getInt("open_shows") > 0) {
                Log.e("SOOOO", "I tried clicking");
                //txtConsert.callOnClick();
            }
            if (b.getInt("is_stage") > 0)
                is_stage = true;
            if (b.getInt("is_festival") > 0)
                is_festival = true;
        }

        if (is_stage) {
            txtBigTag1.setVisibility(View.GONE);
            txtBigTag2.setVisibility(View.GONE);
            txtSmallTag1.setVisibility(View.GONE);
            txtSmallTag2.setVisibility(View.GONE);
            txtSmallTag3.setVisibility(View.GONE);

            txtMusic.setVisibility(View.GONE);
            musicBarLine.setVisibility(View.GONE);
        }

        if (is_festival) {
            txtConsert.setText("ליין אפ");
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        readJsonData1();
        readJsonData2();

    }

    public void showDetail(View v) {
        LinearLayout lnDetail = (LinearLayout) v.findViewById(R.id.lnDetail);
        CellItem item = (CellItem) v.getTag();
        if (item.isExpand) {
            lnDetail.setVisibility(View.VISIBLE);
        } else
            lnDetail.setVisibility(View.GONE);
    }

    private void initButton() {
        txtAbout.setTextColor(Color.WHITE);
        txtAbout.setTypeface(Typeface.DEFAULT);
        txtConsert.setTextColor(Color.WHITE);
        txtConsert.setTypeface(Typeface.DEFAULT);
        txtMusic.setTextColor(Color.WHITE);
        txtMusic.setTypeface(Typeface.DEFAULT);

        txtAbout.setEnabled(true);
        txtMusic.setEnabled(true);
        txtConsert.setEnabled(true);

        lnConsert.setVisibility(View.GONE);
        lnAbout.setVisibility(View.GONE);
        lnMusic.setVisibility(View.GONE);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtAbout:
                initButton();
                txtAbout.setTypeface(Typeface.DEFAULT_BOLD);
                txtAbout.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtAbout.setEnabled(false);
                lnAbout.setVisibility(View.VISIBLE);
                break;

            case R.id.txtConsert:
                initButton();
                txtConsert.setTypeface(Typeface.DEFAULT_BOLD);
                txtConsert.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtConsert.setEnabled(false);
                lnConsert.setVisibility(View.VISIBLE);
                break;

            case R.id.txtMusic:
                initButton();
                txtMusic.setTypeface(Typeface.DEFAULT_BOLD);
                txtMusic.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tint));
                txtMusic.setEnabled(false);
                lnMusic.setVisibility(View.VISIBLE);
                break;

            case R.id.txtAddress:
                if (txtAddress.getText().toString().length() > 0) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + txtAddress.getText().toString().replace(" ", "+")));
                    startActivity(intent);
                }
                break;

            case R.id.txtPhoneLabel:
                if (txtPhoneLabel.getText().toString().length() > 0) {
                    String strTel = "tel:" + txtPhoneLabel.getText().toString();
                    Uri number = Uri.parse(strTel); // sample-> tel:123456789
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
                break;


            case R.id.btnFollow:
                if (HomeActivity.member_id.length() > 0 && Integer.parseInt(HomeActivity.member_id) >= 0) {
                    if (btnFollow.isChecked()) {
                        mInfo.likes++;
                        btnFollow.setBackgroundResource(R.drawable.heart_fill_132);
                        updateLikePHP(true);
                    } else {
                        mInfo.likes--;
                        btnFollow.setBackgroundResource(R.drawable.heart_line_132);
                        updateLikePHP(false);
                    }
                    txtLikes.setText(String.format("%d", mInfo.likes));
                } else {
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("שגיאה")
                            .setMessage("עליך להתחבר")
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
                break;

            case R.id.txtBigTag1:
            case R.id.txtBigTag2:
            case R.id.txtSmallTag1:
            case R.id.txtSmallTag2:
            case R.id.txtSmallTag3:
                TextView view = (TextView) v;
                String str = view.getText().toString();

                Intent retIntent = new Intent();
                retIntent.putExtra("searchKey", str);
                setResult(Activity.RESULT_OK, retIntent);
                finish();

                break;

            case R.id.btnX:
                break;
        }
    }

    private class CellItem {
        boolean isExpand;
        boolean isFree;

        CellItem() {
        }
    }

    void updateLikePHP(boolean didLike) {

        if (isNetworkAvailable()) {
            String url = "";

            if (didLike) { //Pressed to like
                url = C.base + "add_like.php?member_id=" + HomeActivity.member_id + "&id=" + artId + "&fav_type=" + artType;
            } else { // Removing Like
                url = C.base + "remove_like.php?member_id=" + HomeActivity.member_id + "&id=" + artId + "&fav_type=" + artType;
            }

            Log.v("url", url);
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //if (dialog != null) { 		dialog.cancel(); 	}
                                JSONArray arr = new JSONArray(response);
                                int size = arr.length();
                                for (int i = 0; i < size; i++) {

                                    JSONObject obj = arr.getJSONObject(i);
                                    if (!obj.has("Error")) {

                                        //>>>>>>SUCCESS<<<<<<<
                                        //Log.v("ADD STAGE", "Success!");
                                        //session = object.getString("session_id");
                                        //is_new_user = object.getBoolean("newPackage");
                                        ////Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session);
                                        //return true;

                                    } else {

                                        //System.out.println("FATAL ERROR!! "+object.getString("Error"));
                                        String error = obj.getString("Error");

                                        //Server Error

                                        //return false;
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //Unexpected Error
                }
            });
            queue.add(stringRequest);
            //new AddArtistPhase().execute(url);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
