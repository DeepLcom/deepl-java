// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** Information about a translation memory. */
public class TranslationMemoryInfo {
  @SerializedName(value = "translation_memory_id")
  private final String translationMemoryId;

  @SerializedName(value = "name")
  private final String name;

  @SerializedName(value = "source_language")
  private final String sourceLanguage;

  @SerializedName(value = "target_languages")
  private final List<String> targetLanguages;

  @SerializedName(value = "segment_count")
  private final int segmentCount;

  @SerializedName(value = "creation_time")
  private final @Nullable Date creationTime;

  @SerializedName(value = "updated_time")
  private final @Nullable Date updatedTime;

  /**
   * Initializes a new {@link TranslationMemoryInfo} containing information about a translation
   * memory.
   *
   * @param translationMemoryId Unique ID assigned to the translation memory.
   * @param name User-defined name assigned to the translation memory.
   * @param sourceLanguage Source language code for the translation memory.
   * @param targetLanguages List of target language codes for the translation memory.
   * @param segmentCount Number of segments in the translation memory.
   */
  public TranslationMemoryInfo(
      String translationMemoryId,
      String name,
      String sourceLanguage,
      List<String> targetLanguages,
      int segmentCount) {
    this(translationMemoryId, name, sourceLanguage, targetLanguages, segmentCount, null, null);
  }

  /**
   * Initializes a new {@link TranslationMemoryInfo} containing information about a translation
   * memory.
   *
   * @param translationMemoryId Unique ID assigned to the translation memory.
   * @param name User-defined name assigned to the translation memory.
   * @param sourceLanguage Source language code for the translation memory.
   * @param targetLanguages List of target language codes for the translation memory.
   * @param segmentCount Number of segments in the translation memory.
   * @param creationTime Timestamp when the translation memory was created, if provided by the API.
   * @param updatedTime Timestamp when the translation memory was last updated, if provided by the
   *     API.
   */
  public TranslationMemoryInfo(
      String translationMemoryId,
      String name,
      String sourceLanguage,
      List<String> targetLanguages,
      int segmentCount,
      @Nullable Date creationTime,
      @Nullable Date updatedTime) {
    this.translationMemoryId = translationMemoryId;
    this.name = name;
    this.sourceLanguage = sourceLanguage;
    this.targetLanguages = targetLanguages;
    this.segmentCount = segmentCount;
    this.creationTime = creationTime;
    this.updatedTime = updatedTime;
  }

  /** @return Unique ID assigned to the translation memory. */
  public String getTranslationMemoryId() {
    return translationMemoryId;
  }

  /** @return User-defined name assigned to the translation memory. */
  public String getName() {
    return name;
  }

  /** @return Source language code for the translation memory. */
  public String getSourceLanguage() {
    return sourceLanguage;
  }

  /** @return List of target language codes for the translation memory. */
  public List<String> getTargetLanguages() {
    return targetLanguages;
  }

  /** @return Number of segments in the translation memory. */
  public int getSegmentCount() {
    return segmentCount;
  }

  /**
   * @return Timestamp when the translation memory was created, or <code>null</code> if not provided
   *     by the API.
   */
  public @Nullable Date getCreationTime() {
    return creationTime;
  }

  /**
   * @return Timestamp when the translation memory was last updated, or <code>null</code> if not
   *     provided by the API.
   */
  public @Nullable Date getUpdatedTime() {
    return updatedTime;
  }

  @Override
  public String toString() {
    return "TranslationMemoryInfo{"
        + "translationMemoryId='"
        + translationMemoryId
        + '\''
        + ", name='"
        + name
        + '\''
        + ", sourceLanguage='"
        + sourceLanguage
        + '\''
        + ", targetLanguages="
        + targetLanguages
        + ", segmentCount="
        + segmentCount
        + '}';
  }
}
