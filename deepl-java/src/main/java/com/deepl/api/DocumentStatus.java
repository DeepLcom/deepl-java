// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/** Status of an in-progress document translation. */
public class DocumentStatus {
  @SerializedName(value = "document_id")
  private final String documentId;

  @SerializedName(value = "status")
  private final StatusCode status;

  @SerializedName(value = "billed_characters")
  private final @Nullable Long billedCharacters;

  @SerializedName(value = "seconds_remaining")
  private final @Nullable Long secondsRemaining;

  @SerializedName(value = "error_message")
  private final @Nullable String errorMessage;

  /** Status code indicating status of the document translation. */
  public enum StatusCode {
    /** Document translation has not yet started, but will begin soon. */
    @SerializedName("queued")
    Queued,
    /** Document translation is in progress. */
    @SerializedName("translating")
    Translating,
    /**
     * Document translation completed successfully, and the translated document may be downloaded.
     */
    @SerializedName("done")
    Done,
    /** An error occurred during document translation. */
    @SerializedName("error")
    Error,
  }

  public DocumentStatus(
      String documentId,
      StatusCode status,
      @Nullable Long billedCharacters,
      @Nullable Long secondsRemaining,
      @Nullable String errorMessage) {
    this.documentId = documentId;
    this.status = status;
    this.billedCharacters = billedCharacters;
    this.secondsRemaining = secondsRemaining;
    this.errorMessage = errorMessage;
  }

  /** @return Document ID of the associated document. */
  public String getDocumentId() {
    return documentId;
  }

  /** @return Status of the document translation. */
  public StatusCode getStatus() {
    return status;
  }

  /**
   * @return <code>true</code> if no error has occurred during document translation, otherwise
   *     <code>false</code>.
   */
  public boolean ok() {
    return status != null && status != StatusCode.Error;
  }

  /**
   * @return <code>true</code> if document translation has completed successfully, otherwise <code>
   *     false</code>.
   */
  public boolean done() {
    return status != null && status == StatusCode.Done;
  }

  /**
   * @return Number of seconds remaining until translation is complete if available, otherwise
   *     <code>null</code>. Only available while document is in translating state.
   */
  public @Nullable Long getSecondsRemaining() {
    return secondsRemaining;
  }

  /**
   * @return Number of characters billed for the translation of this document if available,
   *     otherwise <code>null</code>. Only available after document translation is finished and the
   *     status is {@link StatusCode#Done}, otherwise <code>null</code>.
   */
  public @Nullable Long getBilledCharacters() {
    return billedCharacters;
  }

  /** @return Short description of the error if available, otherwise <code>null</code>. */
  public @Nullable String getErrorMessage() {
    return errorMessage;
  }
}
