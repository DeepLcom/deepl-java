// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.http;

import com.deepl.api.*;
import com.deepl.api.utils.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import org.jetbrains.annotations.*;

public class HttpContent {
  private static final String LINE_BREAK = "\r\n";
  private final String contentType;
  private final byte[] content;

  private HttpContent(String contentType, byte[] content) {
    this.contentType = contentType;
    this.content = content;
  }

  public byte[] getContent() {
    return content;
  }

  public String getContentType() {
    return contentType;
  }

  public static HttpContent buildFormURLEncodedContent(
      @Nullable Iterable<KeyValuePair<String, String>> params) throws DeepLException {
    StringBuilder sb = new StringBuilder();
    if (params != null) {
      for (KeyValuePair<String, String> pair : params) {
        if (sb.length() != 0) sb.append("&");
        sb.append(urlEncode(pair.getKey()));
        sb.append("=");
        sb.append(urlEncode(pair.getValue()));
      }
    }
    return new HttpContent(
        "application/x-www-form-urlencoded", sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  private static String urlEncode(String value) throws DeepLException {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException exception) {
      throw new DeepLException("Error while URL-encoding request", exception);
    }
  }

  public static HttpContent buildMultipartFormDataContent(
      Iterable<KeyValuePair<String, Object>> params) throws Exception {
    String boundary = UUID.randomUUID().toString();
    return buildMultipartFormDataContent(params, boundary);
  }

  private static HttpContent buildMultipartFormDataContent(
      Iterable<KeyValuePair<String, Object>> params, String boundary) throws Exception {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        PrintWriter writer = new PrintWriter(osw)) {

      if (params != null) {
        for (KeyValuePair<String, Object> entry : params) {
          String key = entry.getKey();
          Object value = entry.getValue();
          if (entry.getValue() instanceof NamedStream) {
            NamedStream namedStream = (NamedStream) entry.getValue();
            String probableContentType =
                URLConnection.guessContentTypeFromName(namedStream.getFileName());
            writer.append("--").append(boundary).append(LINE_BREAK);
            writer
                .append("Content-Disposition: form-data; name=\"")
                .append(key)
                .append("\"; filename=\"")
                .append(namedStream.getFileName())
                .append("\"")
                .append(LINE_BREAK);
            writer.append("Content-Type: ").append(probableContentType).append(LINE_BREAK);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_BREAK);
            writer.append(LINE_BREAK);
            writer.flush();

            StreamUtil.transferTo(namedStream.getInputStream(), stream);

            writer.append(LINE_BREAK);
            writer.flush();
          } else if (value instanceof String) {
            writer.append("--").append(boundary).append(LINE_BREAK);
            writer
                .append("Content-Disposition: form-data; name=\"")
                .append(key)
                .append("\"")
                .append(LINE_BREAK);
            writer.append(LINE_BREAK);
            writer.append((String) value).append(LINE_BREAK);
            writer.flush();
          } else {
            throw new Exception("Unknown argument type: " + value.getClass().getName());
          }
        }
      }

      writer.append("--").append(boundary).append("--").append(LINE_BREAK);
      writer.flush();
      writer.close();
      return new HttpContent("multipart/form-data; boundary=" + boundary, stream.toByteArray());
    }
  }
}
