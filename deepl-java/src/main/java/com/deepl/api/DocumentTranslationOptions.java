// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

/**
 * Options to control document translation behaviour. These options may be provided to {@link
 * Translator#translateDocument} overloads.
 *
 * <p>All properties have corresponding setters in fluent-style, so the following is possible:
 * <code>
 *      DocumentTranslationOptions options = new DocumentTranslationOptions()
 *          .setFormality(Formality.Less).setGlossaryId("f63c02c5-f056-..");
 * </code>
 */
public class DocumentTranslationOptions {
  private Formality formality;
  private String glossaryId;

  /**
   * Sets whether translations should lean toward formal or informal language. This option is only
   * applicable for target languages that support the formality option. By default, this value is
   * <code>null</code> and translations use the default formality.
   *
   * @see Language#getSupportsFormality()
   * @see Formality
   */
  public DocumentTranslationOptions setFormality(Formality formality) {
    this.formality = formality;
    return this;
  }

  /**
   * Sets the ID of a glossary to use with the translation. By default, this value is <code>
   * null</code> and no glossary is used.
   */
  public DocumentTranslationOptions setGlossaryId(String glossaryId) {
    this.glossaryId = glossaryId;
    return this;
  }

  /**
   * Sets the glossary to use with the translation. By default, this value is <code>null</code> and
   * no glossary is used.
   */
  public DocumentTranslationOptions setGlossary(GlossaryInfo glossary) {
    return setGlossary(glossary.getGlossaryId());
  }

  /**
   * Sets the glossary to use with the translation. By default, this value is <code>null</code> and
   * no glossary is used.
   */
  public DocumentTranslationOptions setGlossary(String glossaryId) {
    this.glossaryId = glossaryId;
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
}
