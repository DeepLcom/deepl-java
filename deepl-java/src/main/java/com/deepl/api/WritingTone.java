// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/** Represents the tone the improved text should be in in a rephrase request. */
public enum WritingTone {
  Confident("confident"),
  Default("default"),
  Diplomatic("diplomatic"),
  Enthusiastic("enthusiastic"),
  Friendly("friendly"),
  PreferConfident("prefer_confident"),
  PreferDiplomatic("prefer_diplomatic"),
  PreferEnthusiastic("prefer_enthusiastic"),
  PreferFriendly("prefer_friendly");

  private final String value;

  WritingTone(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
