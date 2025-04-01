// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.GlossaryEntries;
import com.deepl.api.MultilingualGlossaryDictionaryEntries;
import com.google.gson.annotations.SerializedName;

/**
 * Class representing v3 list-glossaries response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
public class MultilingualGlossaryDictionaryEntriesResponse {

  @SerializedName(value = "source_lang")
  private final String sourceLanguageCode;

  @SerializedName(value = "target_lang")
  private final String targetLanguageCode;

  @SerializedName(value = "entries")
  private final String entries;

  @SerializedName(value = "entries_format")
  private final String entriesFormat;

  /**
   * Initializes a new {@link MultilingualGlossaryDictionaryEntriesResponse} containing information
   * about a glossary dictionary.
   *
   * @param sourceLanguageCode the source language for this dictionary
   * @param targetLanguageCode the target language for this dictionary
   * @param entries the entries in this dictionary
   * @param entriesFormat the format of the entries in this dictionary
   */
  public MultilingualGlossaryDictionaryEntriesResponse(
      String sourceLanguageCode, String targetLanguageCode, String entries, String entriesFormat) {
    this.sourceLanguageCode = sourceLanguageCode;
    this.targetLanguageCode = targetLanguageCode;
    this.entries = entries;
    this.entriesFormat = entriesFormat;
  }

  public MultilingualGlossaryDictionaryEntries getDictionaryEntries() {
    return new MultilingualGlossaryDictionaryEntries(
        this.sourceLanguageCode,
        this.targetLanguageCode,
        new GlossaryEntries(GlossaryEntries.fromTsv(this.entries)));
  }
}
