// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;

/** Information about a glossary, excluding the entry list. */
public class MultilingualGlossaryInfo implements IGlossary {
  @SerializedName(value = "glossary_id")
  private final String glossaryId;

  @SerializedName(value = "name")
  private final String name;

  @SerializedName(value = "creation_time")
  private final Date creationTime;

  @SerializedName(value = "dictionaries")
  private final List<MultilingualGlossaryDictionaryInfo> dictionaries;

  /**
   * Initializes a new {@link MultilingualGlossaryInfo} containing information about a glossary.
   *
   * @param glossaryId ID of the associated glossary.
   * @param name Name of the glossary chosen during creation.
   * @param creationTime Time when the glossary was created.
   * @param dictionaries A list of dictionaries that are in this glossary
   */
  public MultilingualGlossaryInfo(
      String glossaryId,
      String name,
      Date creationTime,
      List<MultilingualGlossaryDictionaryInfo> dictionaries) {
    this.glossaryId = glossaryId;
    this.name = name;
    this.creationTime = creationTime;
    this.dictionaries = dictionaries;
  }

  /** @return Unique ID assigned to the glossary. */
  public String getGlossaryId() {
    return glossaryId;
  }

  /** @return User-defined name assigned to the glossary. */
  public String getName() {
    return name;
  }

  /** @return Timestamp when the glossary was created. */
  public Date getCreationTime() {
    return creationTime;
  }

  /** @return the list of dictionaries in this glossary */
  public List<MultilingualGlossaryDictionaryInfo> getDictionaries() {
    return dictionaries;
  }
}
