// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Exception thrown when the specified authentication key was invalid. */
public class AuthorizationException extends DeepLException {
  public AuthorizationException(String message) {
    super(message);
  }
}
