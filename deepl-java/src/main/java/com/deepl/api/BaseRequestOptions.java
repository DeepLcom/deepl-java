// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.Map;

/** Base class for request options providing common functionality for all endpoints. */
public abstract class BaseRequestOptions {
  private Map<String, String> extraBodyParameters;

  /**
   * Sets additional parameters to pass in the body of the HTTP request. Can be used to access beta
   * features, override built-in parameters, or for testing purposes. Keys in this map will be added
   * to the request body and can override existing keys.
   *
   * @param extraBodyParameters Map of additional parameters to include in the request.
   * @return This options object for method chaining.
   */
  public BaseRequestOptions setExtraBodyParameters(Map<String, String> extraBodyParameters) {
    this.extraBodyParameters = extraBodyParameters;
    return this;
  }

  /** Gets the current extra body parameters. */
  public Map<String, String> getExtraBodyParameters() {
    return extraBodyParameters;
  }
}
