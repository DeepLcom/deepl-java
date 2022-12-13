// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import org.jetbrains.annotations.Nullable;

/**
 * Class representing error messages returned by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class ErrorResponse {
  @Nullable private String message;
  @Nullable private String detail;

  /** Returns a diagnostic string including the message and detail (if available). */
  public String getErrorMessage() {
    StringBuilder sb = new StringBuilder();
    if (getMessage() != null) sb.append("message: ").append(getMessage());
    if (getDetail() != null) {
      if (sb.length() != 0) sb.append(", ");
      sb.append("detail: ").append(getDetail());
    }
    return sb.toString();
  }

  public @Nullable String getMessage() {
    return message;
  }

  public @Nullable String getDetail() {
    return detail;
  }
}
