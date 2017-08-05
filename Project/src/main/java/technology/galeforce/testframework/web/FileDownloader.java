package technology.galeforce.testframework.web;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;


/**
 * Created by peter.gale on 04/11/2016.
 */

public class FileDownloader {

    private HTTP.RequestType httpRequestMethod = HTTP.RequestType.GET;
    private URI fileURI;
    List<NameValuePair> urlParameters;

    BasicCookieStore basicCookieStore;
    String userAgent;

    public FileDownloader() {}

    public FileDownloader(BasicCookieStore basicCookieStore, String userAgent) {
        this.basicCookieStore = basicCookieStore;
        this.userAgent = userAgent;
    }

    public void setHTTPRequestMethod(HTTP.RequestType requestType) {
        httpRequestMethod=requestType;
    }

    public void setURLParameters(HTTP.RequestType requestType) {
        this.urlParameters=urlParameters;
    }

    public void setURI(URI linkToFile) throws MalformedURLException {fileURI=linkToFile;}

    public int getLinkHTTPStatus() throws Exception {
        int httpStatusCode = 0;
        HttpResponse downloadableFile = null;
        try {
            downloadableFile = makeHTTPConnection();
            try {
                httpStatusCode = downloadableFile.getStatusLine().getStatusCode();
            } finally {
                if (null != downloadableFile.getEntity()) {
                    downloadableFile.getEntity().getContent().close();
                }
            }
        } catch (HttpHostConnectException e) {
            httpStatusCode = -1;
        }
        return httpStatusCode;
    }

    private HttpResponse makeHTTPConnection() throws IOException, NullPointerException {

        if (fileURI ==null) throw new NullPointerException("No file URI specified");

        HttpClient client = HttpClientBuilder.create().build();

        HttpRequestBase requestMethod = httpRequestMethod.getRequestMethod();
        requestMethod.setURI(fileURI);

        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, this.basicCookieStore);
        requestMethod.setHeader("User-Agent", this.userAgent);

        if (null != urlParameters && (
                httpRequestMethod.equals(HTTP.RequestType.PATCH) ||
                        httpRequestMethod.equals(HTTP.RequestType.POST) ||
                        httpRequestMethod.equals(HTTP.RequestType.PUT)
                )) {
            ((HttpEntityEnclosingRequestBase) requestMethod).setEntity(new UrlEncodedFormEntity(urlParameters));
        }

        return client.execute(requestMethod, localContext);

    }

    public File downloadFile(String fileURL) throws IOException {
        File downloadedFile = File.createTempFile("download",".html"); //,"C:/WIP");
        HttpResponse fileToDownload = makeHTTPConnection();
        try {
            FileUtils.copyInputStreamToFile(fileToDownload.getEntity().getContent(),downloadedFile);
        } finally {
            fileToDownload.getEntity().getContent().close();
        }
        return downloadedFile;
    }

}



