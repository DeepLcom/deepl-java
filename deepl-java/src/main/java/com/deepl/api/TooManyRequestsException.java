// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Exception thrown when too many requests are made to the DeepL API too quickly. */
public class TooManyRequestsException extends DeepLException {
  public TooManyRequestsException(String message) {
    super(message);
  }
}
