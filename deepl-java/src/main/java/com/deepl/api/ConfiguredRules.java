// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** Configuration rules for a style rule list. */
public class ConfiguredRules {
  @SerializedName(value = "dates_and_times")
  @Nullable
  private final Map<String, String> datesAndTimes;

  @SerializedName(value = "formatting")
  @Nullable
  private final Map<String, String> formatting;

  @SerializedName(value = "numbers")
  @Nullable
  private final Map<String, String> numbers;

  @SerializedName(value = "punctuation")
  @Nullable
  private final Map<String, String> punctuation;

  @SerializedName(value = "spelling_and_grammar")
  @Nullable
  private final Map<String, String> spellingAndGrammar;

  @SerializedName(value = "style_and_tone")
  @Nullable
  private final Map<String, String> styleAndTone;

  @SerializedName(value = "vocabulary")
  @Nullable
  private final Map<String, String> vocabulary;

  /**
   * Initializes a new {@link ConfiguredRules} containing configuration rules for a style rule list.
   *
   * @param datesAndTimes Date and time formatting rules.
   * @param formatting Text formatting rules.
   * @param numbers Number formatting rules.
   * @param punctuation Punctuation rules.
   * @param spellingAndGrammar Spelling and grammar rules.
   * @param styleAndTone Style and tone rules.
   * @param vocabulary Vocabulary rules.
   */
  public ConfiguredRules(
      @Nullable Map<String, String> datesAndTimes,
      @Nullable Map<String, String> formatting,
      @Nullable Map<String, String> numbers,
      @Nullable Map<String, String> punctuation,
      @Nullable Map<String, String> spellingAndGrammar,
      @Nullable Map<String, String> styleAndTone,
      @Nullable Map<String, String> vocabulary) {
    this.datesAndTimes = datesAndTimes;
    this.formatting = formatting;
    this.numbers = numbers;
    this.punctuation = punctuation;
    this.spellingAndGrammar = spellingAndGrammar;
    this.styleAndTone = styleAndTone;
    this.vocabulary = vocabulary;
  }

  /** @return Date and time formatting rules. */
  @Nullable
  public Map<String, String> getDatesAndTimes() {
    return datesAndTimes;
  }

  /** @return Text formatting rules. */
  @Nullable
  public Map<String, String> getFormatting() {
    return formatting;
  }

  /** @return Number formatting rules. */
  @Nullable
  public Map<String, String> getNumbers() {
    return numbers;
  }

  /** @return Punctuation rules. */
  @Nullable
  public Map<String, String> getPunctuation() {
    return punctuation;
  }

  /** @return Spelling and grammar rules. */
  @Nullable
  public Map<String, String> getSpellingAndGrammar() {
    return spellingAndGrammar;
  }

  /** @return Style and tone rules. */
  @Nullable
  public Map<String, String> getStyleAndTone() {
    return styleAndTone;
  }

  /** @return Vocabulary rules. */
  @Nullable
  public Map<String, String> getVocabulary() {
    return vocabulary;
  }
}
