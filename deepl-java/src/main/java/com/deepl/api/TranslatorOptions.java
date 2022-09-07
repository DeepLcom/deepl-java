// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.net.Proxy;
import java.time.Duration;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Options to control translator behaviour. These options may be provided to the {@link Translator}
 * constructor.
 *
 * <p>All properties have corresponding setters in fluent-style, so the following is possible:
 * <code>
 *      TranslatorOptions options = new TranslatorOptions()
 *          .setTimeout(Duration.ofSeconds(1)).setMaxRetries(2);
 * </code>
 */
public class TranslatorOptions {
  private int maxRetries = 5;
  private Duration timeout = Duration.ofSeconds(10);
  @Nullable private Proxy proxy = null;
  @Nullable private Map<String, String> headers = null;
  @Nullable private String serverUrl = null;

  /**
   * Set the maximum number of failed attempts that {@link Translator} will retry, per request. By
   * default, 5 retries are made. Note: only errors due to transient conditions are retried.
   */
  public TranslatorOptions setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  /** Set the connection timeout used for each HTTP request retry, the default is 10 seconds. */
  public TranslatorOptions setTimeout(Duration timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Set the proxy to use for HTTP requests. By default, this value is <code>null</code> and no
   * proxy will be used.
   */
  public TranslatorOptions setProxy(Proxy proxy) {
    this.proxy = proxy;
    return this;
  }

  /**
   * Set HTTP headers attached to every HTTP request. By default, this value is <code>null</code>
   * and no extra headers are used. Note that in the {@link Translator} constructor the headers for
   * Authorization and User-Agent are added, unless they are overridden in this option.
   */
  public TranslatorOptions setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  /**
   * Set the base URL for DeepL API that may be overridden for testing purposes. By default, this
   * value is <code>null</code> and the correct DeepL API base URL is selected based on the API
   * account type (free or paid).
   */
  public TranslatorOptions setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
    return this;
  }

  /** Gets the current maximum number of retries. */
  public int getMaxRetries() {
    return maxRetries;
  }

  /** Gets the current maximum request timeout. */
  public Duration getTimeout() {
    return timeout;
  }

  /** Gets the current proxy. */
  public @Nullable Proxy getProxy() {
    return proxy;
  }

  /** Gets the current HTTP headers. */
  public @Nullable Map<String, String> getHeaders() {
    return headers;
  }

  /** Gets the current custom server URL. */
  public @Nullable String getServerUrl() {
    return serverUrl;
  }
}
