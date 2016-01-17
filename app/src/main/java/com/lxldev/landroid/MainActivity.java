package com.lxldev.landroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lxl.landroid.net.core.HttpAccess;
import com.lxl.landroid.net.core.HttpRequest;
import com.lxl.landroid.net.core.utils.AccessParams;
import com.lxl.landroid.net.impl.HttpStringRequest;
import com.lxl.landroid.net.impl.HttpUrlAccess;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.textview);


        HttpStringRequest request=HttpStringRequest.getInstance(this);
        request.setCache(true);
        request.sendReq("http://www.baidu.com", null, new HttpRequest.onResponseCallback() {
            @Override
            public void onResponse(byte[] data) {
                textView.setText(new String(data));
            }
        }, new HttpRequest.onErrorCallback() {
            @Override
            public void onError(Throwable errInfo,byte[]cache) {
                textView.setText("加载失败\n");
                if(cache!=null)
                    textView.append(new String(cache));
            }
        });

    }
}
