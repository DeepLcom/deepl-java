// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.*;
import com.google.gson.*;
import java.lang.reflect.*;
import org.jetbrains.annotations.*;

/**
 * Class representing usage responses from the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class UsageDeserializer implements JsonDeserializer<Usage> {
  public Usage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    return new Usage(
        createDetail(jsonObject, "character_"),
        createDetail(jsonObject, "document_"),
        createDetail(jsonObject, "team_document_"));
  }

  public static @Nullable Usage.Detail createDetail(JsonObject jsonObject, String prefix) {
    Long count = Parser.getAsLongOrNull(jsonObject, prefix + "count");
    Long limit = Parser.getAsLongOrNull(jsonObject, prefix + "limit");
    if (count == null || limit == null) return null;
    return new Usage.Detail(count, limit);
  }
}
