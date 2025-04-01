// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.MultilingualGlossaryInfo;
import java.util.List;

/**
 * Class representing v3 list-glossaries response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class MultilingualGlossaryListResponse {
  private List<MultilingualGlossaryInfo> glossaries;

  public List<MultilingualGlossaryInfo> getGlossaries() {
    return glossaries;
  }
}
