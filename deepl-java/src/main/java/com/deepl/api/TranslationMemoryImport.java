// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * A newly created translation memory import job.
 *
 * <p>The TMX file must be uploaded to the upload URL before it expires; processing starts
 * automatically once the upload is detected.
 */
public class TranslationMemoryImport {
  @SerializedName(value = "job_id")
  private final String jobId;

  @SerializedName(value = "upload_url")
  private final String uploadUrl;

  @SerializedName(value = "expires_at")
  private final @Nullable Date expiresAt;

  /**
   * Initializes a new {@link TranslationMemoryImport} describing a created import job.
   *
   * @param jobId Unique ID assigned to the import job.
   * @param uploadUrl URL to upload the TMX file to.
   * @param expiresAt Timestamp after which the upload URL is no longer valid, if provided by the
   *     API.
   */
  public TranslationMemoryImport(String jobId, String uploadUrl, @Nullable Date expiresAt) {
    this.jobId = jobId;
    this.uploadUrl = uploadUrl;
    this.expiresAt = expiresAt;
  }

  /** @return Unique ID assigned to the import job. */
  public String getJobId() {
    return jobId;
  }

  /** @return URL to upload the TMX file to. */
  public String getUploadUrl() {
    return uploadUrl;
  }

  /**
   * @return Timestamp after which the upload URL is no longer valid, or <code>null</code> if not
   *     provided by the API.
   */
  public @Nullable Date getExpiresAt() {
    return expiresAt;
  }
}
