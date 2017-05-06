package cn.f_ms.globalhttpparams_http;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;

/**
 * Desc: Http Request Params Wrapper
 *
 * @author f-ms
 * @time 2017/5/6
 */
public class HttpParams {

    private UrlQuerys mUrlQuerys;
    private Headers mHttpHeaders;
    private FormBody mFormBody;
    private MultipartBody mMultipartBody;

    private HttpParams(UrlQuerys mUrlQuerys, Headers mHttpHeaders, FormBody mFormBody, MultipartBody mMultipartBody) {
        this.mUrlQuerys = mUrlQuerys;
        this.mHttpHeaders = mHttpHeaders;
        this.mFormBody = mFormBody;
        this.mMultipartBody = mMultipartBody;
    }

    public UrlQuerys urlQuerys() { return mUrlQuerys; }
    public Headers httpHeaders() { return mHttpHeaders; }
    public FormBody formBody() { return mFormBody; }
    public MultipartBody multipartBody() { return mMultipartBody; }

    public static class Builder {

        private UrlQuerys mUrlQuerys;
        private Headers mHttpHeaders;
        private FormBody mFormBody;
        private MultipartBody mMultipartBody;

        public Builder urlQuerys(UrlQuerys mUrlQuerys) { this.mUrlQuerys = mUrlQuerys; return this; }
        public Builder httpHeaders(Headers mHttpHeaders) { this.mHttpHeaders = mHttpHeaders; return this; }
        public Builder formBody(FormBody mFormBody) { this.mFormBody = mFormBody; return this; }
        public Builder multipartBody(MultipartBody mMultipartBody) { this.mMultipartBody = mMultipartBody; return this;}

        public HttpParams build() { return new HttpParams(mUrlQuerys, mHttpHeaders, mFormBody, mMultipartBody); }
    }

}
