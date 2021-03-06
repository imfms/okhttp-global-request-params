[![](https://jitpack.io/v/imfms/okhttp-global-http-params.svg)](https://jitpack.io/#imfms/okhttp-global-http-params)


# okhttp-global-request-params

okhttp interceptor 方式实现的强壮全局附加请求参数工具，可根据业务需求自行调节各种实现

## 引用方式

- Gradle 当前最新版本为 [![](https://jitpack.io/v/imfms/okhttp-global-http-params.svg)](https://jitpack.io/#imfms/okhttp-global-http-params)


        repositories {
            maven { url 'https://jitpack.io' } // If not already there
        }
        
        dependencies {
            compile 'com.github.imfms:okhttp-global-http-params:${最新版本}'
        }

## 可添加参数
- Header Params
- URL query Params
- RequestBody Params
    - FormBody - 提供默认实现
    - MulitPartBody - 提供默认实现
    - [自定义]
    
## 使用方法

### 代码示例

~~~java
 new OkHttpClient.Builder()
    .addInterceptor(
            new GlobalHttpParamsIntercepter(new GlobalHttpParamsIntercepter.OnNeedHttpParams() {
                @Override
                public HttpParams getParams(Request request) {
                    return new HttpParams.Builder()
                            // 设置添加URL_Query参数
                            .setUrlQuerys(
                                    new UrlQuerys.Builder()
                                            .addQueryParameter("local_time", System.currentTimeMillis() + "")
                                            .build()
                            )
                            // 设置添加RequestHeader参数
                            .setRequestHeaders(
                                    new Headers.Builder()
                                            .add("local_time", System.currentTimeMillis() + "")
                                            .build()
                            )
                            // 添加RequstBody参数(请求方法可携带Body情况)
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
            })
    )
~~~


### 自定义RequestBody处理

默认会添加追加器 FormBodyAppender 和 MulitPartBodyAppender, 如不需要可在使用前调用 clearRequestBodyAppender 方法

在实际业务需求中可能会使用自定义RequestBody规范，故使用提供的方法则无法迎合业务需求，这种情况下可以自定义RequestBody追加器并添加到本库中

#### 追加器定义 RequestBodyAppender
~~~java
public interface RequestBodyAppender {

    /**
     * 判断源RequestBody与追加RequestBody是否符合本追加器
     *
     * @return true -> 符合, false -> not 不符合
     */
    boolean isAccept(RequestBody sourceBody, RequestBody appendBody);

    /**
     * 将追加RequestBody追加到源RequestBody
     *
     * @return appended 追加后RequestBody
     */
    RequestBody append(RequestBody source, RequestBody append);
}
~~~

#### 添加追加器到GlobalHttpParamsIntercepter
~~~java
    GlobalHttpParamsIntercepter.addRequestBodyAppender(RequestBodyAppender appender);
~~~

**注意: 因实际项目中可能会遇到当 '须携带请求体的请求方法(post, put ... )而请求体为空时(contentLength == 0)' 想要追加全局参数的情况, 因为无法知晓开发者所需要的contentType及对应的参数组织方式, 故开发者如有类似需求请务必自定义RequestBodyAppender进行处理**

**例如:**

~~~java
public class EmptyRequestBodyAppender implements RequestBodyAppender {

    @Override public boolean isAccept(RequestBody requestBody, RequestBody requestBody1) {

        long contentLength;

        try {
            contentLength = requestBody.contentLength();
        } catch (IOException e) {
            contentLength = -1;
        }

        return contentLength <= 0
                && requestBody1 instanceof FormBody;
    }

    @Override public RequestBody append(RequestBody requestBody, RequestBody requestBody1) {
        return requestBody1;
    }

}
~~~