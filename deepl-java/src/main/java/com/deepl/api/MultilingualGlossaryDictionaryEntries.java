// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Stores the entries of a glossary. */
public class MultilingualGlossaryDictionaryEntries {
  private final String sourceLanguageCode;
  private final String targetLanguageCode;
  private final GlossaryEntries entries;

  /**
   * Initializes a new {@link MultilingualGlossaryDictionaryInfo} containing information about a
   * glossary dictionary.
   *
   * @param sourceLanguageCode the source language for this dictionary
   * @param targetLanguageCode the target language for this dictionary
   * @param entries the entries in this dictionary
   */
  public MultilingualGlossaryDictionaryEntries(
      String sourceLanguageCode, String targetLanguageCode, GlossaryEntries entries) {
    this.sourceLanguageCode = sourceLanguageCode;
    this.targetLanguageCode = targetLanguageCode;
    this.entries = entries;
  }

  /** @return the source language code */
  public String getSourceLanguageCode() {
    return this.sourceLanguageCode;
  }

  /** @return the target language code */
  public String getTargetLanguageCode() {
    return this.targetLanguageCode;
  }

  /** @return the entry count */
  public GlossaryEntries getEntries() {
    return this.entries;
  }
}
