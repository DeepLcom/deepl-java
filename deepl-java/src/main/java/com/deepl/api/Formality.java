// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Desired level of formality for translation. */
public enum Formality {
  /** Standard level of formality. */
  Default,

  /** Less formality, i.e. more informal. */
  Less,

  /** Increased formality. */
  More,

  /**
   * Less formality, i.e. more informal, if available for the specified target language, otherwise
   * default.
   */
  PreferLess,

  /** Increased formality, if available for the specified target language, otherwise default. */
  PreferMore,
}
