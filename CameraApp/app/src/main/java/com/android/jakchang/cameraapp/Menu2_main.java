package com.android.jakchang.cameraapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by insec on 2018-05-09.
 */

public class Menu2_main extends AppCompatActivity {
    Intent intent;
    ImageView imageView,add;

    FloatingActionButton fab;
    Bitmap image;

    private Socket clientSocket;
    private BufferedReader socketIn = null;
    private PrintWriter socketOut;
    private final String ip = "";
    int port = 9009;
    InputStream in;
    DataInputStream dis;


    byte[] data;
    String pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu2_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        add = (ImageView)findViewById(R.id.add);


        fab = (FloatingActionButton)findViewById(R.id.fab);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"select File"),0);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                try {
                    clientSocket = new Socket(ip, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //myThread.start();


                try {


                    socketOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-16")) ,true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image = Bitmap.createScaledBitmap(image, 256, 256, false);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    byte[] imageBytes = baos.toByteArray();

                    socketOut.printf("detect1",imageBytes);

                    OutputStream out = clientSocket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.write(imageBytes,0,imageBytes.length);
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

                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    DataOutputStream dos2= new DataOutputStream(out2);
                    readBytes = 0;
                    int totalReadBytes=0;
                    dis2 = new DataInputStream(in2);
                    byte[] buf2;

                    while(readBytes <= 0) {
                        System.out.println("파일받기 대기");
                        readBytes=dis2.read(data);

                        if(readBytes<=0)
                            buf2=new byte[0];
                        else
                            buf2 = new byte[readBytes];

                        for(int i=0; i<readBytes;i++) {
                            buf2[i]=data[i];
                        }
                        pos = new String(buf2,"UTF-8");

                        if(readBytes==-1) {
                            dis2.close();
                            dos2.close();
                            in.close();
                            out2.close();
                            clientSocket.close();
                            throw new IllegalArgumentException("명령어가 제대로 전송 안됨");
                        }

                        if(readBytes>=0)
                            totalReadBytes += readBytes;

                        System.out.println(readBytes);
                    }


                    while(readBytes >0) {

                        if(readBytes<=0)
                            buf2=new byte[0];
                        else
                            buf2 = new byte[readBytes];

                        for(int i=0; i<readBytes;i++) {
                            buf2[i]=data[i];
                        }

                        dos2.write(buf2);

                        readBytes=dis2.read(data);

                        if(readBytes>=0)
                            totalReadBytes += readBytes;

                        System.out.println(readBytes);
                    }
                    data = out2.toByteArray();
                    System.out.println("total : "+totalReadBytes);

                    dis2.close();
                    dos2.close();;
                    in.close();
                    out2.close();
                    clientSocket.close();



                } catch (Exception e) {
                    e.printStackTrace();
                }



                intent = new Intent(getApplicationContext(), Menu2_result_list.class);
                String holderId = DataHolder.putDataHolder(image);
                intent.putExtra("holderId",holderId);
                intent.putExtra("pos",pos);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            Uri selectImageUri = data.getData();
            ClipData clipData = data.getClipData();
            try {
                InputStream is = getContentResolver().openInputStream(selectImageUri);
                image = BitmapFactory.decodeStream(is);
                image = Bitmap.createScaledBitmap(image,imageView.getWidth(),imageView.getHeight(),true);
                imageView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }



}

