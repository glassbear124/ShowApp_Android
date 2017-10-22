package offek.com.showapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Xiaosoft on 5/18/17.
 */

public class CarouSelInfo {

    public CarouSelInfo() {

    }

    int id, artId, artType;
    String name, url, place, date, date_format, hour, phone;

    String bio;
    int likes, hasLiked, promoted;
    String major_tag1, major_tag2;
    String minor_tag1, minor_tag2, minor_tag3;

    String festival_lineup;

    double lat, lng;
    String address;

    public boolean isEmpty;

    public CarouSelInfo( JSONObject obj ) {
        read(obj);
    }

    public boolean read( JSONObject obj ) {
        try {
            Log.d("Loading JSONObject: ", obj.toString());
            if( obj.has("id") )
                id = obj.getInt("id");

            if( obj.has("artist_id") )
                artId = obj.getInt("artist_id");

            if (obj.has("artist_type") )
                artType = obj.getInt("artist_type");

            if( obj.has("likes"))
                likes = obj.getInt("likes");

            if( obj.has("has_liked"))
                hasLiked = obj.getInt("has_liked");

            if( obj.has("promoted"))
                promoted = obj.getInt("promoted");

            if( obj.has("lat") )
                lat = obj.getDouble("lat");

            if( obj.has("lng") )
                lng = obj.getDouble("lng");

            name = "";
            if( obj.has("name") )
                name = obj.getString("name");

            address = "";
            if( obj.has("address") )
                address = obj.getString("address");

            festival_lineup = "";
            if( obj.has("festival_lineup"))
                festival_lineup = obj.getString("festival_lineup");

            url = "";
            if( obj.isNull("picture_url") )
                url = obj.getString("picture_url");

            phone = "";
            if( obj.isNull("phone") == false )
                phone = obj.getString("phone");

            hour = "";
            if( obj.isNull("hour") != true ) {
                hour = obj.getString("hour");
            }

            date = "";
            if( obj.has("date_unix") == true )
                date = obj.getString("date_unix");

            date_format = "";
            if( obj.has("date_format") == true )
                date_format = obj.getString("date_format");

            place = "";
            if( obj.has("place_string") == true )
                place = obj.getString("place_string");

            bio = "";
            if( obj.has("bio") == true )
                bio = obj.getString("bio");

            major_tag1 = "";
            if( obj.has("major_tag1") == true )
                major_tag1 = obj.getString("major_tag1");

            major_tag2 = "";
            if( obj.has("major_tag2") == true )
                major_tag2 = obj.getString("major_tag2");

            minor_tag1 = "";
            if( obj.has("minor_tag1") == true )
                minor_tag1 = obj.getString("minor_tag1");

            minor_tag2 = "";
            if( obj.has("minor_tag2") == true )
                minor_tag2 = obj.getString("minor_tag2");

            minor_tag3 = "";
            if( obj.has("minor_tag3") == true )
                minor_tag3 = obj.getString("minor_tag3");


            if( name.compareTo("null") == 0 )  name = "";
            if( url.compareTo("null") == 0 )   url = "";
            if( phone.compareTo("null") == 0 )   phone = "";
            if( hour.compareTo("null") == 0 )  hour = "";
            if( date.compareTo("null") == 0 )  date = "";
            if( date_format.compareTo("null") == 0 )  date_format = "";
            if( place.compareTo("null") == 0 ) place = "";
            if( bio.compareTo("null") == 0) bio = "";

            if( address.compareTo("null") == 0) address = "";

            if( major_tag1.compareTo("null") == 0) major_tag1 = "";
            if( major_tag2.compareTo("null") == 0) major_tag2 = "";
            if( minor_tag1.compareTo("null") == 0) minor_tag1 = "";
            if( minor_tag2.compareTo("null") == 0) minor_tag2 = "";
            if( minor_tag3.compareTo("null") == 0) minor_tag3 = "";

        } catch ( JSONException e) {
            e.printStackTrace();
            Log.e("JSON ERRORRZ", "ERRORZ");
            return false;
        }
        return true;
    }

    public String getMeta() {
        return hour + " " + place;
    }

    public void fillViewForCellSearch( View view, Context context, int searchType ) {
        // fill for layout.cell_search
        ImageView imgThumb = (ImageView) view.findViewById(R.id.imgThumb);
        TextView txtLoc = (TextView)view.findViewById(R.id.txtLoc);
        //Log.v("fillingSearchImg",url);
        if( url != null && url.length() > 0 ) {
            if (url.equals("h134")) {
                imgThumb.setVisibility(View.GONE);
                txtLoc.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            else {
                Picasso.with(context).load(url).placeholder(R.drawable.image).fit().centerCrop().into(imgThumb);
                imgThumb.setVisibility(View.VISIBLE);
            }
        }


        if( searchType == C.search_type_festival && (url == null || url.length() == 0 ) ) {
            imgThumb.setImageResource(R.drawable.image_festival);
        }

        TextView txtName = (TextView)view.findViewById(R.id.txtName);
        TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
        TextView txtMainTag1 = (TextView)view.findViewById(R.id.txtMainTag1);
        TextView txtMainTag2 = (TextView)view.findViewById(R.id.txtMainTag2);

        if( searchType != C.search_type_stage && searchType != C.search_type_tag) { //NOT STAGE
            txtName.setText(name);
            if( major_tag1 != null && major_tag1.length() > 0 )
                txtMainTag1.setText( major_tag1 );
            else
                txtMainTag1.setVisibility(View.GONE);

            if( major_tag2 != null && major_tag2.length() > 0 )
                txtMainTag2.setText( major_tag2 );
            else
                txtMainTag2.setVisibility(View.GONE);

            if( hour != null && hour.length() > 0 )
                if (searchType == C.search_type_show)
                    txtTime.setText( "תאריך: " + date_format.substring(0, date_format.length() - 5) + " - שעה: " + hour );
                else
                    txtTime.setText( "שעה: " + hour );

            if( searchType == C.search_type_artist || searchType == C.search_type_festival) {
                txtTime.setVisibility(View.INVISIBLE);
                txtLoc.setVisibility(View.INVISIBLE);
            }
            else if (searchType == C.search_type_show) {
                txtLoc.setText("מיקום: "+ place);
            }

            if (artType == 2) {
                txtMainTag1.setVisibility(View.GONE);
                txtMainTag2.setVisibility(View.GONE);
            }
        }
        else //if (!tag)
        {
            txtLoc.setText(name);

            txtTime.setVisibility(View.INVISIBLE);
            if (searchType != C.search_type_tag) {
                txtName.setVisibility(View.GONE);
                txtMainTag1.setVisibility(View.GONE);
                txtMainTag2.setVisibility(View.GONE);
            }
            else {
                txtLoc.setVisibility(View.INVISIBLE);
                txtName.setText(name);
                if( major_tag1.length() > 0 )
                    txtMainTag1.setText( major_tag1 );

                if( major_tag2.length() > 0 )
                    txtMainTag2.setText( major_tag2 );
            }
            //if (searchType == C.search_type_tag)

        }
        /*else { // IS tag
            txtName.setText(name);
            if( major_tag1.length() > 0 )
                txtMainTag1.setText( major_tag1 );

            if( major_tag2.length() > 0 )
                txtMainTag2.setText( major_tag2 );

            txtName.setVisibility(View.VISIBLE);
            txtTime.setVisibility(View.GONE);
            txtMainTag1.setVisibility(View.VISIBLE);
            txtMainTag2.setVisibility(View.VISIBLE);
        }*/
    }
}
