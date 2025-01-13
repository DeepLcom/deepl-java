// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Represents the style the improved text should be in in a rephrase request. */
public enum WritingStyle {
  Academic("academic"),
  Business("business"),
  Casual("casual"),
  Default("default"),
  PreferAcademic("prefer_academic"),
  PreferBusiness("prefer_business"),
  PreferCasual("prefer_casual"),
  PreferSimple("prefer_simple"),
  Simple("simple");

  private final String value;

  WritingStyle(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
