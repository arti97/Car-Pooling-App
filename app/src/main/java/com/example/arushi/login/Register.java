package com.example.arushi.login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class Register extends AppCompatActivity {
    EditText Email, Name, Phone, Homeadd, Carno, Pass, Pass2;
    String email, name, phone, homeadd, carno, pass, pass2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Button signup = (Button) findViewById(R.id.save);
        signup.setOnClickListener(new Button.OnClickListener() {

                                      public void onClick(View v) {
                                          Email = (EditText) findViewById(R.id.email);
                                          Name = (EditText) findViewById(R.id.name);
                                          Phone = (EditText) findViewById(R.id.phno);
                                          Homeadd = (EditText) findViewById(R.id.homeadd);
                                          Carno = (EditText) findViewById(R.id.carno);
                                          Pass = (EditText) findViewById(R.id.pw);
                                          Pass2 = (EditText) findViewById(R.id.pw2);

                                          email = Email.getText().toString();
                                          name = Name.getText().toString();
                                          phone = Phone.getText().toString();
                                          homeadd = Homeadd.getText().toString();
                                          carno = Carno.getText().toString();
                                          pass = Pass.getText().toString();
                                          pass2 = Pass2.getText().toString();

                                          if (email.length() == 0 || pass.length() == 0 || name.length() == 0 ||
                                                  phone.length() == 0 || homeadd.length() == 0 || carno.length() == 0 ||
                                                  pass.length() == 0 || pass2.length() == 0) {

                                              Toast.makeText(getApplicationContext(), "Null or empty value found",
                                                      Toast.LENGTH_LONG).show();

                                          } else if (pass.length() <= 8) {

                                              Toast.makeText(getApplicationContext(), "Password less than 8 characters",
                                                      Toast.LENGTH_LONG).show();

                                          }
                                          //to check if email contains @
                                          else if (!email.contains("@")) {
                                              Toast.makeText(getApplicationContext(), "Email does not contain @",
                                                      Toast.LENGTH_LONG).show();
                                          } else if (pass.equals(pass2)) {
                                              RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                              String url = R.string.serverAddress + "/register";
                                              StringRequest myStringRequest = new StringRequest(Request.Method.POST, url,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                                                              Context context = getApplicationContext();
                                                              Toast.makeText(context, R.string.popup, LENGTH_LONG)
                                                                      .show();

                                                          }


                                                      }, new Response.ErrorListener() {
                                                  @Override
                                                  public void onErrorResponse(VolleyError error) {
                                                      Context context = getApplicationContext();
                                                      Toast.makeText(context, R.string.fail, LENGTH_LONG)
                                                              .show();
                                                      Log.d("network response: ", error.toString());
                                                      error.printStackTrace();
                                                  }
                                              }) {
                                                  protected Map<String, String> getParams() {
                                                      Map<String, String> MyData = new HashMap<String, String>();
                                                      MyData.put("name", name);
                                                      MyData.put("email", email);
                                                      MyData.put("phone", phone);
                                                      MyData.put("homeadd", homeadd);
                                                      MyData.put("carno", carno);
                                                      MyData.put("pass", pass);
                                                      return MyData;
                                                  }

                                                  ;
                                              };
                                              Log.d("request:", myStringRequest.toString());
                                              MyRequestQueue.add(myStringRequest);


                                          } else {
                                              Toast.makeText(getApplicationContext(), "Passwords do not match!",
                                                      Toast.LENGTH_LONG).show();
                                          }

                                      }
                                  }

        );
    }
}
