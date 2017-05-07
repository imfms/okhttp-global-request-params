package cn.f_ms.network.okhttp.intercepter.globalrequestparams;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.RequestBody;

/**
 * Desc: Http Request Params Wrapper
 *
 * @author f-ms
 * @time 2017/5/6
 */
public class HttpParams {

    private UrlQuerys mUrlQuerys;
    private Headers mRequestHeaders;
    private ArrayList<RequestBody> mRequestBody;

    private HttpParams(UrlQuerys mUrlQuerys, Headers mRequestHeaders, ArrayList<RequestBody> mBody) {
        this.mUrlQuerys = mUrlQuerys;
        this.mRequestHeaders = mRequestHeaders;
        this.mRequestBody = mBody;
    }

    public UrlQuerys urlQuerys() { return mUrlQuerys; }
    public Headers requestHeaders() { return mRequestHeaders; }
    public ArrayList<RequestBody> requestBody() { return mRequestBody; }

    public static class Builder {

        private UrlQuerys mUrlQuerys;
        private Headers mHttpHeaders;
        private ArrayList<RequestBody> mRequestBodyList;

        public Builder setUrlQuerys(UrlQuerys mUrlQuerys) { this.mUrlQuerys = mUrlQuerys; return this; }
        public Builder setRequestHeaders(Headers mHttpHeaders) { this.mHttpHeaders = mHttpHeaders; return this; }
        public Builder addRequestBody(RequestBody requestBody) {
            if (requestBody == null) {
                throw new NullPointerException();
            }
            if (mRequestBodyList == null) {
                mRequestBodyList = new ArrayList<>();
            }
            mRequestBodyList.add(requestBody);
            return this;
        }

        public HttpParams build() { return new HttpParams(mUrlQuerys, mHttpHeaders, mRequestBodyList); }
    }

}
