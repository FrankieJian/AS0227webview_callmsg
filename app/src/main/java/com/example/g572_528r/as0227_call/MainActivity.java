package com.example.g572_528r.as0227_call;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CALL_PERMISSION = 0;
    private static final int REQUEST_CODE_SEND_MSG = 1;
    private Button btn1;
    private Button btn2;
    private WebView mWebView;
    private boolean isCanCall = false;
    private boolean isCanSendMsg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    private void findViews() {
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(MainActivity.this, "android");
        mWebView.loadUrl("file:///android_asset/web.html");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:javacalljs()");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:javacalljswith(" + "'Hello LFJ'" + ")");
            }
        });
    }

    private boolean isHasCallPermission() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
    }

    private boolean isHasSendMsgPermission() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
    }

    private void applyCallPermission() {
        String permissions[] = {Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE_CALL_PERMISSION);
    }
    
    @JavascriptInterface
    public void startFunction(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "js调用JAVA代码", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setMessage(text).show();
            }
        });
    }

    @JavascriptInterface
    public void call(String num){
        if(!isHasCallPermission()){
            applyCallPermission();
        }
        if(num.isEmpty()){
            Toast.makeText(this, "电话号码为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + num));
        startActivity(intent);
    }

    @JavascriptInterface
    public void sendMsg(String num, String msg){
        if(!isHasSendMsgPermission()){
            applyCallPermission();
        }
        if(num.isEmpty()){
            Toast.makeText(this, "电话号码和短信内容禁止为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> list = smsManager.divideMessage(msg);
        for (String text:list) {
            smsManager.sendTextMessage(num,null,text,null,null);
        }
        Log.i("msg","sendMsg:successful!!");
        Toast.makeText(this, "successful!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_CALL_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请打电话权限成功！", Toast.LENGTH_SHORT).show();
                    isCanCall = true;
                }else{
                    Toast.makeText(this, "申请打电话权限失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_SEND_MSG:
                if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请发短信权限成功！", Toast.LENGTH_SHORT).show();
                    isCanSendMsg = true;
                }else{
                    Toast.makeText(this, "申请发短信权限失败！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
