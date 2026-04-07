// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.List;
import org.junit.jupiter.api.*;

public class TranslationMemoryTest extends TestBase {
  private static final String DEFAULT_TM_ID = "a74d88fb-ed2a-4943-a664-a4512398b994";

  @Test
  void testListTranslationMemories() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<TranslationMemoryInfo> translationMemories = client.listTranslationMemories(0, 10);

    Assertions.assertNotNull(translationMemories);
    Assertions.assertFalse(translationMemories.isEmpty());
    Assertions.assertNotNull(translationMemories.get(0).getTranslationMemoryId());
    Assertions.assertNotNull(translationMemories.get(0).getName());
    Assertions.assertNotNull(translationMemories.get(0).getSourceLanguage());
    Assertions.assertNotNull(translationMemories.get(0).getTargetLanguages());
  }

  @Test
  void testTranslateTextWithTranslationMemoryId() throws Exception {
    // Note: this test may use the mock server that will not translate the text
    // with a translation memory, therefore we do not check the translated result.
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(
            text,
            "de",
            "en-US",
            new TextTranslationOptions().setTranslationMemoryId(DEFAULT_TM_ID));

    Assertions.assertNotNull(result);
  }

  @Test
  void testTranslateTextWithTranslationMemoryIdAndThreshold() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(
            text,
            "de",
            "en-US",
            new TextTranslationOptions()
                .setTranslationMemoryId(DEFAULT_TM_ID)
                .setTranslationMemoryThreshold(80));

    Assertions.assertNotNull(result);
  }
}
