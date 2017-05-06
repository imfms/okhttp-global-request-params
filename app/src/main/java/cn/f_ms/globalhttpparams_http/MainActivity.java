package cn.f_ms.globalhttpparams_http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request request = chain.request();

                        RequestBody body = request.body();
                        if (body instanceof FormBody) {

//                            body.

                        }

                        new Request.Builder()
                                .


                        return null;
                    }
                })
                .build();
    }

    private void request() {

//        new HttpUrl.Builder()


        mOkHttpClient.newCall();

    }
}
