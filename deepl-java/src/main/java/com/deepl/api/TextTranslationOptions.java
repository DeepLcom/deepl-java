// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/**
 * Options to control text translation behaviour. These options may be provided to {@link
 * Translator#translateText} overloads.
 *
 * <p>All properties have corresponding setters in fluent-style, so the following is possible:
 * <code>
 *      TextTranslationOptions options = new TextTranslationOptions()
 *          .setFormality(Formality.Less).setGlossaryId("f63c02c5-f056-..");
 * </code>
 */
public class TextTranslationOptions {
  private Formality formality;
  private String glossaryId;
  private SentenceSplittingMode sentenceSplittingMode;
  private boolean preserveFormatting = false;
  private String context;
  private String tagHandling;
  private boolean outlineDetection = true;
  private Iterable<String> ignoreTags;
  private Iterable<String> nonSplittingTags;
  private Iterable<String> splittingTags;

  /**
   * Sets whether translations should lean toward formal or informal language. This option is only
   * applicable for target languages that support the formality option. By default, this value is
   * <code>null</code> and<code>null</code> translations use the default formality.
   *
   * @see Language#getSupportsFormality()
   * @see Formality
   */
  public TextTranslationOptions setFormality(Formality formality) {
    this.formality = formality;
    return this;
  }

  /**
   * Sets the ID of a glossary to use with the translation. By default, this value is <code>
   * null</code> and no glossary is used.
   */
  public TextTranslationOptions setGlossaryId(String glossaryId) {
    this.glossaryId = glossaryId;
    return this;
  }

  /**
   * Sets the glossary to use with the translation. By default, this value is <code>null</code> and
   * no glossary is used.
   */
  public TextTranslationOptions setGlossary(GlossaryInfo glossary) {
    return setGlossary(glossary.getGlossaryId());
  }

  /**
   * Sets the glossary to use with the translation. By default, this value is <code>null</code> and
   * no glossary is used.
   */
  public TextTranslationOptions setGlossary(String glossaryId) {
    this.glossaryId = glossaryId;
    return this;
  }

  /**
   * Specifies additional context to influence translations, that is not translated itself.
   * Characters in the `context` parameter are not counted toward billing.
   * See the API documentation for more information and example usage.
   */
  public TextTranslationOptions setContext(String context) {
    this.context = context;
    return this;
  }

  /**
   * Specifies how input translation text should be split into sentences. By default, this value is
   * <code>null</code> and the default sentence splitting mode is used.
   *
   * @see SentenceSplittingMode
   */
  public TextTranslationOptions setSentenceSplittingMode(
      SentenceSplittingMode sentenceSplittingMode) {
    this.sentenceSplittingMode = sentenceSplittingMode;
    return this;
  }

  /**
   * Sets whether formatting should be preserved in translations. Set to <code>true</code> to
   * prevent the translation engine from correcting some formatting aspects, and instead leave the
   * formatting unchanged, default is <code>false</code>.
   */
  public TextTranslationOptions setPreserveFormatting(boolean preserveFormatting) {
    this.preserveFormatting = preserveFormatting;
    return this;
  }

  /**
   * Set the type of tags to parse before translation, only <code>"xml"</code> and <code>"html"
   * </code> are currently available. By default, this value is <code>null</code> and no
   * tag-handling is used.
   */
  public TextTranslationOptions setTagHandling(String tagHandling) {
    this.tagHandling = tagHandling;
    return this;
  }

  /**
   * Sets whether outline detection is used; set to <code>false</code> to disable automatic tag
   * detection, default is <code>true</code>.
   */
  public TextTranslationOptions setOutlineDetection(boolean outlineDetection) {
    this.outlineDetection = outlineDetection;
    return this;
  }

  /**
   * Sets the list of XML tags containing content that should not be translated. By default, this
   * value is <code>null</code> and no tags are specified.
   */
  public TextTranslationOptions setIgnoreTags(Iterable<String> ignoreTags) {
    this.ignoreTags = ignoreTags;
    return this;
  }

  /**
   * Sets the list of XML tags that should not be used to split text into sentences. By default,
   * this value is <code>null</code> and no tags are specified.
   */
  public TextTranslationOptions setNonSplittingTags(Iterable<String> nonSplittingTags) {
    this.nonSplittingTags = nonSplittingTags;
    return this;
  }

  /**
   * Set the list of XML tags that should be used to split text into sentences. By default, this
   * value is <code>null</code> and no tags are specified.
   */
  public TextTranslationOptions setSplittingTags(Iterable<String> splittingTags) {
    this.splittingTags = splittingTags;
    return this;
  }

  /** Gets the current formality setting. */
  public Formality getFormality() {
    return formality;
  }

  /** Gets the current glossary ID. */
  public String getGlossaryId() {
    return glossaryId;
  }

  /** Gets the current sentence splitting mode. */
  public SentenceSplittingMode getSentenceSplittingMode() {
    return sentenceSplittingMode;
  }

  /** Gets the current preserve formatting setting. */
  public boolean isPreserveFormatting() {
    return preserveFormatting;
  }

  /** Gets the current context. */
  public String getContext() {
    return context;
  }

  /** Gets the current tag handling setting. */
  public String getTagHandling() {
    return tagHandling;
  }

  /** Gets the current outline detection setting. */
  public boolean isOutlineDetection() {
    return outlineDetection;
  }

  /** Gets the current ignore tags list. */
  public Iterable<String> getIgnoreTags() {
    return ignoreTags;
  }

  /** Gets the current non-splitting tags list. */
  public Iterable<String> getNonSplittingTags() {
    return nonSplittingTags;
  }

  /** Gets the current splitting tags list. */
  public Iterable<String> getSplittingTags() {
    return splittingTags;
  }
}
