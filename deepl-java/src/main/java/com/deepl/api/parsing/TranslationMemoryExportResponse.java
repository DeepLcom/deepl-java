// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

/**
 * Class representing v3 translation memory export response by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class TranslationMemoryExportResponse {
  private String job_id;
  private Parameters parameters;

  static class Parameters {
    private String translation_memory_id;
  }

  public String getJobId() {
    return job_id;
  }

  public String getTranslationMemoryId() {
    return parameters == null ? null : parameters.translation_memory_id;
  }
}
