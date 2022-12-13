// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.TextResult;
import java.util.List;

/**
 * Class representing text translation responses from the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class TextResponse {
  public List<TextResult> translations;
}
