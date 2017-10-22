package offek.com.showapp;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xiaosoft on 5/3/17.
 */

public class G {
    public static int w;
    public static int h;

    public static boolean isUser;

    public static void hideKeyboard( Context context, View v ) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showKeyboard( Context context, View v ) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public static String getSearchType( int searchType ) {
        if( searchType == C.search_type_artist )
            return "artists";
        else if(searchType == C.search_type_festival )
            return "festivals";
        else if(searchType == C.search_type_stage )
            return "stages";
        else if(searchType == C.search_type_tag )
            return "tags";
        else if(searchType == C.search_type_show )
            return "shows";
        else
            return "shows";
    }
}

/*

• Change order of button in home screen
• If Main Activity has a property set to true, show a text view instead of a table view on segment button pressed. (Or change existing text view text)
• In search, if result is of “festival” type, and result has no image, show a default “festival image” (right now it shows a default “artist image”)

• Fix image carousel crash, error says “notifyDataSetChanged()” not called.

*/