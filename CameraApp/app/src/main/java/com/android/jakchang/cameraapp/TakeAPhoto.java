package com.android.jakchang.cameraapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.icu.util.Output;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.floor;

/**
 * Created by ckdrb on 2018-03-23.
 */

public class TakeAPhoto extends AppCompatActivity {


    private ImageView btnCapture,changeBtn;
    private ImageView char0,char1;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();


    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;


    public CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader reader;

    //파일저장
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroumdHandler;
    private HandlerThread mBackgroundThread;
    private CameraCaptureSession.CaptureCallback captureListener;
    private CameraManager.AvailabilityCallback availabilityCallback;
    TextureView.SurfaceTextureListener textureListener;
    CameraManager manager;
    CameraCharacteristics characteristics;

    int width,height;
    int rotation;
    CaptureRequest.Builder captureBuilder;

    Intent intent;
    private Toast toast;;
    Image image = null;
    byte[] bytes;
    Bitmap bitmap;
    private Socket clientSocket;
    private BufferedReader socketIn = null;
    private PrintWriter socketOut;
    private final String ip = "";
    int port = 9009;
    InputStream in;
    DataInputStream dis;

    byte[] data;

    int charFlag = -1;

    ConstraintLayout textureLinear;
    int disWidth;
    int disHeight;


    //private List<OutputConfiguration> outputConfiguration;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreView();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice =null;
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.takingphoto);



        btnCapture = (ImageView)findViewById(R.id.btnCapture);
        changeBtn = (ImageView)findViewById(R.id.changeBtn);
        textureLinear = (ConstraintLayout)findViewById(R.id.textureLinear);
        textureView  = (TextureView)findViewById(R.id.textureView);
        assert textureView != null;

        char0 =(ImageView)findViewById(R.id.list_char0);
        char1 =(ImageView)findViewById(R.id.list_char1);

        textureView.setSurfaceTextureListener(getTextureListenerInstance());

        /*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        disWidth = displayMetrics.widthPixels;
        disHeight = displayMetrics.heightPixels;
        System.out.println("disWidth"+disWidth);
        System.out.println("disHeight"+disHeight);

        textureView.setLayoutParams(new ConstraintLayout.LayoutParams(1440 , 1440));

        textureView.getBitmap(1440,1440);
        */
        char0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char0.setVisibility(View.INVISIBLE);
                char1.setVisibility(View.VISIBLE);
                charFlag = 0;
            }
        });

        char1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char0.setVisibility(View.VISIBLE);
                char1.setVisibility(View.INVISIBLE);
                charFlag = 1;
            }
        });


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraCharacterlistics_2(cameraManager_1());
                cameraCaptureSession_3();
                intent = new Intent(TakeAPhoto.this, ResultView.class);
                bitmap = textureView.getBitmap(1440,1440);

                if(charFlag!=-1) {


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

                        if(charFlag==0){socketOut.printf("joker", imageBytes);}
                        if(charFlag==1){socketOut.printf("blonde", imageBytes);}

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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                String holderId = DataHolder.putDataHolder(bitmap);
                intent.putExtra("holderId", holderId);
                startActivity(intent);
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });


    }//onCreate()



    private CameraManager cameraManager_1(){
        if(manager==null){
            manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);    //시스템으로부터 카메라 서비스를 받는 카메라 매니저 선언
        }

        return manager;
    }//cameraManager_1

    private void cameraCharacterlistics_2(CameraManager cm){

        try {
            characteristics = cm.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes=null;                                                                           //찍힌 사진의 크기 및 파일확장자를 설정하는 변수 선언
            if(characteristics !=null)                                                                       //특징들이 잘 대입되었을때
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)    //
                        .getOutputSizes(ImageFormat.JPEG);

            width = 640;                                                                                  //디폴트값으로 넓이,높이 설정
            height = 480;
            if(jpegSizes !=null && jpegSizes.length>0){                                                      //카메라특징객체를 통해 실제 측정된 이미지의 높이와 넓이 값을 저장
                width = 1080;
                height = 1440;

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }//cameraCharacterlistics_2



    public void cameraCaptureSession_3(){
        try {

            reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            final List<Surface> outputSurface = new ArrayList<>(2);//찍힌 이미지의 정보를 읽을 객체 선언

            outputSurface.add(reader.getSurface());                                                          //찍힌 이미지의 surface를 추가
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));                                //

            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);           //카메라캡쳐요청정보를 갖고있는 캡쳐빌더 선언
            captureBuilder.addTarget(reader.getSurface());                                                   //캡쳐빌드에서 읽힌 이미지를 타겟으로 설정
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);            //캡쳐빌드에

            if(cameraId==CAMERA_BACK) {
                rotation= getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));               //찍힌 이미지에 회전값을 주어 회전시키도록 설정

            }
            else if(cameraId==CAMERA_FRONT) {
                rotation= getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);               //찍힌 이미지에 회전값을 주어 회전시키도록 설정

            }


            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {  //새로운 이미지가 찍혔을때 발생하는 리스너
                @Override
                public void onImageAvailable(ImageReader imageReader) {

                    try {

                        image = reader.acquireLatestImage();                                                 //이미지 객체에 최근에 찍힌 이미지객체를 대입
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();                                 //읽혀진 이미지의 버퍼를 선언
                        bytes = new byte[buffer.capacity()];                                                 //버퍼의 가용 크기만큼 바이트 선언
                        buffer.get(bytes);                                                                    //버퍼에 가용크기만큼의 용량을 선언

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        {
                            if (image != null)
                                image.close();
                        }
                    }
                }//onImageAvailable()
            };//ImageReader.OnImageAvailableListener()
            reader.setOnImageAvailableListener(readerListener, mBackgroumdHandler);                         //이미지 리더객체에 리스너객체 set

            captureListener = new CameraCaptureSession.CaptureCallback() {  //캡쳐가 되었을때 실행되는 리스너
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {     //캡쳐되었을때 발생하는 리스너선언
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreView();                                    //이부분을 지우면 캡쳐됬을때 카메라는 켜져있지만 화면은 정지되있음

                }
            };
            /*
            cameraDevice.createCaptureSessionByOutputConfigurations(outputConfiguration, new CameraCaptureSession.StateCallback()  {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        Surface surface = new Surface(textureView.getSurfaceTexture());
                        outputSurface.add(1024,surface);
                        outputSurface.add(1024,surface);
                        outputConfiguration.

                        //cameraCaptureSession.finalizeOutputConfigurations(outputConfiguration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mBackgroumdHandler);
            */
            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {      //2개의 세션과 콜백함수 생성
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroumdHandler);   //요청정보를 빌드하고 리스너객체를 적용
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mBackgroumdHandler);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//cameraCaptureSession_3


    private void createCameraPreView() {                                                            //textureView에 동작하는 함수
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();                               //현재textureView의 뷰를 대입
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());   //
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice ==null) return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    toast = Toast.makeText(TakeAPhoto.this,"Changed",Toast.LENGTH_LONG);
                    toast.show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {

        if(cameraDevice==null)
            toast = Toast.makeText(TakeAPhoto.this,"Error",Toast.LENGTH_SHORT);

        toast.show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroumdHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(CameraManager cm) {

        try
        {
            CameraCharacteristics characteristics = cm.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //API버전이 23보다 높게 설정됬을때
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }   //카메라 권한을 얻지 못했을때

            cm.openCamera(cameraId,stateCallback,null);
            toast = Toast.makeText(this,"카메라가 실행되었습니다",Toast.LENGTH_LONG);
            toast.show();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CAMERA_PERMISSION){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                toast = Toast.makeText(this,"카메라 사용권한을 받으세요",Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }

        }
    }   //카메라 권한 요청결과에 대한 반응 메소드


    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable()) openCamera(cameraManager_1());
        else
            textureView.setSurfaceTextureListener(getTextureListenerInstance());
    }

    @Override
    protected void onPause() {

        stopBackgroundThread();
        super.onPause();
        //cameraCaptureSessions.close();
        //cameraDevice.close();
        cameraDevice = null;
        cameraCaptureSessions = null;
        //reader.close();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void stopBackgroundThread() {
        if(mBackgroundThread!=null) {
            mBackgroundThread.quitSafely();

            try{
                mBackgroundThread.join();
                mBackgroundThread=null;
                mBackgroumdHandler=null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }   //카메라 백그라운드 스레드를 종료하는 메소드

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera BackGround");
        mBackgroundThread.start();
        mBackgroumdHandler = new Handler(mBackgroundThread.getLooper());
    }

    private TextureView.SurfaceTextureListener getTextureListenerInstance(){
        if(textureListener==null) {
            textureListener = new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                    openCamera(cameraManager_1());
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            };
        }
        return textureListener;
    }

    private void closeCamera() {

        if (null != cameraCaptureSessions) {
            cameraCaptureSessions.close();
            cameraDevice.close();
            cameraDevice = null;
            cameraCaptureSessions = null;
        }

        if (null != reader) {
            reader.close();
            reader = null;
        }
    }

    private void switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();
        }

        else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
        }
    }

    private void reopenCamera() {
        if (textureView.isAvailable()) {
            openCamera(cameraManager_1());
        } else {
            textureView.setSurfaceTextureListener(getTextureListenerInstance());
        }
    }







}

