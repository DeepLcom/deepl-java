// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import org.jetbrains.annotations.Nullable;

/** The result of a text translation. */
public class TextResult {
  private final String text;
  private final String detectedSourceLanguage;
  private final int billedCharacters;
  private final @Nullable String modelTypeUsed;

  /** Constructs a new instance. */
  public TextResult(
      String text,
      String detectedSourceLanguage,
      int billedCharacters,
      @Nullable String modelTypeUsed) {
    this.text = text;
    this.detectedSourceLanguage = LanguageCode.standardize(detectedSourceLanguage);
    this.billedCharacters = billedCharacters;
    this.modelTypeUsed = modelTypeUsed;
  }

  /** The translated text. */
  public String getText() {
    return text;
  }

  /** The language code of the source text detected by DeepL. */
  public String getDetectedSourceLanguage() {
    return detectedSourceLanguage;
  }

  /** Number of characters billed for this text. */
  public int getBilledCharacters() {
    return billedCharacters;
  }

  /** Model type used for the translation of this text. */
  public @Nullable String getModelTypeUsed() {
    return modelTypeUsed;
  }
}
