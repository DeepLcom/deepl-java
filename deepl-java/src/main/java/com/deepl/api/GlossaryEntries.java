// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.deepl.api.utils.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** Stores the entries of a glossary. */
public class GlossaryEntries implements Map<String, String> {
  private final Map<String, String> entries = new HashMap<>();

  /** Construct an empty GlossaryEntries. */
  public GlossaryEntries() {}

  /** Initializes a new GlossaryEntries with the entry pairs in the given map. */
  public GlossaryEntries(Map<String, String> entryPairs) {
    this.putAll(entryPairs);
  }

  /**
   * Converts the given tab-separated-value (TSV) string of glossary entries into a new
   * GlossaryEntries object. Whitespace is trimmed from the start and end of each term.
   */
  public static GlossaryEntries fromTsv(String tsv) {
    GlossaryEntries result = new GlossaryEntries();
    String[] lines = tsv.split("(\\r\\n|\\n|\\r)");
    int lineNumber = 0;
    for (String line : lines) {
      ++lineNumber;
      String lineTrimmed = trimWhitespace(line);
      if (lineTrimmed.isEmpty()) {
        continue;
      }
      String[] splitLine = lineTrimmed.split("\\t");
      if (splitLine.length < 2) {
        throw new IllegalArgumentException(
            String.format(
                "Entry on line %d does not contain a term separator: %s", lineNumber, lineTrimmed));
      } else if (splitLine.length > 2) {
        throw new IllegalArgumentException(
            String.format(
                "Entry on line %d contains more than one term separator: %s", lineNumber, line));
      } else {
        String sourceTerm = trimWhitespace(splitLine[0]);
        String targetTerm = trimWhitespace(splitLine[1]);
        validateGlossaryTerm(sourceTerm);
        validateGlossaryTerm(targetTerm);
        if (result.containsKey(sourceTerm)) {
          throw new IllegalArgumentException(
              String.format(
                  "Entry on line %d duplicates source term '%s'", lineNumber, sourceTerm));
        }
        result.put(sourceTerm, targetTerm);
      }
    }

    if (result.entries.isEmpty()) {
      throw new IllegalArgumentException("TSV string contains no valid entries");
    }

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;
    GlossaryEntries glossaryEntries = (GlossaryEntries) o;
    return glossaryEntries.entries.equals(entries);
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public boolean isEmpty() {
    return entries.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return entries.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return entries.containsValue(value);
  }

  @Override
  public String get(Object key) {
    return entries.get(key);
  }

  /**
   * Adds the given source term and target term to the glossary entries.
   *
   * @param sourceTerm key with which the specified value is to be associated
   * @param targetTerm value to be associated with the specified key
   * @return The previous target term associated with this source term, or null if this source term
   *     was not present.
   */
  public String put(String sourceTerm, String targetTerm) throws IllegalArgumentException {
    validateGlossaryTerm(sourceTerm);
    validateGlossaryTerm(targetTerm);
    return entries.put(sourceTerm, targetTerm);
  }

  @Override
  public String remove(Object key) {
    return entries.remove(key);
  }

  @Override
  public void putAll(@NotNull Map<? extends String, ? extends String> m) {
    for (Map.Entry<? extends String, ? extends String> entryPair : m.entrySet()) {
      put(entryPair.getKey(), entryPair.getValue());
    }
  }

  @Override
  public void clear() {
    entries.clear();
  }

  @NotNull
  @Override
  public Set<String> keySet() {
    return entries.keySet();
  }

  @NotNull
  @Override
  public Collection<String> values() {
    return entries.values();
  }

  @NotNull
  @Override
  public Set<Entry<String, String>> entrySet() {
    return entries.entrySet();
  }

  /**
   * Checks the validity of the given glossary term, for example that it contains no invalid
   * characters. Whitespace at the start and end of the term is ignored. Terms are considered valid
   * if they comprise at least one non-whitespace character, and contain no invalid characters: C0
   * and C1 control characters, and Unicode newlines.
   *
   * @param term String containing term to check.
   */
  public static void validateGlossaryTerm(String term) throws IllegalArgumentException {
    String termTrimmed = trimWhitespace(term);
    if (termTrimmed.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Term '%s' contains no non-whitespace characters", term));
    }
    for (int i = 0; i < termTrimmed.length(); ++i) {
      char ch = termTrimmed.charAt(i);
      if ((ch <= 31) || (128 <= ch && ch <= 159) || ch == '\u2028' || ch == '\u2029') {
        throw new IllegalArgumentException(
            String.format(
                "Term '%s' contains invalid character: '%c' (U+%04d)", term, ch, (int) ch));
      }
    }
  }

  /**
   * Converts the glossary entries to a string containing the entries in tab-separated-value (TSV)
   * format.
   *
   * @return String containing the entries in TSV format.
   */
  public String toTsv() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entryPair : entries.entrySet()) {
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(entryPair.getKey()).append("\t").append(entryPair.getValue());
    }
    return builder.toString();
  }

  /**
   * Strips whitespace characters from the beginning and end of the given string. Implemented here
   * because String.strip() is not available in Java 8.
   *
   * @param input String to have whitespace trimmed.
   * @return Input string with whitespace removed from ends.
   */
  private static String trimWhitespace(String input) {
    int left = 0;
    for (; left < input.length(); left++) {
      char ch = input.charAt(left);
      if (ch != ' ' && ch != '\t') {
        break;
      }
    }
    if (left >= input.length()) {
      return "";
    }
    int right = input.length() - 1;
    for (; left < right; right--) {
      char ch = input.charAt(right);
      if (ch != ' ' && ch != '\t') {
        break;
      }
    }
    return input.substring(left, right + 1);
  }
}
