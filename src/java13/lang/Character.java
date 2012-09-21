/**
 * Distribution License:
 * jBQ is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 of higher as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 */

// ported from GNU Classpath to support some unicode operations

package java13.lang;

public final class Character {

  /**
   * Lu = Letter, Uppercase (Informative).
   *
   * @since 1.1
   */
  public static final byte UPPERCASE_LETTER = 1;

  /**
   * Ll = Letter, Lowercase (Informative).
   *
   * @since 1.1
   */
  public static final byte LOWERCASE_LETTER = 2;

  /**
   * Lt = Letter, Titlecase (Informative).
   *
   * @since 1.1
   */
  public static final byte TITLECASE_LETTER = 3;

  /**
   * Mn = Mark, Non-Spacing (Normative).
   *
   * @since 1.1
   */
  public static final byte NON_SPACING_MARK = 6;

  /**
   * Mc = Mark, Spacing Combining (Normative).
   *
   * @since 1.1
   */
  public static final byte COMBINING_SPACING_MARK = 8;

  /**
   * Me = Mark, Enclosing (Normative).
   *
   * @since 1.1
   */
  public static final byte ENCLOSING_MARK = 7;

  /**
   * Nd = Number, Decimal Digit (Normative).
   *
   * @since 1.1
   */
  public static final byte DECIMAL_DIGIT_NUMBER = 9;

  /**
   * Nl = Number, Letter (Normative).
   *
   * @since 1.1
   */
  public static final byte LETTER_NUMBER = 10;

  /**
   * No = Number, Other (Normative).
   *
   * @since 1.1
   */
  public static final byte OTHER_NUMBER = 11;

  /**
   * Zs = Separator, Space (Normative).
   *
   * @since 1.1
   */
  public static final byte SPACE_SEPARATOR = 12;

  /**
   * Zl = Separator, Line (Normative).
   *
   * @since 1.1
   */
  public static final byte LINE_SEPARATOR = 13;

  /**
   * Zp = Separator, Paragraph (Normative).
   *
   * @since 1.1
   */
  public static final byte PARAGRAPH_SEPARATOR = 14;

  /**
   * Cc = Other, Control (Normative).
   *
   * @since 1.1
   */
  public static final byte CONTROL = 15;

  /**
   * Cf = Other, Format (Normative).
   *
   * @since 1.1
   */
  public static final byte FORMAT = 16;

  /**
   * Cs = Other, Surrogate (Normative).
   *
   * @since 1.1
   */
  public static final byte SURROGATE = 19;

  /**
   * Co = Other, Private Use (Normative).
   *
   * @since 1.1
   */
  public static final byte PRIVATE_USE = 18;

  /**
   * Cn = Other, Not Assigned (Normative).
   *
   * @since 1.1
   */
  public static final byte UNASSIGNED = 0;

  /**
   * Lm = Letter, Modifier (Informative).
   *
   * @since 1.1
   */
  public static final byte MODIFIER_LETTER = 4;

  /**
   * Lo = Letter, Other (Informative).
   *
   * @since 1.1
   */
  public static final byte OTHER_LETTER = 5;

  /**
   * Mask for grabbing the type out of the contents of data.
   * @see CharData#DATA
   */
  private static final int TYPE_MASK = 0x1F;
  
  /**
   * Stores unicode block offset lookup table. Exploit package visibility of
   * String.value to avoid copying the array.
   * @see #readCodePoint(int)
   * @see CharData#BLOCKS
   */
  private static final char[][] blocks = 
    new char[][]{
                 (CharData.BLOCKS[0]).toCharArray(),
                 (CharData.BLOCKS[1]).toCharArray(),
                 (CharData.BLOCKS[2]).toCharArray(),
                 (CharData.BLOCKS[3]).toCharArray(),
                 (CharData.BLOCKS[4]).toCharArray(),
                 (CharData.BLOCKS[5]).toCharArray(),
                 (CharData.BLOCKS[6]).toCharArray(),
                 (CharData.BLOCKS[7]).toCharArray(),
                 (CharData.BLOCKS[8]).toCharArray(),
                 (CharData.BLOCKS[9]).toCharArray(),
                 (CharData.BLOCKS[10]).toCharArray(),
                 (CharData.BLOCKS[11]).toCharArray(),
                 (CharData.BLOCKS[12]).toCharArray(),
                 (CharData.BLOCKS[13]).toCharArray(),
                 (CharData.BLOCKS[14]).toCharArray(),
                 (CharData.BLOCKS[15]).toCharArray(),
                 (CharData.BLOCKS[16]).toCharArray()};

  /**
   * Stores unicode attribute offset lookup table. Exploit package visibility
   * of String.value to avoid copying the array.
   * @see CharData#DATA
   */  
  private static final char[][] data = 
    new char[][]{
                 (CharData.DATA[0]).toCharArray(),
                 (CharData.DATA[1]).toCharArray(),
                 (CharData.DATA[2]).toCharArray(),
                 (CharData.DATA[3]).toCharArray(),
                 (CharData.DATA[4]).toCharArray(),
                 (CharData.DATA[5]).toCharArray(),
                 (CharData.DATA[6]).toCharArray(),
                 (CharData.DATA[7]).toCharArray(),
                 (CharData.DATA[8]).toCharArray(),
                 (CharData.DATA[9]).toCharArray(),
                 (CharData.DATA[10]).toCharArray(),
                 (CharData.DATA[11]).toCharArray(),
                 (CharData.DATA[12]).toCharArray(),
                 (CharData.DATA[13]).toCharArray(),
                 (CharData.DATA[14]).toCharArray(),
                 (CharData.DATA[15]).toCharArray(),
                 (CharData.DATA[16]).toCharArray()};

  /**
   * Stores unicode lowercase attribute table. Exploit package visibility
   * of String.value to avoid copying the array.
   * @see CharData#LOWER
   */
  private static final char[][] lower = 
    new char[][]{
                 (CharData.LOWER[0]).toCharArray(),
                 (CharData.LOWER[1]).toCharArray(),
                 (CharData.LOWER[2]).toCharArray(),
                 (CharData.LOWER[3]).toCharArray(),
                 (CharData.LOWER[4]).toCharArray(),
                 (CharData.LOWER[5]).toCharArray(),
                 (CharData.LOWER[6]).toCharArray(),
                 (CharData.LOWER[7]).toCharArray(),
                 (CharData.LOWER[8]).toCharArray(),
                 (CharData.LOWER[9]).toCharArray(),
                 (CharData.LOWER[10]).toCharArray(),
                 (CharData.LOWER[11]).toCharArray(),
                 (CharData.LOWER[12]).toCharArray(),
                 (CharData.LOWER[13]).toCharArray(),
                 (CharData.LOWER[14]).toCharArray(),
                 (CharData.LOWER[15]).toCharArray(),
                 (CharData.LOWER[16]).toCharArray()};

  /**
   * Converts a Unicode character into its lowercase equivalent mapping.
   * If a mapping does not exist, then the character passed is returned.
   * Note that isLowerCase(toLowerCase(ch)) does not always return true.
   *
   * @param ch character to convert to lowercase
   * @return lowercase mapping of ch, or ch if lowercase mapping does
   *         not exist
   * @see #isLowerCase(char)
   * @see #isUpperCase(char)
   * @see #toTitleCase(char)
   * @see #toUpperCase(char)
   */
  public static char toLowerCase(char ch)
  {
    return (char) (lower[0][readCodePoint((int)ch) >>> 7] + ch);
  }

  /**
   * Grabs an attribute offset from the Unicode attribute database. The lower
   * 5 bits are the character type, the next 2 bits are flags, and the top
   * 9 bits are the offset into the attribute tables.
   *
   * @param codePoint the character to look up
   * @return the character's attribute offset and type
   * @see #TYPE_MASK
   * @see #NO_BREAK_MASK
   * @see #MIRROR_MASK
   * @see CharData#DATA
   * @see CharData#SHIFT
   */
  static char readCodePoint(int codePoint)
  {
    int plane = codePoint >>> 16;
    char offset = (char) (codePoint & 0xffff);
    return data[plane][(char) (blocks[plane][offset >> CharData.SHIFT[plane]] + offset)];
  }

  /**
   * Determines if a character is a Unicode letter. Not all letters have case,
   * so this may return true when isLowerCase and isUpperCase return false.
   * A character is a Unicode letter if getType() returns one of 
   * UPPERCASE_LETTER, LOWERCASE_LETTER, TITLECASE_LETTER, MODIFIER_LETTER,
   * or OTHER_LETTER.
   * <br>
   * letter = [Lu]|[Ll]|[Lt]|[Lm]|[Lo]
   *
   * @param ch character to test
   * @return true if ch is a Unicode letter, else false
   * @see #isDigit(char)
   * @see #isJavaIdentifierStart(char)
   * @see #isJavaLetter(char)
   * @see #isJavaLetterOrDigit(char)
   * @see #isLetterOrDigit(char)
   * @see #isLowerCase(char)
   * @see #isTitleCase(char)
   * @see #isUnicodeIdentifierStart(char)
   * @see #isUpperCase(char)
   */
  public static boolean isLetter(char ch)
  {
    return isLetter((int)ch);
  }
  
  /**
   * Determines if a character is a Unicode letter. Not all letters have case,
   * so this may return true when isLowerCase and isUpperCase return false.
   * A character is a Unicode letter if getType() returns one of 
   * UPPERCASE_LETTER, LOWERCASE_LETTER, TITLECASE_LETTER, MODIFIER_LETTER,
   * or OTHER_LETTER.
   * <br>
   * letter = [Lu]|[Ll]|[Lt]|[Lm]|[Lo]
   *
   * @param codePoint character to test
   * @return true if ch is a Unicode letter, else false
   * @see #isDigit(char)
   * @see #isJavaIdentifierStart(char)
   * @see #isJavaLetter(char)
   * @see #isJavaLetterOrDigit(char)
   * @see #isLetterOrDigit(char)
   * @see #isLowerCase(char)
   * @see #isTitleCase(char)
   * @see #isUnicodeIdentifierStart(char)
   * @see #isUpperCase(char)
   * 
   * @since 1.5
   */
  public static boolean isLetter(int codePoint)
  {
    return ((1 << getType(codePoint))
        & ((1 << UPPERCASE_LETTER)
            | (1 << LOWERCASE_LETTER)
            | (1 << TITLECASE_LETTER)
            | (1 << MODIFIER_LETTER)
            | (1 << OTHER_LETTER))) != 0;
  }
  
  /**
   * Determines if a character is a Unicode decimal digit. For example,
   * <code>'0'</code> is a digit.  A character is a Unicode digit if
   * getType() returns DECIMAL_DIGIT_NUMBER.
   * <br>
   * Unicode decimal digit = [Nd]
   *
   * @param ch character to test
   * @return true if ch is a Unicode decimal digit, else false
   * @see #digit(char, int)
   * @see #forDigit(int, int)
   * @see #getType(char)
   */
  public static boolean isDigit(char ch)
  {
    return isDigit((int)ch);
  }
  
  /**
   * Determines if a character is a Unicode decimal digit. For example,
   * <code>'0'</code> is a digit. A character is a Unicode digit if
   * getType() returns DECIMAL_DIGIT_NUMBER.
   * <br>
   * Unicode decimal digit = [Nd]
   *
   * @param codePoint character to test
   * @return true if ch is a Unicode decimal digit, else false
   * @see #digit(char, int)
   * @see #forDigit(int, int)
   * @see #getType(char)
   * 
   * @since 1.5
   */

  public static boolean isDigit(int codePoint)
  {
    return getType(codePoint) == DECIMAL_DIGIT_NUMBER;
  }

  /**
   * Returns the Unicode general category property of a character.
   *
   * @param ch character from which the general category property will
   *        be retrieved
   * @return the character category property of ch as an integer
   * @see #UNASSIGNED
   * @see #UPPERCASE_LETTER
   * @see #LOWERCASE_LETTER
   * @see #TITLECASE_LETTER
   * @see #MODIFIER_LETTER
   * @see #OTHER_LETTER
   * @see #NON_SPACING_MARK
   * @see #ENCLOSING_MARK
   * @see #COMBINING_SPACING_MARK
   * @see #DECIMAL_DIGIT_NUMBER
   * @see #LETTER_NUMBER
   * @see #OTHER_NUMBER
   * @see #SPACE_SEPARATOR
   * @see #LINE_SEPARATOR
   * @see #PARAGRAPH_SEPARATOR
   * @see #CONTROL
   * @see #FORMAT
   * @see #PRIVATE_USE
   * @see #SURROGATE
   * @see #DASH_PUNCTUATION
   * @see #START_PUNCTUATION
   * @see #END_PUNCTUATION
   * @see #CONNECTOR_PUNCTUATION
   * @see #OTHER_PUNCTUATION
   * @see #MATH_SYMBOL
   * @see #CURRENCY_SYMBOL
   * @see #MODIFIER_SYMBOL
   * @see #INITIAL_QUOTE_PUNCTUATION
   * @see #FINAL_QUOTE_PUNCTUATION
   * @since 1.1
   */
  public static int getType(char ch)
  {
    return getType((int)ch);
  }
  
  /**
   * Returns the Unicode general category property of a character.
   *
   * @param codePoint character from which the general category property will
   *        be retrieved
   * @return the character category property of ch as an integer
   * @see #UNASSIGNED
   * @see #UPPERCASE_LETTER
   * @see #LOWERCASE_LETTER
   * @see #TITLECASE_LETTER
   * @see #MODIFIER_LETTER
   * @see #OTHER_LETTER
   * @see #NON_SPACING_MARK
   * @see #ENCLOSING_MARK
   * @see #COMBINING_SPACING_MARK
   * @see #DECIMAL_DIGIT_NUMBER
   * @see #LETTER_NUMBER
   * @see #OTHER_NUMBER
   * @see #SPACE_SEPARATOR
   * @see #LINE_SEPARATOR
   * @see #PARAGRAPH_SEPARATOR
   * @see #CONTROL
   * @see #FORMAT
   * @see #PRIVATE_USE
   * @see #SURROGATE
   * @see #DASH_PUNCTUATION
   * @see #START_PUNCTUATION
   * @see #END_PUNCTUATION
   * @see #CONNECTOR_PUNCTUATION
   * @see #OTHER_PUNCTUATION
   * @see #MATH_SYMBOL
   * @see #CURRENCY_SYMBOL
   * @see #MODIFIER_SYMBOL
   * @see #INITIAL_QUOTE_PUNCTUATION
   * @see #FINAL_QUOTE_PUNCTUATION
   * 
   * @since 1.5
   */
  public static int getType(int codePoint)
  {
    // If the codePoint is unassigned or in one of the private use areas
    // then we delegate the call to the appropriate private static inner class.
    int plane = codePoint >>> 16;
    if (plane > 2 && plane < 14)
      return UNASSIGNED;
    if (plane > 14)
      return PRIVATE_USE;
    
    return readCodePoint(codePoint) & TYPE_MASK;
  }
}
