// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.utils;

import java.io.*;

public class NamedStream {
  private final String fileName;
  private final InputStream inputStream;

  public NamedStream(String fileName, InputStream inputStream) {
    this.fileName = fileName;
    this.inputStream = inputStream;
  }

  public String getFileName() {
    return fileName;
  }

  public InputStream getInputStream() {
    return inputStream;
  }
}
