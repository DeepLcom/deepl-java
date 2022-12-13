// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.parsing;

import com.deepl.api.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.lang.reflect.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * Parsing functions for responses from the DeepL API.
 *
 * <p>This class is internal; you should not use this class directly.
 */
public class Parser {
  private final Gson gson;

  public Parser() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(TextResult.class, new TextResultDeserializer());
    gsonBuilder.registerTypeAdapter(Language.class, new LanguageDeserializer());
    gsonBuilder.registerTypeAdapter(Usage.class, new UsageDeserializer());
    gson = gsonBuilder.create();
  }

  public List<TextResult> parseTextResult(String json) {
    TextResponse result = gson.fromJson(json, TextResponse.class);
    return result.translations;
  }

  public Usage parseUsage(String json) {
    return gson.fromJson(json, Usage.class);
  }

  public List<Language> parseLanguages(String json) {
    Type languageListType = new TypeToken<ArrayList<Language>>() {}.getType();
    return gson.fromJson(json, languageListType);
  }

  public List<GlossaryLanguagePair> parseGlossaryLanguageList(String json) {
    return gson.fromJson(json, GlossaryLanguagesResponse.class).getSupportedLanguages();
  }

  public DocumentStatus parseDocumentStatus(String json) {
    return gson.fromJson(json, DocumentStatus.class);
  }

  public DocumentHandle parseDocumentHandle(String json) {
    return gson.fromJson(json, DocumentHandle.class);
  }

  public GlossaryInfo parseGlossaryInfo(String json) {
    return gson.fromJson(json, GlossaryInfo.class);
  }

  public List<GlossaryInfo> parseGlossaryInfoList(String json) {
    GlossaryListResponse result = gson.fromJson(json, GlossaryListResponse.class);
    return result.getGlossaries();
  }

  public String parseErrorMessage(String json) {
    ErrorResponse response = gson.fromJson(json, ErrorResponse.class);

    if (response != null) {
      return response.getErrorMessage();
    } else {
      return "";
    }
  }

  static @Nullable Integer getAsIntOrNull(JsonObject jsonObject, String parameterName) {
    if (!jsonObject.has(parameterName)) return null;
    return jsonObject.get(parameterName).getAsInt();
  }

  static @Nullable String getAsStringOrNull(JsonObject jsonObject, String parameterName) {
    if (!jsonObject.has(parameterName)) return null;
    return jsonObject.get(parameterName).getAsString();
  }

  static @Nullable Boolean getAsBooleanOrNull(JsonObject jsonObject, String parameterName) {
    if (!jsonObject.has(parameterName)) return null;
    return jsonObject.get(parameterName).getAsBoolean();
  }
}
