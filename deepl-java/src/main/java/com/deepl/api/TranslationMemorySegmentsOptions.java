// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import org.jetbrains.annotations.*;

/**
 * Options influencing the page of segments returned by {@link
 * DeepLClient#listTranslationMemorySegments(String, TranslationMemorySegmentsOptions)}.
 */
public class TranslationMemorySegmentsOptions {
  private @Nullable Integer pageSize;
  private @Nullable String pageCursor;
  private @Nullable String filterText;
  private @Nullable Boolean filterCaseSensitive;

  /**
   * Sets the maximum number of segments to return in one page.
   *
   * @param pageSize Number of segments per page, between 1 and 100, or <code>null</code> to use the
   *     API default.
   * @return This object, for convenience when chaining setters.
   * @throws IllegalArgumentException If the page size is outside the range 1 to 100.
   */
  public TranslationMemorySegmentsOptions setPageSize(@Nullable Integer pageSize) {
    if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
      throw new IllegalArgumentException("pageSize must be between 1 and 100");
    }
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Sets the cursor of the page to return.
   *
   * @param pageCursor Cursor returned as {@link TranslationMemorySegments#getNextPageCursor()} by
   *     the previous page, or <code>null</code> to return the first page.
   * @return This object, for convenience when chaining setters.
   */
  public TranslationMemorySegmentsOptions setPageCursor(@Nullable String pageCursor) {
    this.pageCursor = pageCursor;
    return this;
  }

  /**
   * Sets the text that returned segments must contain, in either their source or one of their
   * target texts.
   *
   * @param filterText Text to filter by, at least 2 characters, or <code>null</code> for no
   *     filtering.
   * @return This object, for convenience when chaining setters.
   * @throws IllegalArgumentException If the filter text is shorter than 2 characters.
   */
  public TranslationMemorySegmentsOptions setFilterText(@Nullable String filterText) {
    if (filterText != null && filterText.length() < 2) {
      throw new IllegalArgumentException("filterText must be at least 2 characters");
    }
    this.filterText = filterText;
    return this;
  }

  /**
   * Sets whether the text filter is case-sensitive.
   *
   * @param filterCaseSensitive <code>true</code> to match the filter text case-sensitively, or
   *     <code>null</code> to use the API default.
   * @return This object, for convenience when chaining setters.
   */
  public TranslationMemorySegmentsOptions setFilterCaseSensitive(
      @Nullable Boolean filterCaseSensitive) {
    this.filterCaseSensitive = filterCaseSensitive;
    return this;
  }

  /** @return Number of segments per page, or <code>null</code> if unset. */
  public @Nullable Integer getPageSize() {
    return pageSize;
  }

  /** @return Cursor of the page to return, or <code>null</code> if unset. */
  public @Nullable String getPageCursor() {
    return pageCursor;
  }

  /** @return Text that returned segments must contain, or <code>null</code> if unset. */
  public @Nullable String getFilterText() {
    return filterText;
  }

  /** @return Whether the text filter is case-sensitive, or <code>null</code> if unset. */
  public @Nullable Boolean getFilterCaseSensitive() {
    return filterCaseSensitive;
  }
}
