// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

public enum DeepLApiVersion {
  VERSION_1("v1"),
  VERSION_2("v2");

  /**
   * How the version is represented in the URL string. Does not include any slashes (/). Example:
   * "v2"
   */
  private final String urlRepresentation;

  private DeepLApiVersion(String urlRepresentation) {
    this.urlRepresentation = urlRepresentation;
  }

  public String toString() {
    return this.urlRepresentation;
  }
}
