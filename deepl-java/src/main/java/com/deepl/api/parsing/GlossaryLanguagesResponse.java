// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.*;
import com.google.gson.annotations.*;
import java.util.List;

/**
 * Class representing glossary-languages response from the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class GlossaryLanguagesResponse {
  @SerializedName("supported_languages")
  private List<GlossaryLanguagePair> supportedLanguages;

  public List<GlossaryLanguagePair> getSupportedLanguages() {
    return supportedLanguages;
  }
}
