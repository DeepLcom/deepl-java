// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import org.jetbrains.annotations.*;

/** Custom instruction for a style rule. */
public class CustomInstruction {
  @SerializedName(value = "label")
  private final String label;

  @SerializedName(value = "prompt")
  private final String prompt;

  @SerializedName(value = "source_language")
  @Nullable
  private final String sourceLanguage;

  /**
   * Initializes a new {@link CustomInstruction} containing a custom instruction for a style rule.
   *
   * @param label Label for the custom instruction.
   * @param prompt Prompt text for the custom instruction.
   * @param sourceLanguage Optional source language code for the custom instruction.
   */
  public CustomInstruction(String label, String prompt, @Nullable String sourceLanguage) {
    this.label = label;
    this.prompt = prompt;
    this.sourceLanguage = sourceLanguage;
  }

  /** @return Label for the custom instruction. */
  public String getLabel() {
    return label;
  }

  /** @return Prompt text for the custom instruction. */
  public String getPrompt() {
    return prompt;
  }

  /** @return Optional source language code for the custom instruction. */
  @Nullable
  public String getSourceLanguage() {
    return sourceLanguage;
  }
}
