package com.android.jakchang.cameraapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import retrofit2.http.Url;

public class ResultView_3D extends AppCompatActivity {

    WebView view;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3d);

        view = (WebView)findViewById(R.id.web);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setLightTouchEnabled(true);
        view.getSettings().setSaveFormData(true);
        view.getSettings().setSaveFormData(true);

        view.loadUrl(url);
        view.setWebViewClient(new MyWebViewClient());


    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }
    }

}