// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.SerializedName;

/** Stores the entries of a glossary. */
public class MultilingualGlossaryDictionaryInfo {
  @SerializedName(value = "source_lang")
  private final String sourceLanguageCode;

  @SerializedName(value = "target_lang")
  private final String targetLanguageCode;

  @SerializedName(value = "entry_count")
  private final long entryCount;

  /**
   * Initializes a new {@link MultilingualGlossaryDictionaryInfo} containing information about a
   * glossary dictionary.
   *
   * @param sourceLanguageCode the source language for this dictionary
   * @param targetLanguageCode the target language for this dictionary
   * @param entryCount the number of entries in this dictionary
   */
  public MultilingualGlossaryDictionaryInfo(
      String sourceLanguageCode, String targetLanguageCode, long entryCount) {
    this.sourceLanguageCode = sourceLanguageCode;
    this.targetLanguageCode = targetLanguageCode;
    this.entryCount = entryCount;
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
  public long getEntryCount() {
    return this.entryCount;
  }
}
