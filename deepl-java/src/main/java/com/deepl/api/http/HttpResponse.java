// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.http;

public class HttpResponse {

  private final int code;

  private final String body;

  public HttpResponse(int code, String body) {
    this.code = code;
    this.body = body;
  }

  public int getCode() {
    return code;
  }

  public String getBody() {
    return body;
  }
}
