package com.example.keer.okhttpexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;
    private Button getbt;
    private Button postbt;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        getbt = (Button) findViewById(R.id.get);
        postbt = (Button) findViewById(R.id.post);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        getbt.setOnClickListener(this);
        postbt.setOnClickListener(this);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(MainActivity.this, "成功接收到返回值" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onClick(View v) {
        String name = accountEdit.getText().toString();
        String pass = passwordEdit.getText().toString();
        if (v.getId() == R.id.post) {
            JSONObject j = new JSONObject();
            try {
                j.put("name", name);
                j.put("pass", pass);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            postTest(j);
        } else if (v.getId() == R.id.get) {
            getTest(name);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 发送get请求
     *
     * @param name
     */
    private void getTest(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.0.101:8080/android/getTest/" + name)
                        .build();
                Response response = null;
                String responseData = null;
                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("成功接收到返回值" + responseData);
                Message message = new Message();
                message.obj = responseData;
                handler.sendMessage(message);

            }
        }).start();
    }

    /**
     * 发送POST请求
     *
     * @param map
     */
    private void postTest(final JSONObject map) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");//发送json数据的固定写法
                JSONObject jsonObject = new JSONObject();

                RequestBody body = RequestBody.create(JSON, map.toString());//这里是需要是json字符串
                Request request = new Request.Builder()
                        .url("http://192.168.0.101:8080/android/postTest")//用安卓虚拟器跑也不要用127.0.0.1
                        .post(body)
                        .build();
                String res = null;
                try {
                    Response response = client.newCall(request).execute();
                    res = response.body().string();
                    System.out.println(res);
                    Message message = new Message();
                    message.obj = res;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
