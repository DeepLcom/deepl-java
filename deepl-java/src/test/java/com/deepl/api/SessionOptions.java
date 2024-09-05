// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionOptions {
  // Mock server session options
  public Integer noResponse;
  public Integer respondWith429;
  public Long initCharacterLimit;
  public Long initDocumentLimit;
  public Long initTeamDocumentLimit;
  public Integer documentFailure;
  public Duration documentQueueTime;
  public Duration documentTranslateTime;
  public Boolean expectProxy;

  public boolean randomAuthKey;

  SessionOptions() {
    randomAuthKey = false;
  }

  public Map<String, String> createSessionHeaders() {
    Map<String, String> headers = new HashMap<>();

    String uuid = UUID.randomUUID().toString();
    headers.put("mock-server-session", "deepl-java-test/" + uuid);

    if (noResponse != null) {
      headers.put("mock-server-session-no-response-count", noResponse.toString());
    }
    if (respondWith429 != null) {
      headers.put("mock-server-session-429-count", respondWith429.toString());
    }
    if (initCharacterLimit != null) {
      headers.put("mock-server-session-init-character-limit", initCharacterLimit.toString());
    }
    if (initDocumentLimit != null) {
      headers.put("mock-server-session-init-document-limit", initDocumentLimit.toString());
    }
    if (initTeamDocumentLimit != null) {
      headers.put("mock-server-session-init-team-document-limit", initTeamDocumentLimit.toString());
    }
    if (documentFailure != null) {
      headers.put("mock-server-session-doc-failure", documentFailure.toString());
    }
    if (documentQueueTime != null) {
      headers.put(
          "mock-server-session-doc-queue-time", Long.toString(documentQueueTime.toMillis()));
    }
    if (documentTranslateTime != null) {
      headers.put(
          "mock-server-session-doc-translate-time",
          Long.toString(documentTranslateTime.toMillis()));
    }
    if (expectProxy != null) {
      headers.put("mock-server-session-expect-proxy", expectProxy ? "1" : "0");
    }

    return headers;
  }

  public SessionOptions setNoResponse(int noResponse) {
    this.noResponse = noResponse;
    return this;
  }

  public SessionOptions setRespondWith429(int respondWith429) {
    this.respondWith429 = respondWith429;
    return this;
  }

  public SessionOptions setInitCharacterLimit(long initCharacterLimit) {
    this.initCharacterLimit = initCharacterLimit;
    return this;
  }

  public SessionOptions setInitDocumentLimit(long initDocumentLimit) {
    this.initDocumentLimit = initDocumentLimit;
    return this;
  }

  public SessionOptions setInitTeamDocumentLimit(long initTeamDocumentLimit) {
    this.initTeamDocumentLimit = initTeamDocumentLimit;
    return this;
  }

  public SessionOptions setDocumentFailure(int documentFailure) {
    this.documentFailure = documentFailure;
    return this;
  }

  public SessionOptions setDocumentQueueTime(Duration documentQueueTime) {
    this.documentQueueTime = documentQueueTime;
    return this;
  }

  public SessionOptions setDocumentTranslateTime(Duration documentTranslateTime) {
    this.documentTranslateTime = documentTranslateTime;
    return this;
  }

  public SessionOptions setExpectProxy(boolean expectProxy) {
    this.expectProxy = expectProxy;
    return this;
  }

  public SessionOptions withRandomAuthKey() {
    this.randomAuthKey = true;
    return this;
  }
}
