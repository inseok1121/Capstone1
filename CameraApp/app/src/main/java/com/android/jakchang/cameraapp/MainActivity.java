package com.android.jakchang.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

    private Animation a;
    LinearLayout title, copy;
    LinearLayout imageBtn1,imageBtn2,imageBtn3,imageBtn4;
    ImageView imgBtn1,imgBtn2,imgBtn3,imgBtn4;
    ImageView menu1, menu2, menu3,menu4;
    FloatingActionButton mfab;

    public static int CAMERA_MODE=0;
    public int idx_tap = 0;
    final Activity activity = MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (LinearLayout)findViewById(R.id.main_title);

        imageBtn1 = (LinearLayout)findViewById(R.id.button_1);
        imageBtn2 = (LinearLayout)findViewById(R.id.button_2);
        imageBtn3 = (LinearLayout)findViewById(R.id.button_3);
        imageBtn4 = (LinearLayout)findViewById(R.id.button_4);
        mfab = (FloatingActionButton)findViewById(R.id.fab);

        ViewCompat.setElevation(title, 8);
        ViewCompat.setElevation(imageBtn1, 6);
        ViewCompat.setElevation(imageBtn2, 5);
        ViewCompat.setElevation(imageBtn3, 4);
        ViewCompat.setElevation(imageBtn4, 3);

        imgBtn1 = (ImageView)findViewById(R.id.imgBtn1);
        imgBtn2 = (ImageView)findViewById(R.id.imgBtn2);
        imgBtn3 = (ImageView)findViewById(R.id.imgBtn3);
        imgBtn4 = (ImageView)findViewById(R.id.imgBtn4);




        imgBtn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),TakeAPhoto.class);
                startActivity(intent);
            }
        });

        imgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Menu2_main.class);
                startActivity(intent);
            }
        });
        imgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Menu3_main.class);
                startActivity(intent);
            }
        });
        imgBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Menu4_main.class);
                startActivity(intent);
            }
        });
    }


}
