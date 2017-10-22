package offek.com.showapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.security.MessageDigest;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    public static ProgressDialog dialog;

    Button btnSignup;
    EditText editUsername, editAge, editPhone, editEmail, editPassword, editVerifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        editUsername = (EditText) findViewById(R.id.editUsername);
        editAge = (EditText) findViewById(R.id.editAge);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editVerifyPassword = (EditText) findViewById(R.id.editVerifyPassword);

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
        editPhone.setFilters(new InputFilter[] { filter });
        editPassword.setFilters(new InputFilter[] { filter });
        editVerifyPassword.setFilters(new InputFilter[] { filter });
        editAge.setFilters(new InputFilter[] { filter });

    }

    public void onClick(View v) {
        //Intent userLogin = new Intent( SignupActivity.this, HomeActivity.class);
        switch (v.getId()) {

            case R.id.btnSignup:
                performRegister();
                break;
        }
        //startActivity(userLogin);
    }

    void performRegister() {
        if (editUsername.getText().toString().length() > 0 && editEmail.getText().toString().length() > 0 && editPassword.getText().toString().length() > 0 && editVerifyPassword.getText().toString().length() > 0) {
            if (!editPassword.getText().toString().equals(editVerifyPassword.getText().toString())) {
                new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("שגיאה")
                        .setMessage("סיסמאות לא תואמות")
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .show();
            } else {
                if (!editEmail.getText().toString().contains("@")) {
                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("שגיאה")
                            .setMessage("אנא הכנס כתובת מייל מלאה")
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                } else {
                    proceedRegister();
                }
            }
        } else {
            new AlertDialog.Builder(SignupActivity.this)
                    .setTitle("שגיאה")
                    .setMessage("אנא מלא את כל התיבות המסומנות בכוכבית (*)")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .show();
        }
    }

    void proceedRegister() {
        if (isNetworkAvailable()) {
            dialog = new ProgressDialog(SignupActivity.this);
            dialog.setMessage(SignupActivity.this.getString(R.string.PleaseWait));
            dialog.setTitle("מוסיף...");
            if (!dialog.isShowing())
                dialog.show();
            dialog.setCancelable(false);

            final String email = editEmail.getText().toString();
            String name = editUsername.getText().toString().replace(" ", "{^");
            String phone = editPhone.getText().toString();
            String md5_password = md5(editPassword.getText().toString());

            String url = C.base+"register.php?email=" + email + "&PasSwo=" + md5_password + "&phone=" + phone + "&name=" + name;
            Log.v("url", url);
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                JSONArray arr = new JSONArray(response);
                                int size = arr.length();
                                for (int i = 0; i < size; i++) {

                                    JSONObject obj = arr.getJSONObject(i);
                                    if (!obj.has("Error")) {

                                        Log.v("ADD USER", "Success!");
                                        dialog.cancel();
                                        new android.app.AlertDialog.Builder(SignupActivity.this)
                                                .setTitle("בוצע בהצלחה")
                                                .setMessage("")
                                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //do nothing
                                                        LoginDetailActivity.editUsername.setText(email);
                                                        finish();
                                                    }
                                                })
                                                .show();
                                        //session = object.getString("session_id");
                                        //is_new_user = object.getBoolean("newPackage");
                                        ////Log.e("MEMBER IDD", "member_id: "+userId+"\tSession: "+session);
                                        //return true;

                                    } else {

                                        //System.out.println("FATAL ERROR!! "+object.getString("Error"));
                                        String error = obj.getString("Error");

                                        if (error != null) {
                                            new android.app.AlertDialog.Builder(SignupActivity.this)
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

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    new android.app.AlertDialog.Builder(SignupActivity.this)
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
