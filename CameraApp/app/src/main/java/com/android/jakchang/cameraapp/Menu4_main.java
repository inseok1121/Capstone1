package com.android.jakchang.cameraapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by insec on 2018-05-13.
 */

public class Menu4_main extends AppCompatActivity{

    private Integer[] mThumbIds = {R.drawable.char_0, R.drawable.char_1,R.drawable.char_0, R.drawable.char_1};
    ImageView area_character;
    ImageView area_user;
    Bitmap bitmap;
    int flag;

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
        setContentView(R.layout.menu4_main);
        area_character = (ImageView)findViewById(R.id.Area_character);
        area_user = (ImageView)findViewById(R.id.Area_user);
        Button result = (Button)findViewById(R.id.button_complete);

        ImageView char0 = (ImageView)findViewById(R.id.list_char0);
        char0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_CharImage((ImageView)findViewById(R.id.list_char0));
                flag=0;
            }
        });
        ImageView char1 = (ImageView)findViewById(R.id.list_char1);
        char1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_CharImage((ImageView)findViewById(R.id.list_char1));
                flag=1;
            }
        });


        area_character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToView((LinearLayout)findViewById(R.id.list_char), (ScrollView)findViewById(R.id.scrollview), 0);
            }
        });
        area_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 150);
            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (area_user == null ) {
                    Toast.makeText(getApplicationContext(), "사진을 넣어주세요", Toast.LENGTH_SHORT).show();


                }else if(area_user.getDrawable()!=null && area_character.getDrawable() == null){
                    Intent intent = new Intent(getBaseContext(), Menu4_result.class);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    String holderId = DataHolder.putDataHolder(bitmap);
                    intent.putExtra("holderId", holderId);

                    startActivity(intent);

                }else if(area_user.getDrawable()!=null && area_character.getDrawable() !=null){
                    Intent intent = new Intent(getBaseContext(), Menu4_result.class);
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

                        if (flag == 0) {
                            socketOut.printf("joker", imageBytes);
                        } else if (flag == 1) {
                            socketOut.printf("blonde", imageBytes);
                        }


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

                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
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
                        data = out2.toByteArray();
                        System.out.println("total : " + totalReadBytes);

                        dis2.close();
                        dos2.close();
                        ;
                        in.close();
                        out2.close();
                        clientSocket.close();
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        String holderId = DataHolder.putDataHolder(bitmap);
                        intent.putExtra("holderId", holderId);

                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 150){
            if(resultCode == RESULT_OK){
                try {
                    Uri uri = data.getData();
                    InputStream is = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(is);
                    bitmap = Bitmap.createScaledBitmap(bitmap,area_user.getWidth(),area_user.getHeight(),true);

                    area_user.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void set_CharImage(ImageView view){
        ImageView target = (ImageView)findViewById(R.id.Area_character);
        BitmapDrawable d = (BitmapDrawable)(view).getDrawable();
        Bitmap b = d.getBitmap();
        target.setImageBitmap(b);
    }
    public static void scrollToView(View view, final ScrollView scrollView, int count){
        if (view != null && view != scrollView){
            count += view.getTop();
            scrollToView((View)view.getParent(), scrollView, count);
        }else if(scrollView != null){
            final int finalCount = count;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, finalCount);
                }
            },200);
        }
    }




}
