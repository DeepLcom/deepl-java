// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.WriteResult;
import java.util.List;

/**
 * Class representing text rephrase responses from the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class WriteResponse {
  public List<WriteResult> improvements;
}
