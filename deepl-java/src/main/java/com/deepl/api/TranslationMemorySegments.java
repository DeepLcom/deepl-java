// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.google.gson.annotations.*;
import java.util.*;
import org.jetbrains.annotations.*;

/** One page of the segments of a translation memory. */
public class TranslationMemorySegments {
  @SerializedName(value = "segments")
  private final List<TranslationMemorySegment> segments;

  @SerializedName(value = "segment_count")
  private final int segmentCount;

  @SerializedName(value = "next_page_cursor")
  private final @Nullable String nextPageCursor;

  /**
   * Initializes a new {@link TranslationMemorySegments} holding one page of segments.
   *
   * @param segments The segments in this page.
   * @param segmentCount Total number of segments stored in the translation memory.
   * @param nextPageCursor Cursor to fetch the next page, or <code>null</code> on the last page.
   */
  public TranslationMemorySegments(
      List<TranslationMemorySegment> segments, int segmentCount, @Nullable String nextPageCursor) {
    this.segments = segments;
    this.segmentCount = segmentCount;
    this.nextPageCursor = nextPageCursor;
  }

  /** @return The segments in this page. */
  public List<TranslationMemorySegment> getSegments() {
    return segments;
  }

  /**
   * @return Total number of segments stored in the translation memory. This is
   *     translation-memory-level metadata, so it is not reduced by a text filter.
   */
  public int getSegmentCount() {
    return segmentCount;
  }

  /**
   * @return Opaque cursor to pass as the page cursor to fetch the next page, or <code>null</code>
   *     if this is the last page.
   */
  public @Nullable String getNextPageCursor() {
    return nextPageCursor;
  }
}
