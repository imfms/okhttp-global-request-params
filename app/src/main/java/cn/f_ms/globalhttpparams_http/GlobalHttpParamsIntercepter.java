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
        HttpParams getParams();
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

        HttpParams appendParams = mOnNeedHttpParams.getParams();

        Request sourceRequest = chain.request();

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
        RequestBody targetRequestBody = sourceRequest.body();
        if (targetRequestBody != null) {

            RequestBody sourceRequestBody = sourceRequest.body();

            /*
            FormBody
             */
            if (targetRequestBody instanceof FormBody) {

                FormBody appendFormBody = appendParams.formBody();
                if (appendFormBody != null) {

                    FormBody sourceFormBody = (FormBody) sourceRequestBody;
                    FormBody.Builder targetFormBodyBuilder = new FormBody.Builder();

                    /* source */
                    for (int x = 0; x < sourceFormBody.size(); x++) {

                        String name = sourceFormBody.name(x);
                        String value = sourceFormBody.value(x);

                        targetFormBodyBuilder.add(name, value);
                    }

                    /* append */
                    for (int x = 0; x < appendFormBody.size(); x++) {

                        String name = appendFormBody.name(x);
                        String value = appendFormBody.value(x);

                        targetFormBodyBuilder.add(name, value);
                    }

                    targetRequestBody = targetFormBodyBuilder.build();
                }

            }
            /*
            MultipartBody
             */
            else if (targetRequestBody instanceof MultipartBody) {

                MultipartBody appendMultipartBody = appendParams.multipartBody();
                if (appendMultipartBody != null) {

                    MultipartBody sourceMultipartBody = (MultipartBody) sourceRequestBody;
                    MultipartBody.Builder targetMultipartBodyBuilder = new MultipartBody.Builder();

                    /* source */
                    for (int x = 0; x < sourceMultipartBody.size(); x++) {

                        MultipartBody.Part part = sourceMultipartBody.part(x);

                        targetMultipartBodyBuilder.addPart(part);
                    }

                    /* append */
                    for (int x = 0; x < appendMultipartBody.size(); x++) {

                        MultipartBody.Part part = appendMultipartBody.part(x);

                        targetMultipartBodyBuilder.addPart(part);
                    }

                    targetRequestBody = targetMultipartBodyBuilder.build();
                }
            }
        }


        Request targetRequest = sourceRequest.newBuilder()
                .headers(targetHeaders)
                .url(targetHttpUrl)
                .method(sourceRequest.method(), targetRequestBody)
                .build();

        return chain.proceed(targetRequest);
    }

    public Headers concatHeader(Headers firstHeaders, Headers secondHeaders) {
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

    public HttpUrl concatUrlQuery(HttpUrl firstUrl, HttpUrl secondUrl) {

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

    public FormBody concatFormBody(FormBody firstFormBody, FormBody secondFormBody) {

        FormBody isNull =
                firstFormBody != null ?
                        firstFormBody : secondFormBody != null ?
                        secondFormBody : null;

        if (firstFormBody == null
                || secondFormBody == null) {
            return isNull;
        }

        // TODO

        if (isNull == null) {
            return null;
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

}
