// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.StyleRuleInfo;
import java.util.List;

/**
 * Class representing v3 style_rules list response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class StyleRuleListResponse {
  private List<StyleRuleInfo> style_rules;

  public List<StyleRuleInfo> getStyleRules() {
    return style_rules;
  }
}
