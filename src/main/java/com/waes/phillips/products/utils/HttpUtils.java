package com.waes.phillips.products.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.phillips.products.exception.SupplyChainErrorException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@Component
public class HttpUtils {

    private HttpClient client;
    private static ObjectMapper mapper;
    private static Retry retry;

    public HttpUtils(ObjectMapper mapper,
                     Retry retry) {
        this.client = HttpClient.newHttpClient();
        this.mapper = mapper;
        this.retry = retry;
    }

    public <T> T executeGetRequest(String url, Class<T> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            Supplier<T> getRequestSupplier = () -> executeRequest(request, clazz);
            Supplier<T> retryingGetRequest = Retry.decorateSupplier(retry, getRequestSupplier);
            return retryingGetRequest.get();
        } catch (URISyntaxException e) {
            log.error("Failed to execute request to url {}.", url, e);
            throw new SupplyChainErrorException("Failed to access resource");
        }

    }

    public <T, R> R executePostRequest(String url, T body, Class<R> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            return executeRequest(request, clazz);
        } catch (IOException e) {
            log.error("Failed to serialize the request body into {} object.", clazz.getName(), e);
            throw new SupplyChainErrorException(String.format("Failed to serialize the request body into %s object.", clazz.getName()));
        } catch (URISyntaxException e) {
            log.error("There is an error in URL {}", url, e);
            throw new SupplyChainErrorException(String.format("There is an error in URL %s", url));
        }
    }

    public <T> T executeDeleteRequest(String url, Class<T> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .DELETE()
                    .build();
            return executeRequest(request, clazz);
        } catch (URISyntaxException e) {
            log.error("There is an error deleting product in URL {}", url, e);
            throw new SupplyChainErrorException(String.format("There is an error deleting product in URL %s", url));
        }

    }

    private <T> T executeRequest(HttpRequest request, Class<T> clazz) {
        log.info(String.format("Executing request to url %s.", request.uri()));
        HttpResponse response;
        try {
            response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return parseResponseBody(response.body().toString(), clazz);
        } catch (IOException e) {
            log.error("Failed to execute request to url {}.", request.uri(), e);
            throw new SupplyChainErrorException("Failed to access resource");
        } catch (InterruptedException e) {
            log.error("Failed to execute request to url {}.", request.uri(), e);
            throw new SupplyChainErrorException("Failed to access resource");
        }
    }

    private <T> T parseResponseBody(String responseBody, Class<T> clazz) {
        if (Objects.nonNull(responseBody)) {
            try {
                return mapper.readValue(responseBody, clazz);
            } catch (IOException e) {
                log.error(String.format("Failed to deserialize the response body into %s object.", clazz.getName()), e);
                throw new SupplyChainErrorException(String.format("Failed to deserialize the response body into %s object.", clazz.getName()));
            }
        }
        return null;
    }

}
