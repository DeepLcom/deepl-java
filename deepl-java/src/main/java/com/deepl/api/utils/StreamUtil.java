// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.utils;

import java.io.*;
import java.nio.charset.*;

public class StreamUtil {
  public static final int DEFAULT_BUFFER_SIZE = 1024;

  public static String readStream(InputStream inputStream) throws IOException {
    Charset charset = StandardCharsets.UTF_8;
    final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    final StringBuilder sb = new StringBuilder();
    final Reader in = new BufferedReader(new InputStreamReader(inputStream, charset));
    int charsRead;
    while ((charsRead = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) > 0) {
      sb.append(buffer, 0, charsRead);
    }
    return sb.toString();
  }

  /**
   * Reads all bytes from input stream and writes the bytes to the given output stream in the order
   * that they are read. On return, input stream will be at end of stream. This method does not
   * close either stream.
   *
   * <p>Implementation based on {@link InputStream#transferTo(OutputStream)} added in Java 9.
   *
   * @param inputStream The input stream, non-null.
   * @param outputStream The output stream, non-null.
   * @return Number of bytes transferred.
   * @throws IOException if an I/O error occurs when reading or writing.
   */
  public static long transferTo(InputStream inputStream, OutputStream outputStream)
      throws IOException {
    long transferred = 0;
    final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int read;
    while ((read = inputStream.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
      outputStream.write(buffer, 0, read);
      transferred += read;
    }
    return transferred;
  }
}
