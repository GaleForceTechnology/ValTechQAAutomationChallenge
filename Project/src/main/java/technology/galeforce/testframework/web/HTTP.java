package technology.galeforce.testframework.web;

import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by peter.gale on 04/11/2016.
 */

public class HTTP {

    public enum RequestType {
        OPTIONS(new HttpOptions()),
        GET(new HttpOptions()),
        HEAD(new HttpOptions()),
        PATCH(new HttpOptions()),
        POST(new HttpOptions()),
        PUT(new HttpOptions()),
        DELETE(new HttpOptions()),
        TRACE(new HttpOptions());

        private final HttpRequestBase requestMethod;


        RequestType(HttpRequestBase requestMethod) {
            this.requestMethod=requestMethod;
        }

        public HttpRequestBase getRequestMethod() {
            return this.requestMethod;
        }

    }

}
