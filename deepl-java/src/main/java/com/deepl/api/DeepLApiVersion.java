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
