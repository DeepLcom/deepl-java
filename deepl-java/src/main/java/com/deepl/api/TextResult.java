// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** The result of a text translation. */
public class TextResult {
  private final String text;
  private final String detectedSourceLanguage;

  /** Constructs a new instance. */
  public TextResult(String text, String detectedSourceLanguage) {
    this.text = text;
    this.detectedSourceLanguage = LanguageCode.standardize(detectedSourceLanguage);
  }

  /** The translated text. */
  public String getText() {
    return text;
  }

  /** The language code of the source text detected by DeepL. */
  public String getDetectedSourceLanguage() {
    return detectedSourceLanguage;
  }
}
