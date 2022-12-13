// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.deepl.api.http.*;
import com.deepl.api.utils.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * Helper class providing functions to make HTTP requests and retry with exponential-backoff.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class HttpClientWrapper {
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String POST = "POST";
  private final String serverUrl;
  private final Map<String, String> headers;
  private final Duration minTimeout;
  private final @Nullable Proxy proxy;
  private final int maxRetries;

  public HttpClientWrapper(
      String serverUrl,
      Map<String, String> headers,
      Duration minTimeout,
      @Nullable Proxy proxy,
      int maxRetries) {
    this.serverUrl = serverUrl;
    this.headers = headers;
    this.minTimeout = minTimeout;
    this.proxy = proxy;
    this.maxRetries = maxRetries;
  }

  public HttpResponse sendRequestWithBackoff(String relativeUrl)
      throws InterruptedException, DeepLException {
    return sendRequestWithBackoff(POST, relativeUrl, null).toStringResponse();
  }

  public HttpResponse sendRequestWithBackoff(
      String relativeUrl, @Nullable Iterable<KeyValuePair<String, String>> params)
      throws InterruptedException, DeepLException {
    HttpContent content = HttpContent.buildFormURLEncodedContent(params);
    return sendRequestWithBackoff(POST, relativeUrl, content).toStringResponse();
  }

  public HttpResponseStream downloadWithBackoff(
      String relativeUrl, @Nullable Iterable<KeyValuePair<String, String>> params)
      throws InterruptedException, DeepLException {
    HttpContent content = HttpContent.buildFormURLEncodedContent(params);
    return sendRequestWithBackoff(POST, relativeUrl, content);
  }

  public HttpResponse uploadWithBackoff(
      String relativeUrl,
      @Nullable Iterable<KeyValuePair<String, String>> params,
      String fileName,
      InputStream inputStream)
      throws InterruptedException, DeepLException {
    ArrayList<KeyValuePair<String, Object>> fields = new ArrayList<>();
    fields.add(new KeyValuePair<>("file", new NamedStream(fileName, inputStream)));
    if (params != null) {
      params.forEach(
          (KeyValuePair<String, String> entry) -> {
            fields.add(new KeyValuePair<>(entry.getKey(), entry.getValue()));
          });
    }
    HttpContent content;
    try {
      content = HttpContent.buildMultipartFormDataContent(fields);
    } catch (Exception e) {
      throw new DeepLException("Failed building request", e);
    }
    return sendRequestWithBackoff(POST, relativeUrl, content).toStringResponse();
  }

  // Sends a request with exponential backoff
  private HttpResponseStream sendRequestWithBackoff(
      String method, String relativeUrl, HttpContent content)
      throws InterruptedException, DeepLException {
    BackoffTimer backoffTimer = new BackoffTimer(this.minTimeout);
    while (true) {
      try {
        HttpResponseStream response =
            sendRequest(method, serverUrl + relativeUrl, backoffTimer.getTimeoutMillis(), content);
        if (backoffTimer.getNumRetries() >= this.maxRetries) {
          return response;
        } else if (response.getCode() != 429 && response.getCode() < 500) {
          return response;
        }
        response.close();
      } catch (ConnectionException exception) {
        if (!exception.getShouldRetry() || backoffTimer.getNumRetries() >= this.maxRetries) {
          throw exception;
        }
      }
      backoffTimer.sleepUntilRetry();
    }
  }

  private HttpResponseStream sendRequest(
      String method, String urlString, long timeoutMs, HttpContent content)
      throws ConnectionException {
    try {
      URL url = new URL(urlString);
      HttpURLConnection connection =
          (HttpURLConnection) (proxy != null ? url.openConnection(proxy) : url.openConnection());

      connection.setRequestMethod(method);
      connection.setConnectTimeout((int) timeoutMs);
      connection.setReadTimeout((int) timeoutMs);
      connection.setUseCaches(false);

      for (Map.Entry<String, String> entry : this.headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }

      if (content != null) {
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE, content.getContentType());

        try (OutputStream output = connection.getOutputStream()) {
          output.write(content.getContent());
        }
      }

      int responseCode = connection.getResponseCode();
      InputStream responseStream =
          (responseCode >= 200 && responseCode < 400)
              ? connection.getInputStream()
              : connection.getErrorStream();
      return new HttpResponseStream(responseCode, responseStream);
    } catch (SocketTimeoutException e) {
      throw new ConnectionException(e.getMessage(), true, e);
    } catch (RuntimeException | IOException e) {
      throw new ConnectionException(e.getMessage(), false, e);
    }
  }
}
