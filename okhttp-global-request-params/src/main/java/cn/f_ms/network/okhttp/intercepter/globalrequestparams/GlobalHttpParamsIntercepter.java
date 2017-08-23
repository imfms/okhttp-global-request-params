package cn.f_ms.network.okhttp.intercepter.globalrequestparams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

/**
 * Global Http Params Intercepter For OkHttp
 *
 * @author f-ms
 * @time 2017/5/6
 */
public class GlobalHttpParamsIntercepter implements Interceptor {

    public static final String TAG = GlobalHttpParamsIntercepter.class.getSimpleName();

    /**
     * When need params
     */
    public interface OnNeedHttpParams {
        /**
         * get global http params
         *
         * @return global http params, return can be null
         */
        HttpParams getParams(Request request);
    }

    private List<RequestBodyAppender> mRequestBodyAppender;
    private OnNeedHttpParams mOnNeedHttpParams;

    public GlobalHttpParamsIntercepter(OnNeedHttpParams onNeedHttpParams) {

        if (onNeedHttpParams == null) {
            throw new NullPointerException();
        }

        mOnNeedHttpParams = onNeedHttpParams;

        addDefaultRequestBodyAppender();
    }

    /**
     * add custom requestbodyappender,
     * default add FormBodyAppender & MulitPartBodyAppender,
     */
    public GlobalHttpParamsIntercepter addRequestBodyAppender(RequestBodyAppender appender) {

        if (appender == null) {
            throw new NullPointerException();
        }

        if (mRequestBodyAppender == null) {
            mRequestBodyAppender = new ArrayList<>(4);
        }

        mRequestBodyAppender.add(appender);

        return this;
    }

    /**
     * clear all of request body appender,
     * include default appender(FormBodyAppender, MulitPartBodyAppender)
     */
    public GlobalHttpParamsIntercepter clearRequestBodyAppender() {
        if (mRequestBodyAppender != null) {
            mRequestBodyAppender.clear();
        }

        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request sourceRequest = chain.request();

        HttpParams appendParams = mOnNeedHttpParams.getParams(sourceRequest);

        if (appendParams == null) {
            return chain.proceed(sourceRequest);
        }

        /*
        HttpHeaders
         */
        Headers targetHeaders = concatHeader(
                sourceRequest.headers(),
                appendParams.requestHeaders()
        );

        /*
        HttpUrlQuery
         */
        HttpUrl targetHttpUrl = concatUrlQuery(
                sourceRequest.url(),
                appendParams.urlQuerys() == null
                        ? null
                        : appendParams.urlQuerys().url()
        );

        /*
        RequestBody
         */
        RequestBody targetRequestBody = concatRequestBody(
                sourceRequest.method(),
                sourceRequest.body(),
                appendParams.requestBody(),
                mRequestBodyAppender
        );

        /*
        Generate Target Request
         */
        Request targetRequest = sourceRequest.newBuilder()
                .headers(targetHeaders)
                .url(targetHttpUrl)
                .method(sourceRequest.method(), targetRequestBody)
                .build();

        return chain.proceed(targetRequest);
    }

    private Headers concatHeader(Headers firstHeaders, Headers secondHeaders) {
        if (secondHeaders == null) {
            return firstHeaders;
        }

        Headers.Builder sourceHeadersBuilder = firstHeaders.newBuilder();

        for (int x = 0; x < secondHeaders.size(); x++) {
            String name = secondHeaders.name(x);
            String value = secondHeaders.value(x);

            sourceHeadersBuilder.add(name, value);
        }

        return sourceHeadersBuilder.build();
    }

    private HttpUrl concatUrlQuery(HttpUrl firstUrl, HttpUrl secondUrl) {

        if (secondUrl == null) {
            return firstUrl;
        }

        HttpUrl.Builder sourceHttpUrlBuilder = firstUrl.newBuilder();

        for (int x = 0; x < secondUrl.querySize(); x++) {

            String name = secondUrl.queryParameterName(x);
            String value = secondUrl.queryParameterValue(x);

            sourceHttpUrlBuilder.addQueryParameter(name, value);
        }

        return sourceHttpUrlBuilder.build();
    }

    private RequestBody concatRequestBody(String method, RequestBody firstRequestBody, ArrayList<RequestBody> secondRequestBodyList, List<RequestBodyAppender> appenders) {

        if (secondRequestBodyList == null
                || secondRequestBodyList.isEmpty()) {
            return firstRequestBody;
        }

        if (firstRequestBody == null) {

            return HttpMethod.requiresRequestBody(method)
                    ? secondRequestBodyList.get(0)
                    : null;
        }

        if (appenders == null
                || appenders.isEmpty()) {

            System.err.println(">>>> " + TAG + " ERROR:");
            System.err.println("can't find RequestBodyAppender, cancel append global http params, please check the code");
            System.err.println("<<<<");
            return firstRequestBody;
        }

        for (RequestBodyAppender appender : appenders) {

            for (RequestBody appenderRequestBody : secondRequestBodyList) {

                if (appender.isAccept(firstRequestBody, appenderRequestBody)) {
                    return appender.append(firstRequestBody, appenderRequestBody);
                }
            }
        }

        System.err.println(">>>> " + TAG + " ERROR:");
        System.err.println(
                "can't find proper RequestBodyAppender for RequestBody '"
                        + firstRequestBody.getClass().getSimpleName()
                        + "', cancel append global http params, please check the code"
        );
        System.err.println("<<<<");

        return firstRequestBody;
    }

    private void addDefaultRequestBodyAppender() {
        addRequestBodyAppender(
                FormBodyAppender.create()
        );
        addRequestBodyAppender(
                MulitPartBodyAppender.create()
        );
    }
}
