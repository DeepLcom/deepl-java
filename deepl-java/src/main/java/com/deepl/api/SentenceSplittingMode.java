// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Enum controlling how input translation text should be split into sentences. */
public enum SentenceSplittingMode {
  /**
   * Input translation text will be split into sentences using both newlines and punctuation, this
   * is the default behaviour.
   */
  All,

  /**
   * Input text will not be split into sentences. This is advisable for applications where each
   * input translation text is only one sentence.
   */
  Off,

  /**
   * Input translation text will be split into sentences using only punctuation but not newlines.
   */
  NoNewlines,
}
