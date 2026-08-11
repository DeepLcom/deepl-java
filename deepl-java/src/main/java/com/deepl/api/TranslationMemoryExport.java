// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import org.jetbrains.annotations.*;

/** A translation memory export job. */
public class TranslationMemoryExport {
  private final String jobId;
  private final @Nullable String translationMemoryId;
  private final boolean reusedExisting;

  /**
   * Initializes a new {@link TranslationMemoryExport} describing a created export job.
   *
   * @param jobId Unique ID assigned to the export job.
   * @param translationMemoryId ID of the translation memory being exported, if provided by the API.
   * @param reusedExisting <code>true</code> if the API reused a previously completed export instead
   *     of starting a new one.
   */
  public TranslationMemoryExport(
      String jobId, @Nullable String translationMemoryId, boolean reusedExisting) {
    this.jobId = jobId;
    this.translationMemoryId = translationMemoryId;
    this.reusedExisting = reusedExisting;
  }

  /** @return Unique ID assigned to the export job. */
  public String getJobId() {
    return jobId;
  }

  /**
   * @return ID of the translation memory being exported, or <code>null</code> if not provided by
   *     the API.
   */
  public @Nullable String getTranslationMemoryId() {
    return translationMemoryId;
  }

  /**
   * @return <code>true</code> if the API answered with a previously completed export of the same
   *     translation memory (HTTP 200), or <code>false</code> if it started a new one (HTTP 202).
   */
  public boolean isReusedExisting() {
    return reusedExisting;
  }
}
