// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.TranslationMemoryInfo;
import java.util.List;

/**
 * Class representing v3 translation_memories list response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class TranslationMemoryListResponse {
  private List<TranslationMemoryInfo> translation_memories;

  public List<TranslationMemoryInfo> getTranslationMemories() {
    return translation_memories;
  }
}
