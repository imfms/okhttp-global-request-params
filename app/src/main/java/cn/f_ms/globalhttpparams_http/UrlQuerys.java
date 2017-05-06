package cn.f_ms.globalhttpparams_http;

import okhttp3.HttpUrl;

public class UrlQuerys {

    private HttpUrl mHttpUrl;

    private UrlQuerys(HttpUrl httpUrl) { this.mHttpUrl = httpUrl; }

    HttpUrl url() { return mHttpUrl; }

    public static class Builder {

        private HttpUrl.Builder httpUrlBuilder;

        public Builder() { httpUrlBuilder = new HttpUrl.Builder(); }

        /**
         * add url query paramter without urlencode
         *
         * @param name  name
         * @param value value
         */
        public Builder addQueryParameter(String name, String value) {
            httpUrlBuilder.addQueryParameter(name, value);
            return this;
        }

        /**
         * add url query paramter with urlencode
         *
         * @param encodeName  encodeName
         * @param encodeValue encodeValue
         */
        public Builder addEncodedQueryParameter(String encodeName, String encodeValue) {
            httpUrlBuilder.addEncodedQueryParameter(encodeName, encodeValue);
            return this;
        }

        public UrlQuerys build() {

            /* unuseful, jump HttpUrl's throw exception */
            httpUrlBuilder
                    .scheme("http")
                    .host("none");

            return new UrlQuerys(httpUrlBuilder.build());
        }
    }

}