package cn.f_ms.network.okhttp.intercepter.globalrequestparams;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * RequestBody_FormBody appender
 *
 * @author f-ms
 * @time 2017/5/7
 */
public class FormBodyAppender implements RequestBodyAppender {

    private static class Instance {
        static final FormBodyAppender INSTANCE = new FormBodyAppender();
    }

    private FormBodyAppender() {}

    public static FormBodyAppender getInstance() {
        return Instance.INSTANCE;
    }

    @Override
    public boolean isAccept(RequestBody sourceBody, RequestBody appendBody) {
        return sourceBody instanceof FormBody
                && appendBody instanceof FormBody;
    }

    @Override
    public RequestBody append(RequestBody source, RequestBody append) {

        FormBody firstFormBody = (FormBody) source;
        FormBody secondFormBody = (FormBody) append;

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
