// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.Language;
import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Utility class for deserializing language codes returned by the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
class LanguageDeserializer implements JsonDeserializer<Language> {
  public Language deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    Boolean supportsFormality = Parser.getAsBooleanOrNull(jsonObject, "supports_formality");
    return new Language(
        jsonObject.get("name").getAsString(),
        jsonObject.get("language").getAsString(),
        supportsFormality);
  }
}
