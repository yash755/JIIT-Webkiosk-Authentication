package com.webkiosklogin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText eno, password, dob;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eno = (EditText) findViewById(R.id.eno);
        password = (EditText) findViewById(R.id.password);
        dob = (EditText) findViewById(R.id.dob);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v == login) {

            hideSoftKeyboard();

            if (new Util().check_connection(Login.this)) {

                if (validate()) {

                    String enos = eno.getText().toString();
                    String passwords = password.getText().toString();
                    String dobs = dob.getText().toString();

                    authenticate(enos, passwords, dobs);
                } else {

                    if (eno.getText().toString().trim().equals("")) {
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.eno));
                    }
                    if (password.getText().toString().trim().equals("")) {
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.password));
                    }
                    if (dob.getText().toString().trim().equals("")) {
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.dob));
                    }


                }
            } else {

                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("No Internent Connection")
                        .setContentText("Won't be able to login!")
                        .setConfirmText("Go to Settings!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                                sDialog.cancel();
                            }
                        })
                        .show();

            }


        }
    }



    void authenticate(String eno,String password,String dob){

        final AlertDialog dialog = new SpotsDialog(this, R.style.Custom);
        dialog.show();


        String url = "http://yashgupta.96.lt/lrc/login.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("eno",eno);
        params.put("password",password);
        params.put("dob",dob);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ", response.toString());
                System.out.println("Response" + response.toString());
                dialog.dismiss();

                response_is(response);


                //
                //  Here u get the ressponse
                //
                //


                //return response;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    dialog.hide();
                    new Util().showerrormessage(Login.this, "Time Out Error.....Try Later!!!");

                } else if (error instanceof AuthFailureError) {
                    dialog.hide();
                    new Util().showerrormessage(Login.this, "Authentication Error.....Try Later!!!");
                } else if (error instanceof ServerError) {
                    dialog.hide();
                    new Util().showerrormessage(Login.this,"Server Error.....Try Later!!!");
                } else if (error instanceof NetworkError) {
                    dialog.hide();
                    new Util().showerrormessage(Login.this, "Network Error.....Try Later!!!");
                } else if (error instanceof ParseError) {
                    dialog.hide();
                    Log.d("Response: ", error.toString());
                    System.out.println("Resonse" + error.toString());
                    new Util().showerrormessage(Login.this, "Unknown Error.....Try Later!!!");
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }


    private boolean validate() {
        return !eno.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")
                && !dob.getText().toString().trim().equals("");
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    void response_is(JSONArray response){

        try {

            JSONObject jsonObject = response.getJSONObject(0);

            String result = jsonObject.getString("response");

            if(result.equals("Success")){
                Intent i = new Intent(Login.this,Home.class);
                startActivity(i);

            }
            else{
                new Util().showerrormessage(Login.this,result);
            }

        } catch (JSONException e) {
        }
    }
}
