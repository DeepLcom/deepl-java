// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.utils;

import java.util.AbstractMap;

public class KeyValuePair<K, V> extends AbstractMap.SimpleEntry<K, V> {

  public KeyValuePair(K key, V value) {
    super(key, value);
  }
}
