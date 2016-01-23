package com.lxldev.landroid;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lxl.landroid.net.core.HttpAccess;
import com.lxl.landroid.net.core.HttpRequest;
import com.lxl.landroid.net.core.utils.AccessParams;
import com.lxl.landroid.net.core.utils.Method;
import com.lxl.landroid.net.impl.HttpStringRequest;
import com.lxl.landroid.net.impl.HttpUrlAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.textview);
//
//
//        HttpStringRequest request=HttpStringRequest.getInstance(this);
//        request.setCache(true);
//        request.sendReq("http://www.baidu.com", null, new HttpRequest.onResponseCallback() {
//            @Override
//            public void onResponse(byte[] data) {
//                textView.setText(new String(data));
//            }
//        }, new HttpRequest.onErrorCallback() {
//            @Override
//            public void onError(Throwable errInfo,byte[]cache) {
//                textView.setText("加载失败\n");
//                if(cache!=null)
//                    textView.append(new String(cache));
//            }
//        });

        File file=new File(Environment.getExternalStorageDirectory(),"sss");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            InputStream inputStream=getResources().openRawResource(R.raw.ic_launcher);
            int len=0;
            byte[]buffer=new byte[4096];
            while(-1!=(len=inputStream.read(buffer))){
                fileOutputStream.write(buffer,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpStringRequest request=HttpStringRequest.getInstance(this);
        AccessParams accessParams=new AccessParams();
        Map<String,File> fileMap=new HashMap<>();
        fileMap.put("headimg",file);
        accessParams.setFiles(fileMap);
        Map<String ,String> paramMap=new HashMap<>();
        paramMap.put("params","{\"method\":\"alterUserHeadimg\",\"user_id\":1}");
        accessParams.setParams(paramMap);
        String url="http://192.168.1.99/xidai/android.php";
        request.sendReq(Method.POST, url, accessParams, new HttpRequest.onResponseCallback() {
            @Override
            public void onResponse(byte[] data) {
                Log.i("--------------->>","------------_>>>"+new String(data));
            }
        }, new HttpRequest.onErrorCallback() {
            @Override
            public void onError(Throwable errInfo, byte[] cache) {
                errInfo.printStackTrace();
            }
        });

    }
}
