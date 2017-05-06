package cn.f_ms.globalhttpparams_http;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Global Http Params Intercepter For OkHttp
 *
 * @author f-ms
 * @time 2017/5/6
 */

public class GlobalHttpParamsIntercepter implements Interceptor {

    public interface OnNeedHttpParams {
        /**
         * get global http params
         * @return global http params, return can be null
         */
        HttpParams getParams();
    }

    private OnNeedHttpParams mOnNeedHttpParams;

    public GlobalHttpParamsIntercepter(OnNeedHttpParams onNeedHttpParams) {

        if (onNeedHttpParams == null) { throw new NullPointerException(); }

        mOnNeedHttpParams = onNeedHttpParams;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        HttpParams appendParams = mOnNeedHttpParams.getParams();

        Request sourceRequest = chain.request();

        if (appendParams == null) {
            return chain.proceed(sourceRequest);
        }

        Request.Builder targetRequestBuilder = sourceRequest.newBuilder();

        /*
        HttpHeaders
         */
        Headers targetHeaders = sourceRequest.headers();
        Headers appendHttpHeaders = appendParams.httpHeaders();
        if (appendHttpHeaders != null) {

            Headers.Builder sourceHeadersBuilder = sourceRequest.headers().newBuilder();

            for (int x = 0; x < appendHttpHeaders.size(); x++) {
                String name = appendHttpHeaders.name(x);
                String value = appendHttpHeaders.value(x);

                sourceHeadersBuilder.add(name, value);
            }

            targetHeaders = sourceHeadersBuilder.build();
        }

        /*
        HttpUrlQuery
         */
        HttpUrl targetUrl = sourceRequest.url();
        UrlQuerys appendUrlQuerys = appendParams.urlQuerys();
        if (appendUrlQuerys != null) {

            appendUrlQuerys.urlQuerys().build().querySize()

        }

        return null;
    }
}
