// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.*;

public class GlossaryCleanupUtility implements AutoCloseable {
  private final String glossaryName;
  private final Translator translator;

  public GlossaryCleanupUtility(Translator translator) {
    this(translator, "");
  }

  public GlossaryCleanupUtility(Translator translator, String testNameSuffix) {
    String callingFunc = getCallerFunction();
    String uuid = UUID.randomUUID().toString();

    this.glossaryName =
        String.format("deepl-java-test-glossary: %s%s %s", callingFunc, testNameSuffix, uuid);
    this.translator = translator;
  }

  public String getGlossaryName() {
    return glossaryName;
  }

  @Override
  public void close() throws Exception {
    List<GlossaryInfo> glossaries = translator.listGlossaries();
    for (GlossaryInfo glossary : glossaries) {
      if (Objects.equals(glossary.getName(), glossaryName)) {
        try {
          translator.deleteGlossary(glossary);
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
      if (!stacktrace[i].getClassName().equals(GlossaryCleanupUtility.class.getName())
          && stacktrace[i - 1].getClassName().equals(GlossaryCleanupUtility.class.getName())) {
        return stacktrace[i].getMethodName();
      }
    }
    return "unknown";
  }
}
