package org.stool.session.manager;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stool.myserver.core.Future;
import org.stool.myserver.core.http.HttpClient;
import org.stool.myserver.core.http.HttpClientResponse;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.net.Buffer;
import org.stool.myserver.session.Session;
import org.stool.myserver.session.SessionStore;

import java.nio.charset.Charset;

public class DistributedSessionStore implements SessionStore {

    private final static Logger LOG = LoggerFactory.getLogger(DistributedSessionStore.class);

    private HttpClient httpClient;

    @Override
    public void put(String sessionId, Session session) {
        String sessionJson = JSON.toJSONString(session);
        httpClient.request(HttpMethod.POST, "127.0.0.1", 9000,
                "/sessionManager/" + "?sessionId=" + sessionId + "&session="+sessionJson, null, ar -> {
                    if (ar.failed()) {
                        LOG.error("put fail: {}", ar.cause());
                    }
                }).end();
    }

    @Override
    public Session get(String sessionId) {
        Future<String> sessionFuture = Future.future();
        httpClient.request(HttpMethod.GET, "127.0.0.1", 9000,
                "/sessionManager/" + "?sessionId=" + sessionId, null, ar -> {
                    if (ar.failed()) {
                        LOG.error("put fail: {}", ar.cause());
                    } else {
                        HttpClientResponse httpClientResponse = ar.result();
                        Buffer buffer = Buffer.buffer();
                        httpClientResponse.handler(buffer::appendBuffer);
                        httpClientResponse.endHandler(v ->
                                sessionFuture.tryComplete(buffer.getByteBuf().toString(Charset.forName("utf-8"))));
                    }
                }).end();

        return null;
    }
}
