// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MultilingualGlossaryCleanupUtility implements AutoCloseable {
  private final String glossaryName;
  private final DeepLClient deepLClient;

  public MultilingualGlossaryCleanupUtility(DeepLClient deepLClient) {
    this(deepLClient, "");
  }

  public MultilingualGlossaryCleanupUtility(DeepLClient deepLClient, String testNameSuffix) {
    String callingFunc = getCallerFunction();
    String uuid = UUID.randomUUID().toString();

    this.glossaryName =
        String.format("deepl-java-test-glossary: %s%s %s", callingFunc, testNameSuffix, uuid);
    this.deepLClient = deepLClient;
  }

  public String getGlossaryName() {
    return glossaryName;
  }

  @Override
  public void close() throws Exception {
    List<MultilingualGlossaryInfo> glossaries = deepLClient.listMultilingualGlossaries();
    for (MultilingualGlossaryInfo glossary : glossaries) {
      if (Objects.equals(glossary.getName(), glossaryName)) {
        try {
          // TODO replace with v3 delete glossary
          deepLClient.deleteGlossary(glossary.getGlossaryId());
        } catch (Exception exception) {
          // Ignore
        }
      }
    }
  }

  private static String getCallerFunction() {
    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    // Find the first function outside this class following functions in this class
    for (int i = 1; i < stacktrace.length; i++) {
      if (!stacktrace[i].getClassName().equals(MultilingualGlossaryCleanupUtility.class.getName())
          && stacktrace[i - 1]
              .getClassName()
              .equals(MultilingualGlossaryCleanupUtility.class.getName())) {
        return stacktrace[i].getMethodName();
      }
    }
    return "unknown";
  }
}
