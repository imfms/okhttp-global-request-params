package cn.f_ms.globalhttpparams_http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import cn.f_ms.network.okhttp.intercepter.globalrequestparams.GlobalHttpParamsIntercepter;
import cn.f_ms.network.okhttp.intercepter.globalrequestparams.HttpParams;
import cn.f_ms.network.okhttp.intercepter.globalrequestparams.UrlQuerys;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOkHttp();

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
    }

    private void initOkHttp() {

        GlobalHttpParamsIntercepter globalHttpParamsIntercepter = new GlobalHttpParamsIntercepter(new GlobalHttpParamsIntercepter.OnNeedHttpParams() {
            @Override
            public HttpParams getParams(Request request) {
                return new HttpParams.Builder()
                        .setUrlQuerys(
                                new UrlQuerys.Builder()
                                        .addQueryParameter("local_time", System.currentTimeMillis() + "")
                                        .build()
                        )
                        .setRequestHeaders(
                                new Headers.Builder()
                                        .add("local_time", System.currentTimeMillis() + "")
                                        .build()
                        )
                        .addRequestBody(
                                new FormBody.Builder()
                                        .add("local_time", System.currentTimeMillis() + "")
                                        .build()
                        )
                        .addRequestBody(
                                new MultipartBody.Builder()
                                        .addFormDataPart("local_time", System.currentTimeMillis() + "")
                                        .build()
                        )
                        .build();
            }
        });

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(globalHttpParamsIntercepter)
                .build();
    }

    private void request() {

        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(
                        new MultipartBody.Builder()
                                .addFormDataPart("name", "value")
                                .build()
                )
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
