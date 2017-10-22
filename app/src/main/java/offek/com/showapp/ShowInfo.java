package offek.com.showapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XiaoSoft on 5/18/17.
 */

public class ShowInfo {

    public ShowInfo() {}

    int artId;
    int showId;
    String place_id;
    String date_unix, date_format, hour, day, month;
    String  name, artist_name_temp;
    String order_url, order_phone;
    String description, place_string;
    float price;

    public ShowInfo( JSONObject obj ) {
        read(obj);
    }

    public boolean read( JSONObject obj ) {
        try {
            if( obj.has("artist_id") == true )
                artId = obj.getInt("artist_id");

            if( obj.has("show_id") == true )
                showId = obj.getInt("show_id");


            if( obj.has("place_id") == true )
                place_id = obj.getString("place_id");

            if( obj.has("price") == true )
                price = (float)obj.getDouble("price");

            date_unix = "";
            if( obj.has("date_unix") == true )
                date_unix = obj.getString("date_unix");

            date_format = "";
            if( obj.has("date_format") == true )
                date_format = obj.getString("date_format");

            hour = "";
            if( obj.isNull("hour") != true ) {
                hour = obj.getString("hour");
            }

            day = "";
            if( obj.isNull("day") != true ) {
                day = obj.getString("day");
            }

            month = "";
            if( obj.isNull("month") != true ) {
                month = obj.getString("month");
            }

            place_string = "";
            if( obj.has("place_string") == true ) {
                place_string = obj.getString("place_string");
            }

            name = "";
            if( obj.has("name") == true )
                name = obj.getString("name");

            artist_name_temp = "";
            if( obj.has("artist_name_temp") == true )
                artist_name_temp = obj.getString("artist_name_temp");

            order_url = "";
            if( obj.has("order_url") == true )
                order_url = obj.getString("order_url");

            order_phone = "";
            if( obj.has("order_phone") == true )
                order_phone = obj.getString("order_phone");

            description = "";
            if( obj.has("description") == true )
                description = obj.getString("description");

            if( date_unix.compareTo("null") == 0 )  date_unix = "";
            if( date_format.compareTo("null") == 0 )   date_format = "";
            if( hour.compareTo("null") == 0 )  hour = "";
            if( day.compareTo("null") == 0 )  day = "";
            if( month.compareTo("null") == 0 )  month = "";
            if( place_string.compareTo("null") == 0 )  place_string = "";
            if( name.compareTo("null") == 0 )  name = "";
            if( artist_name_temp.compareTo("null") == 0 )  artist_name_temp = "";
            if( order_url.compareTo("null") == 0 ) order_url = "";
            if( order_phone.compareTo("null") == 0 ) order_phone = "";
            if( description.compareTo("null") == 0 ) description = "";

        } catch ( JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getMeta() {
        return hour;
    }

    public boolean isFree( ) {
        if( price <= 0 )
            return true;
        return false;
    }
}
