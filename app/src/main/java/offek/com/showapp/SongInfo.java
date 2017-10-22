package offek.com.showapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XiaoSoft on 5/18/17.
 */

public class SongInfo {
    public SongInfo() {}

    int id;
    String song_name;
    String url;

    public SongInfo( JSONObject obj ) {
        read(obj);
    }

    public boolean read( JSONObject obj ) {
        try {
            if( obj.has("id") == true )
                id = obj.getInt("id");

            song_name = "";
            if( obj.has("song_name") == true )
                song_name = obj.getString("song_name");

            url = "";
            if( obj.has("url") == true )
                url = obj.getString("url");

            if( song_name.compareTo("null") == 0 )  song_name = "";
            if( url.compareTo("null") == 0 )   url = "";

        } catch ( JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
