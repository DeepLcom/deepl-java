// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Base class for all exceptions thrown by this library. */
public class DeepLException extends Exception {
  public DeepLException(String message) {
    super(message);
  }

  public DeepLException(String message, Throwable cause) {
    super(message, cause);
  }
}
