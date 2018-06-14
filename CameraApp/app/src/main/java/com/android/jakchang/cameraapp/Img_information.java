package com.android.jakchang.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by insec on 2018-05-13.
 */

public class Img_information extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_imginfo);
        Intent intent = getIntent();
        ImageView imgview = (ImageView)findViewById(R.id.box_image);
        TextView namebox = (TextView)findViewById(R.id.name_textbox);
        TextView datebox = (TextView)findViewById(R.id.date_textbox);
        TextView lngbox = (TextView)findViewById(R.id.lng_textbox);
        TextView latbox = (TextView)findViewById(R.id.lat_textbox);
        TextView sizebox = (TextView)findViewById(R.id.size_textbox);
        TextView makebox = (TextView)findViewById(R.id.make_textbox);
        TextView modelbox = (TextView)findViewById(R.id.model_textbox);
        TextView orienbox = (TextView)findViewById(R.id.orientation_textbox);

        LinearLayout box_name = (LinearLayout)findViewById(R.id.box_name);
        LinearLayout box_date = (LinearLayout)findViewById(R.id.box_date);
        LinearLayout box_lng = (LinearLayout)findViewById(R.id.box_lng);
        LinearLayout box_lat = (LinearLayout)findViewById(R.id.box_lat);
        LinearLayout box_size = (LinearLayout)findViewById(R.id.box_size);
        LinearLayout box_make = (LinearLayout)findViewById(R.id.box_make);
        LinearLayout box_model = (LinearLayout)findViewById(R.id.box_model);
        LinearLayout box_orien = (LinearLayout)findViewById(R.id.box_orientation);

        String val_date = intent.getStringExtra("date");
        String val_lat = intent.getStringExtra("lat");
        String val_lng = intent.getStringExtra("lng");
        String val_width = intent.getStringExtra("width");
        String val_length = intent.getStringExtra("length");
        String val_make = intent.getStringExtra("make");
        String val_model = intent.getStringExtra("model");
        String val_orien = intent.getStringExtra("orientation");



        String tmpHolder = getIntent().getStringExtra("tmpHolder");
        Bitmap bitmap = (Bitmap)DataHolder.popDataHolder(tmpHolder);
        imgview.setImageBitmap(bitmap);

        namebox.setText(intent.getStringExtra("name"));

        if(val_date == "" || val_date == null){
            ((TextView)findViewById(R.id.date_titlebox)).setVisibility(View.GONE);
            datebox.setVisibility(View.GONE);
            box_date.setVisibility(View.GONE);
        }else{
            datebox.setText(val_date);
        }

        if(val_lat == "" || val_lat == null){
            ((TextView)findViewById(R.id.lat_titlebox)).setVisibility(View.GONE);
            latbox.setVisibility(View.GONE);
            box_lat.setVisibility(View.GONE);
        }else{
            latbox.setText(val_lat);
        }

        if(val_lng == "" || val_lng == null){
            ((TextView)findViewById(R.id.lng_titlebox)).setVisibility(View.GONE);
            lngbox.setVisibility(View.GONE);
            box_lng.setVisibility(View.GONE);
        }else{
            lngbox.setText(val_lng);
        }

        if(val_make == "" || val_make == null){
            ((TextView)findViewById(R.id.make_titlebox)).setVisibility(View.GONE);
            makebox.setVisibility(View.GONE);
            box_make.setVisibility(View.GONE);
        }else{
            makebox.setText(val_make);
        }

        if(val_model == "" || val_model == null){
            ((TextView)findViewById(R.id.model_titlebox)).setVisibility(View.GONE);
            modelbox.setVisibility(View.GONE);
            box_model.setVisibility(View.GONE);
        }else{
            modelbox.setText(val_model);
        }

        if(val_orien == "" || val_orien == null){
            ((TextView)findViewById(R.id.orientation_titlebox)).setVisibility(View.GONE);
            orienbox.setVisibility(View.GONE);
            box_orien.setVisibility(View.GONE);
        }else{
            orienbox.setText(val_orien);
        }

        if(val_width == "" || val_width == null){
            ((TextView)findViewById(R.id.size_titlebox)).setVisibility(View.GONE);
            sizebox.setVisibility(View.GONE);
            box_size.setVisibility(View.GONE);
        }else{
            sizebox.setText(val_width+" X "+val_length);
        }
    }
}
