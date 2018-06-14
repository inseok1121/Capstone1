package com.android.jakchang.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ResultView extends AppCompatActivity {

    Button saveBtn;
    ImageButton backBtn;
    ImageView resultView;
    Intent intent;
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + UUID.randomUUID().toString() + ".jpg"); //파일의 저장경로 및 확장자를 설정하는 파일변수 선언
    byte[] bytes;
    Bitmap bitmap;
    Image image;
    FileOutputStream fos = null;
    ByteBuffer byteBuffer;
    Toast toast;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.result_view);

        saveBtn = (Button)findViewById(R.id.saveBtn);
        backBtn = (ImageButton)findViewById(R.id.backBtn);
        resultView = (ImageView)findViewById(R.id.resultView);

        String holderId = getIntent().getStringExtra("holderId");
        bitmap = (Bitmap)DataHolder.popDataHolder(holderId);

        resultView.setImageBitmap(bitmap);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bs);
                    byte[] byteArray = bs.toByteArray();


                    save(byteArray,file);
                    toast = Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();

                }catch(Exception e){

                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void save(byte[] bytes,File nFile) throws IOException {
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(nFile);
            outputStream.write(bytes);
        }finally {
            if(outputStream !=null) outputStream.close();

        }

        MediaScannerConnection.scanFile(ResultView.this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }//save()




}
