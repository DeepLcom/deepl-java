// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Exception thrown when attempting to download a translated document before it is ready. */
public class DocumentNotReadyException extends DeepLException {
  public DocumentNotReadyException(String message) {
    super(message);
  }
}
