// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.List;
import org.junit.jupiter.api.*;

public class StyleRuleTest extends TestBase {
  private static final String DEFAULT_STYLE_ID = "dca2e053-8ae5-45e6-a0d2-881156e7f4e4";

  @Test
  void testGetAllStyleRules() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<StyleRuleInfo> styleRules = client.getAllStyleRules(0, 10, true);

    Assertions.assertNotNull(styleRules);
    Assertions.assertFalse(styleRules.isEmpty());
    Assertions.assertEquals(DEFAULT_STYLE_ID, styleRules.get(0).getStyleId());
    Assertions.assertEquals("Default Style Rule", styleRules.get(0).getName());
    Assertions.assertNotNull(styleRules.get(0).getCreationTime());
    Assertions.assertNotNull(styleRules.get(0).getUpdatedTime());
    Assertions.assertEquals("en", styleRules.get(0).getLanguage());
    Assertions.assertEquals(1, styleRules.get(0).getVersion());
    Assertions.assertNotNull(styleRules.get(0).getConfiguredRules());
    Assertions.assertNotNull(styleRules.get(0).getCustomInstructions());
  }

  @Test
  void testGetAllStyleRulesWithoutDetailed() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<StyleRuleInfo> styleRules = client.getAllStyleRules();

    Assertions.assertNotNull(styleRules);
    Assertions.assertFalse(styleRules.isEmpty());
    Assertions.assertEquals(DEFAULT_STYLE_ID, styleRules.get(0).getStyleId());
    Assertions.assertNull(styleRules.get(0).getConfiguredRules());
    Assertions.assertNull(styleRules.get(0).getCustomInstructions());
  }

  @Test
  void testTranslateTextWithStyleId() throws Exception {
    // Note: this test may use the mock server that will not translate the text
    // with a style rule, therefore we do not check the translated result.
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(
            text, "de", "en-US", new TextTranslationOptions().setStyleId(DEFAULT_STYLE_ID));

    Assertions.assertNotNull(result);
  }

  @Test
  void testTranslateTextWithStyleRuleInfo() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<StyleRuleInfo> styleRules = client.getAllStyleRules();
    StyleRuleInfo rule = styleRules.get(0);
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(text, "de", "en-US", new TextTranslationOptions().setStyleRule(rule));

    Assertions.assertNotNull(result);
  }
}
