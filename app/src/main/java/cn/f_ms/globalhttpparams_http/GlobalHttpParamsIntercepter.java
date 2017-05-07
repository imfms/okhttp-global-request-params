package cn.f_ms.globalhttpparams_http;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
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

    public interface OnNeedHttpParams {
        /**
         * get global http params
         *
         * @return global http params, return can be null
         */
        HttpParams getParams(Request request);
    }

    private OnNeedHttpParams mOnNeedHttpParams;

    public GlobalHttpParamsIntercepter(OnNeedHttpParams onNeedHttpParams) {

        if (onNeedHttpParams == null) {
            throw new NullPointerException();
        }

        mOnNeedHttpParams = onNeedHttpParams;
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
                appendParams.httpHeaders()
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
        RequestBody targetRequestBody = null;
        // when request method can take RequestBody
        if (HttpMethod.requiresRequestBody(sourceRequest.method())) {
            targetRequestBody = concatRequestBody(
                    sourceRequest.body(),
                    appendParams.formBody(),
                    appendParams.multipartBody()
            );
        }

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

    private FormBody concatFormBody(FormBody firstFormBody, FormBody secondFormBody) {

        FormBody isNull =
                firstFormBody != null ?
                        firstFormBody : secondFormBody != null ?
                        secondFormBody : null;

        if (firstFormBody == null
                || secondFormBody == null) {
            return isNull;
        }

        FormBody.Builder targetFormBodyBuilder = new FormBody.Builder();

        /* source */
        for (int x = 0; x < firstFormBody.size(); x++) {

            String name = firstFormBody.name(x);
            String value = firstFormBody.value(x);

            targetFormBodyBuilder.add(name, value);
        }

        /* append */
        for (int x = 0; x < secondFormBody.size(); x++) {

            String name = secondFormBody.name(x);
            String value = secondFormBody.value(x);

            targetFormBodyBuilder.add(name, value);
        }

        return targetFormBodyBuilder.build();
    }

    private MultipartBody concatMultipartBody(MultipartBody firstMultipartBody, MultipartBody secondMultipartBody) {

        MultipartBody isNull =
                firstMultipartBody != null ?
                        firstMultipartBody : secondMultipartBody != null ?
                        secondMultipartBody : null;

        if (firstMultipartBody == null
                || secondMultipartBody == null) {
            return isNull;
        }

        MultipartBody.Builder targetMultipartBodyBuilder = new MultipartBody.Builder();

        /* source */
        for (int x = 0; x < firstMultipartBody.size(); x++) {

            MultipartBody.Part part = firstMultipartBody.part(x);

            targetMultipartBodyBuilder.addPart(part);
        }

        /* append */
        for (int x = 0; x < secondMultipartBody.size(); x++) {

            MultipartBody.Part part = secondMultipartBody.part(x);

            targetMultipartBodyBuilder.addPart(part);
        }

        return targetMultipartBodyBuilder.build();
    }

    private RequestBody concatRequestBody(RequestBody firstRequestBody, FormBody secondFormBody, MultipartBody secondMultipartBody) {

        RequestBody isNull =
                firstRequestBody != null ?
                        firstRequestBody : secondFormBody != null ?
                        secondFormBody : secondMultipartBody != null ?
                        secondMultipartBody : null;

        if (firstRequestBody == null) {
            return isNull;
        }

        /*
        FormBody
         */
        if (firstRequestBody instanceof FormBody) {

            return concatFormBody(
                    (FormBody) firstRequestBody,
                    secondFormBody
            );
        }
        /*
        MultipartBody
         */
        else if (firstRequestBody instanceof MultipartBody) {

            return concatMultipartBody(
                    (MultipartBody) firstRequestBody,
                    secondMultipartBody
            );
        }
        /*
        OtherRequestBody
         */
        else {
            return firstRequestBody;
        }
    }

}
