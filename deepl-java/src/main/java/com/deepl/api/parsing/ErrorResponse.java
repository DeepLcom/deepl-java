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
  @Nullable String message;
  @Nullable String detail;

  public String getErrorMessage() {
    StringBuilder sb = new StringBuilder();
    if (message != null) sb.append("message: ").append(message);
    if (detail != null) {
      if (sb.length() != 0) sb.append(", ");
      sb.append("detail: ").append(detail);
    }
    return sb.toString();
  }
}
