// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** A source segment of a translation memory and its translations. */
public class TranslationMemorySegment {
  @SerializedName(value = "source_segment_id")
  private final String sourceSegmentId;

  @SerializedName(value = "source_text")
  private final String sourceText;

  @SerializedName(value = "targets")
  private final List<TranslationMemoryTargetSegment> targets;

  @SerializedName(value = "creation_time")
  private final @Nullable Date creationTime;

  @SerializedName(value = "updated_time")
  private final @Nullable Date updatedTime;

  @SerializedName(value = "last_used_time")
  private final @Nullable Date lastUsedTime;

  /**
   * Initializes a new {@link TranslationMemorySegment} holding a source text and its translations.
   *
   * @param sourceSegmentId Unique ID assigned to the source segment.
   * @param sourceText The source text.
   * @param targets Translations of the source text, one per target language.
   * @param creationTime Timestamp when the source segment was created, if provided by the API.
   * @param updatedTime Timestamp when the source segment was last updated, if provided by the API.
   * @param lastUsedTime Timestamp when the source segment was last used, if provided by the API.
   */
  public TranslationMemorySegment(
      String sourceSegmentId,
      String sourceText,
      List<TranslationMemoryTargetSegment> targets,
      @Nullable Date creationTime,
      @Nullable Date updatedTime,
      @Nullable Date lastUsedTime) {
    this.sourceSegmentId = sourceSegmentId;
    this.sourceText = sourceText;
    this.targets = targets;
    this.creationTime = creationTime;
    this.updatedTime = updatedTime;
    this.lastUsedTime = lastUsedTime;
  }

  /** @return Unique ID assigned to the source segment. */
  public String getSourceSegmentId() {
    return sourceSegmentId;
  }

  /** @return The source text. */
  public String getSourceText() {
    return sourceText;
  }

  /** @return Translations of the source text, one per target language. */
  public List<TranslationMemoryTargetSegment> getTargets() {
    return targets;
  }

  /**
   * @return Timestamp when the source segment was created, or <code>null</code> if not provided by
   *     the API.
   */
  public @Nullable Date getCreationTime() {
    return creationTime;
  }

  /**
   * @return Timestamp when the source segment was last updated, or <code>null</code> if not
   *     provided by the API.
   */
  public @Nullable Date getUpdatedTime() {
    return updatedTime;
  }

  /**
   * @return Timestamp when the source segment was last used, or <code>null</code> if not provided
   *     by the API.
   */
  public @Nullable Date getLastUsedTime() {
    return lastUsedTime;
  }

  @Override
  public String toString() {
    return "TranslationMemorySegment{"
        + "sourceSegmentId='"
        + sourceSegmentId
        + '\''
        + ", sourceText='"
        + sourceText
        + '\''
        + ", targets="
        + targets
        + '}';
  }
}
