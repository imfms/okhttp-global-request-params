package cn.f_ms.network.okhttp.intercepter.globalrequestparams;

import okhttp3.RequestBody;

/**
 * RequestBody Appender, when need append custom source RequestBody and append RequestBody
 */
public interface RequestBodyAppender {

    /**
     * select this appender accept requestbody type
     *
     * @param sourceBody requestbody
     * @return true -> accept, false -> not accept
     */
    boolean isAccept(RequestBody sourceBody, RequestBody appendBody);

    /**
     * append source RequestBody and appendRequestBody
     *
     * @param source sourceRequestBody
     * @param append appendRequestBody
     * @return appended RequestBody
     */
    RequestBody append(RequestBody source, RequestBody append);
}