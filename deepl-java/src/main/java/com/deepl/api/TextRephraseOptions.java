// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/**
 * Options to control text rephrasing behaviour. These options may be provided to {@link
 * DeepLClient#rephraseText} overloads.
 *
 * <p>All properties have corresponding setters in fluent-style, so the following is possible:
 * <code>
 *      TextRephraseOptions options = new TextRephraseOptions()
 *          .WritingStyle(WritingStyle.Business.getValue());
 * </code>
 */
public class TextRephraseOptions {
  private String writingStyle;
  private String tone;

  /**
   * Sets a style the improved text should be in. Note that only style OR tone can be set.
   *
   * @see WritingStyle
   */
  public TextRephraseOptions setWritingStyle(String style) {
    this.writingStyle = style;
    return this;
  }

  /**
   * Sets a tone the improved text should be in. Note that only style OR tone can be set.
   *
   * @see WritingTone
   */
  public TextRephraseOptions setTone(String tone) {
    this.tone = tone;
    return this;
  }

  /** Gets the current style setting. */
  public String getWritingStyle() {
    return writingStyle;
  }

  /** Gets the current tone setting. */
  public String getTone() {
    return tone;
  }
}
