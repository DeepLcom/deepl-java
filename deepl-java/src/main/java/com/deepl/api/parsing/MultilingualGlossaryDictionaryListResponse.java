// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import java.util.List;

/**
 * Class representing v3 list-glossaries response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
public class MultilingualGlossaryDictionaryListResponse {
  private List<MultilingualGlossaryDictionaryEntriesResponse> dictionaries;

  public List<MultilingualGlossaryDictionaryEntriesResponse> getDictionaries() {
    return dictionaries;
  }
}
