// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;

/**
 * Information about DeepL account usage for the current billing period, for example the number of
 * characters translated.
 *
 * <p>Depending on the account type, some usage types will be omitted. See the <a
 * href="https://www.deepl.com/docs-api/">API documentation</a> for more information.
 */
public class Usage {
  private final @Nullable Detail character;
  private final @Nullable Detail document;
  private final @Nullable Detail teamDocument;

  /** The character usage if included for the account type, or <code>null</code>. */
  public @Nullable Detail getCharacter() {
    return character;
  }

  /** The document usage if included for the account type, or <code>null</code>. */
  public @Nullable Detail getDocument() {
    return document;
  }

  /** The team document usage if included for the account type, or <code>null</code>. */
  public @Nullable Detail getTeamDocument() {
    return teamDocument;
  }

  /** Stores the amount used and maximum amount for one usage type. */
  public static class Detail {
    private final long count;
    private final long limit;

    public Detail(long count, long limit) {
      this.count = count;
      this.limit = limit;
    }

    /** @return The currently used number of items for this usage type. */
    public long getCount() {
      return count;
    }

    /** @return The maximum permitted number of items for this usage type. */
    public long getLimit() {
      return limit;
    }

    /**
     * @return <code>true</code> if the amount used meets or exceeds the limit, otherwise <code>
     *     false</code>.
     */
    public boolean limitReached() {
      return getCount() >= getLimit();
    }

    @Override
    public String toString() {
      return getCount() + " of " + getLimit();
    }
  }

  public Usage(
      @Nullable Detail character, @Nullable Detail document, @Nullable Detail teamDocument) {
    this.character = character;
    this.document = document;
    this.teamDocument = teamDocument;
  }

  /**
   * @return <code>true</code> if any of the usage types included for the account type have been
   *     reached, otherwise <code>false</code>.
   */
  public boolean anyLimitReached() {
    return (getCharacter() != null && getCharacter().limitReached())
        || (getDocument() != null && getDocument().limitReached())
        || (getTeamDocument() != null && getTeamDocument().limitReached());
  }

  /**
   * Returns a string representing the usage. This function is for diagnostic purposes only; the
   * content of the returned string is exempt from backwards compatibility.
   *
   * @return A string containing the usage for this billing period.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Usage this billing period:");

    BiConsumer<String, Detail> addLabelledDetail =
        (label, detail) -> {
          if (detail != null) {
            sb.append("\n").append(label).append(": ").append(detail);
          }
        };

    addLabelledDetail.accept("Characters", getCharacter());
    addLabelledDetail.accept("Documents", getDocument());
    addLabelledDetail.accept("Team documents", getTeamDocument());
    return sb.toString();
  }
}
