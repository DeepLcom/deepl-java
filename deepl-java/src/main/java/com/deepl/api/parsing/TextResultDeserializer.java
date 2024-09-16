// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.TextResult;
import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Utility class for deserializing text translation results returned by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class TextResultDeserializer implements JsonDeserializer<TextResult> {
  public TextResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    return new TextResult(
        jsonObject.get("text").getAsString(),
        jsonObject.get("detected_source_language").getAsString(),
        jsonObject.get("billed_characters").getAsInt());
  }
}
