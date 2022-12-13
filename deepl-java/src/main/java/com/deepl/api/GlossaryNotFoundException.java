// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Exception thrown when the specified glossary could not be found. */
public class GlossaryNotFoundException extends NotFoundException {
  public GlossaryNotFoundException(String message) {
    super(message);
  }
}
