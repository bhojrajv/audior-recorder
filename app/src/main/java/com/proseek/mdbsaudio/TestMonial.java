package com.proseek.mdbsaudio;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TestMonial extends AppCompatActivity {
WebView webView;
ProgressDialog progressDialog;
String url2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_monial);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait util open the webView....");
        progressDialog.setCanceledOnTouchOutside(false);
        url2=getIntent().getExtras().get("Urltest").toString();
        webView=findViewById(R.id.web);
        if(checknewtworkConnection()==true)
        {
            Webview(url2,progressDialog);
        }
        else {
            Toast.makeText(this, "please check your network connection", Toast.LENGTH_SHORT).show();
        }



    }

    private void Webview(String Url,ProgressDialog dialog)
    {
         webView.setWebViewClient(new MyView(dialog));
        WebSettings webSettings=webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.getDisplayZoomControls();
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(Url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private boolean checknewtworkConnection()
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if(connectivityManager.getNetworkInfo(0).getState()== NetworkInfo.State.CONNECTED
          ||connectivityManager.getNetworkInfo(0).getState()==NetworkInfo.State.CONNECTING
          ||connectivityManager.getNetworkInfo(1).getState()==NetworkInfo.State.CONNECTED
          ||connectivityManager.getNetworkInfo(1).getState()==NetworkInfo.State.CONNECTING)
          {
              Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
              return true;
          }
          else if(connectivityManager.getNetworkInfo(0).getState()==NetworkInfo.State.DISCONNECTED
          || connectivityManager.getNetworkInfo(1).getState()==NetworkInfo.State.DISCONNECTING) {
              Toast.makeText(this, "DisConnected", Toast.LENGTH_SHORT).show();
              return false;
          }
          return false;

    }

}
class MyView extends WebViewClient{
    ProgressDialog progressDialog;
    public MyView(ProgressDialog dialog)
    {
        this.progressDialog=dialog;
        progressDialog.show();
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
       // Toast.makeText(MyView.this, "", Toast.LENGTH_SHORT).show();
    }
}
