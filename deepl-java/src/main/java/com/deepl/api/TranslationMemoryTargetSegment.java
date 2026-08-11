// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** A target-language translation attached to a source segment of a translation memory. */
public class TranslationMemoryTargetSegment {
  @SerializedName(value = "target_segment_id")
  private final String targetSegmentId;

  @SerializedName(value = "target_language")
  private final String targetLanguage;

  @SerializedName(value = "target_text")
  private final String targetText;

  @SerializedName(value = "creation_time")
  private final @Nullable Date creationTime;

  @SerializedName(value = "updated_time")
  private final @Nullable Date updatedTime;

  @SerializedName(value = "last_used_time")
  private final @Nullable Date lastUsedTime;

  /**
   * Initializes a new {@link TranslationMemoryTargetSegment} holding one translation of a source
   * segment.
   *
   * @param targetSegmentId Unique ID assigned to the target segment.
   * @param targetLanguage Target language code of the translation.
   * @param targetText The translated text.
   * @param creationTime Timestamp when the target segment was created, if provided by the API.
   * @param updatedTime Timestamp when the target segment was last updated, if provided by the API.
   * @param lastUsedTime Timestamp when the target segment was last used, if provided by the API.
   */
  public TranslationMemoryTargetSegment(
      String targetSegmentId,
      String targetLanguage,
      String targetText,
      @Nullable Date creationTime,
      @Nullable Date updatedTime,
      @Nullable Date lastUsedTime) {
    this.targetSegmentId = targetSegmentId;
    this.targetLanguage = targetLanguage;
    this.targetText = targetText;
    this.creationTime = creationTime;
    this.updatedTime = updatedTime;
    this.lastUsedTime = lastUsedTime;
  }

  /** @return Unique ID assigned to the target segment. */
  public String getTargetSegmentId() {
    return targetSegmentId;
  }

  /** @return Target language code of the translation. */
  public String getTargetLanguage() {
    return targetLanguage;
  }

  /** @return The translated text. */
  public String getTargetText() {
    return targetText;
  }

  /**
   * @return Timestamp when the target segment was created, or <code>null</code> if not provided by
   *     the API.
   */
  public @Nullable Date getCreationTime() {
    return creationTime;
  }

  /**
   * @return Timestamp when the target segment was last updated, or <code>null</code> if not
   *     provided by the API.
   */
  public @Nullable Date getUpdatedTime() {
    return updatedTime;
  }

  /**
   * @return Timestamp when the target segment was last used, or <code>null</code> if not provided
   *     by the API.
   */
  public @Nullable Date getLastUsedTime() {
    return lastUsedTime;
  }

  @Override
  public String toString() {
    return targetLanguage + ": " + targetText;
  }
}
