// Copyright 2023 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

public class AppInfo {
  private String appName;
  private String appVersion;

  public AppInfo(String appName, String appVersion) {
    this.appName = appName;
    this.appVersion = appVersion;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppVersion() {
    return appVersion;
  }
}
