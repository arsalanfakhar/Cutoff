package com.example.techtik.cuttoff.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.techtik.cuttoff.R;

public class LoginActivity extends AppCompatActivity {

    ImageView loginButton ;
    RelativeLayout buttonlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonlayout =(RelativeLayout) findViewById(R.id.verifyButton);
     buttonlayout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
             startActivity(mainIntent);
         }
     });
    }
}
