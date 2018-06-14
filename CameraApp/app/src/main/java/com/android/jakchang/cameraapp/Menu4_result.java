package com.android.jakchang.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;


/**
 * Created by insec on 2018-05-18.
 */

public class Menu4_result extends AppCompatActivity {

    Button saveBtn,shareBtn,modelingBtn;
    Intent intent;
    Bitmap bitmap;

    Toast toast;
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + UUID.randomUUID().toString() + ".jpg"); //파일의 저장경로 및 확장자를 설정하는 파일변수 선언
    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + UUID.randomUUID().toString() + ".obj"); //파일의 저장경로 및 확장자를 설정하는 파일변수 선언
    ImageView menu4_result;
    String holderId="";

    private Socket clientSocket;
    private BufferedReader socketIn = null;
    private PrintWriter socketOut;
    private final String ip = "";
    int port = 9009;
    InputStream in;
    DataInputStream dis;

    byte[] data;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu4_result);

        saveBtn = (Button)findViewById(R.id.saveBtn);
        shareBtn = (Button)findViewById(R.id.shareBtn);
        modelingBtn = (Button)findViewById(R.id.modelingBtn);
        menu4_result = (ImageView)findViewById(R.id.menu4_result);

        holderId = getIntent().getStringExtra("holderId");
        bitmap = (Bitmap)DataHolder.popDataHolder(holderId);
        menu4_result.setImageBitmap(bitmap);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bs);
                    byte[] byteArray = bs.toByteArray();
                    save(byteArray,file);
                    toast = Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM,bitmapUri);
                Intent chooser = Intent.createChooser(intent,"공유하기");
                startActivity(chooser);

            }
        });


        modelingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Menu4_result.this , ResultView_3D.class);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);



                try {
                    clientSocket = new Socket(ip, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }



                try {

                    socketOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-16")), true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    byte[] imageBytes = baos.toByteArray();

                    socketOut.printf("3d", imageBytes);

                    OutputStream out = clientSocket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.write(imageBytes, 0, imageBytes.length);
                    dos.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    in = clientSocket.getInputStream();
                    InputStream in2 = clientSocket.getInputStream();
                    dis = new DataInputStream(in);
                    DataInputStream dis2;
                    int readBytes;
                    data = new byte[1024];

                    FileOutputStream out2 = new FileOutputStream(file2);
                    DataOutputStream dos2 = new DataOutputStream(out2);
                    readBytes = 0;
                    int totalReadBytes = 0;
                    dis2 = new DataInputStream(in2);
                    byte[] buf2;

                    while (readBytes <= 0) {
                        System.out.println("파일받기 대기");
                        readBytes = dis2.read(data);

                        if (readBytes <= 0)
                            buf2 = new byte[0];
                        else
                            buf2 = new byte[readBytes];

                        for (int i = 0; i < readBytes; i++) {
                            buf2[i] = data[i];
                        }

                        if (readBytes == -1) {
                            dis2.close();
                            dos2.close();
                            in.close();
                            out2.close();
                            clientSocket.close();
                            throw new IllegalArgumentException("명령어가 제대로 전송 안됨");
                        }

                        if (readBytes >= 0)
                            totalReadBytes += readBytes;

                        System.out.println(readBytes);
                    }

                    while (readBytes > 0) {

                        if (readBytes <= 0)
                            buf2 = new byte[0];
                        else
                            buf2 = new byte[readBytes];

                        for (int i = 0; i < readBytes; i++) {
                            buf2[i] = data[i];
                        }

                        dos2.write(buf2);

                        readBytes = dis2.read(data);

                        if (readBytes >= 0)
                            totalReadBytes += readBytes;

                        System.out.println(readBytes);
                    }

                    System.out.println("total : " + totalReadBytes);

                    dis2.close();
                    dos2.close();

                    in.close();
                    out2.close();
                    clientSocket.close();



                } catch (Exception e) {
                    e.printStackTrace();
                }

                String holderId = DataHolder.putDataHolder(bitmap);
                intent.putExtra("holderId", holderId);
                startActivity(intent);
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

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }//save()



}
