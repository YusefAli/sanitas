package com.mycorp.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZendeskAsyncCompletionHandler<T> extends AsyncCompletionHandler<T> {
    private final Class<T> clazz;
    private final String name;
    private final Class[] typeParams;
    private final Logger logger = LoggerFactory.getLogger(ZendeskAsyncCompletionHandler.class);
    private ObjectMapper mapper;

    public ZendeskAsyncCompletionHandler(Class clazz, String name, ObjectMapper mapper, Class... typeParams) {
        this.clazz = clazz;
        this.name = name;
        this.typeParams = typeParams;
        this.mapper = mapper;
    }

    @Override
    public T onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
            if (typeParams.length > 0) {
                JavaType type = mapper.getTypeFactory().constructParametricType(clazz, typeParams);
                return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), type);
            }
            return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), clazz);
        } else if (isRateLimitResponse(response)) {
            throw new ZendeskException(response.toString());
        }
        if (response.getStatusCode() == 404) {
            return null;
        }
        throw new ZendeskException(response.toString());
    }

    @Override
    public void onThrowable(Throwable t) {
        if (t instanceof IOException) {
            throw new ZendeskException(t);
        } else {
            super.onThrowable(t);
        }
    }

    private void logResponse(Response response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                    response.getResponseBody());
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Response headers {}", response.getHeaders());
        }
    }

    private boolean isStatus2xx(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    private boolean isRateLimitResponse(Response response) {
        return response.getStatusCode() == 429;
    }
}