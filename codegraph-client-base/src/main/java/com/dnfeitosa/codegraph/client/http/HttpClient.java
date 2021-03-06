package com.dnfeitosa.codegraph.client.http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpClient {

    private String baseUrl;

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response post(String path, Object body, ContentType contentType) {
        try {
            org.apache.http.client.HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(baseUrl + "/" + path);
            httpPost.setEntity(createEntity(body, contentType));
            return new Response(client.execute(httpPost));
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
    }

    private StringEntity createEntity(Object body, ContentType contentType) {
        String serialized = contentType.getSerializer().serialize(body);
        return new StringEntity(serialized, contentType.getType());
    }
}
