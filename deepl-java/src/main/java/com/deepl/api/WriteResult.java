// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** The result of a text translation. */
public class WriteResult {
  private final String text;
  private final String detectedSourceLanguage;
  private final String targetLanguage;

  /** Constructs a new instance. */
  public WriteResult(String text, String detectedSourceLanguage, String targetLanguage) {
    this.text = text;
    this.detectedSourceLanguage = LanguageCode.standardize(detectedSourceLanguage);
    this.targetLanguage = targetLanguage;
  }

  /** The translated text. */
  public String getText() {
    return text;
  }

  /** The language code of the source text detected by DeepL. */
  public String getDetectedSourceLanguage() {
    return detectedSourceLanguage;
  }

  /** The language code of the target language set by the request. */
  public String getTargetLanguage() {
    return targetLanguage;
  }
}
