// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  void testStyleRuleCrud() throws Exception {
    DeepLClient client = createDeepLClient();

    // Create
    StyleRuleInfo rule = client.createStyleRule("Test Rule", "en", null, null);
    Assertions.assertNotNull(rule.getStyleId());
    Assertions.assertEquals("Test Rule", rule.getName());

    String styleId = rule.getStyleId();

    // Get
    StyleRuleInfo retrieved = client.getStyleRule(styleId);
    Assertions.assertEquals(styleId, retrieved.getStyleId());

    // Update name
    StyleRuleInfo updated = client.updateStyleRuleName(styleId, "Updated Name");
    Assertions.assertEquals("Updated Name", updated.getName());

    // Update configured rules
    Map<String, String> datesAndTimes = new HashMap<>();
    datesAndTimes.put("calendar_era", "use_bc_and_ad");
    StyleRuleInfo configuredResult =
        client.updateStyleRuleConfiguredRules(
            styleId, new ConfiguredRules(datesAndTimes, null, null, null, null, null, null));
    Assertions.assertEquals(styleId, configuredResult.getStyleId());

    // Create custom instruction
    CustomInstruction instruction =
        client.createStyleRuleCustomInstruction(styleId, "Test Label", "Test prompt", null);
    Assertions.assertNotNull(instruction.getId());
    Assertions.assertEquals("Test Label", instruction.getLabel());

    String instructionId = instruction.getId();

    // Get custom instruction
    CustomInstruction retrievedInstruction =
        client.getStyleRuleCustomInstruction(styleId, instructionId);
    Assertions.assertEquals("Test Label", retrievedInstruction.getLabel());

    // Update custom instruction
    CustomInstruction updatedInstruction =
        client.updateStyleRuleCustomInstruction(
            styleId, instructionId, "Updated Label", "Updated prompt", null);
    Assertions.assertEquals("Updated Label", updatedInstruction.getLabel());

    // Delete custom instruction
    client.deleteStyleRuleCustomInstruction(styleId, instructionId);

    // Delete style rule
    client.deleteStyleRule(styleId);
  }

  @Test
  void testStyleRuleValidation() throws Exception {
    DeepLClient client = createDeepLClient();

    // createStyleRule
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.createStyleRule("", "en", null, null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.createStyleRule("Test", "", null, null));

    // getStyleRule
    Assertions.assertThrows(IllegalArgumentException.class, () -> client.getStyleRule(""));

    // updateStyleRuleName
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.updateStyleRuleName("", "New Name"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.updateStyleRuleName("some-id", ""));

    // deleteStyleRule
    Assertions.assertThrows(IllegalArgumentException.class, () -> client.deleteStyleRule(""));

    // updateStyleRuleConfiguredRules
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            client.updateStyleRuleConfiguredRules(
                "", new ConfiguredRules(null, null, null, null, null, null, null)));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.updateStyleRuleConfiguredRules("some-id", null));

    // createStyleRuleCustomInstruction
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.createStyleRuleCustomInstruction("", "L", "P", null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.createStyleRuleCustomInstruction("some-id", "", "P", null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.createStyleRuleCustomInstruction("some-id", "L", "", null));

    // getStyleRuleCustomInstruction
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.getStyleRuleCustomInstruction("", "instr-id"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.getStyleRuleCustomInstruction("some-id", ""));

    // updateStyleRuleCustomInstruction
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.updateStyleRuleCustomInstruction("", "instr-id", "L", "P", null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.updateStyleRuleCustomInstruction("some-id", "", "L", "P", null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.updateStyleRuleCustomInstruction("some-id", "instr-id", "", "P", null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.updateStyleRuleCustomInstruction("some-id", "instr-id", "L", "", null));

    // deleteStyleRuleCustomInstruction
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.deleteStyleRuleCustomInstruction("", "instr-id"));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.deleteStyleRuleCustomInstruction("some-id", ""));
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
