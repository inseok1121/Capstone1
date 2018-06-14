package com.android.jakchang.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * Created by insec on 2018-05-10.
 */

public class Menu3_result_list extends AppCompatActivity {

    Bitmap bitmap;
    TextView textView;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu2_result);

        textView = (TextView)findViewById(R.id.textView2);
        imageView = (ImageView)findViewById(R.id.imageView);

        String pos = getIntent().getStringExtra("pos");
        String holderId = getIntent().getStringExtra("holderId");

        String[] str = pos.split("fake");
        bitmap = (Bitmap)DataHolder.popDataHolder(holderId);
        imageView.setImageBitmap(bitmap);
        textView.setText(str[1]);


    }

}
