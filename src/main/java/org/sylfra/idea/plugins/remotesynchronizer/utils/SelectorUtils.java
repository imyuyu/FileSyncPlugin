/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.sylfra.idea.plugins.remotesynchronizer.utils;

/**
 * <p>This is a utility class used by selectors and DirectoryScanner. The
 * functionality more properly belongs just to selectors, but unfortunately
 * DirectoryScanner exposed these as protected methods. Thus we have to
 * support any subclasses of DirectoryScanner that may access these methods.
 * </p>
 * <p>This is a Singleton.</p>
 *
 * @since 1.5
 */
@SuppressWarnings("JavadocReference")
public final class SelectorUtils
{

  private static final SelectorUtils instance = new SelectorUtils();

  /**
   * Private Constructor
   */
  private SelectorUtils()
  {
  }

  /**
   * Retrieves the instance of the Singleton.
   *
   * @return singleton instance
   */
  public static SelectorUtils getInstance()
  {
    return instance;
  }

  /**
   * Tests whether or not a string matches against a pattern.
   * The pattern may contain two special characters:<br>
   * '*' means zero or more characters<br>
   * '?' means one and only one character
   *
   * @param pattern The pattern to match against.
   *                Must not be <code>null</code>.
   * @param str     The string which must be matched against the pattern.
   *                Must not be <code>null</code>.
   * @return <code>true</code> if the string matches against the pattern,
   * or <code>false</code> otherwise.
   */
  public static boolean match(String pattern, String str)
  {
    return match(pattern, str, true);
  }

  /**
   * Tests whether or not a string matches against a pattern.
   * The pattern may contain two special characters:<br>
   * '*' means zero or more characters<br>
   * '?' means one and only one character
   *
   * @param pattern       The pattern to match against.
   *                      Must not be <code>null</code>.
   * @param str           The string which must be matched against the pattern.
   *                      Must not be <code>null</code>.
   * @param caseSensitive Whether or not matching should be performed
   *                      case sensitively.
   * @return <code>true</code> if the string matches against the pattern,
   * or <code>false</code> otherwise.
   */
  public static boolean match(String pattern, String str, boolean caseSensitive)
  {
    char[] patArr = pattern.toCharArray();
    char[] strArr = str.toCharArray();
    int patIdxStart = 0;
    int patIdxEnd = patArr.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strArr.length - 1;
    char ch;

    boolean containsStar = false;
    for (char aPatArr : patArr)
    {
      if (aPatArr == '*')
      {
        containsStar = true;
        break;
      }
    }

    if (!containsStar)
    {
      // No '*'s, so we make a shortcut
      if (patIdxEnd != strIdxEnd)
      {
        return false; // Pattern and string do not have the same size
      }
      for (int i = 0; i <= patIdxEnd; i++)
      {
        ch = patArr[i];
        if (ch != '?')
        {
          if (different(caseSensitive, ch, strArr[i]))
          {
            return false; // Character mismatch
          }
        }
      }
      return true; // String matches against pattern
    }

    if (patIdxEnd == 0)
    {
      return true; // Pattern contains only '*', which matches anything
    }

    // Process characters before first star
    while (true)
    {
      ch = patArr[patIdxStart];
      if (ch == '*' || strIdxStart > strIdxEnd)
      {
        break;
      }
      if (ch != '?')
      {
        if (different(caseSensitive, ch, strArr[strIdxStart]))
        {
          return false; // Character mismatch
        }
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd)
    {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      return allStars(patArr, patIdxStart, patIdxEnd);
    }

    // Process characters after last star
    while (true)
    {
      ch = patArr[patIdxEnd];
      if (ch == '*' || strIdxStart > strIdxEnd)
      {
        break;
      }
      if (ch != '?')
      {
        if (different(caseSensitive, ch, strArr[strIdxEnd]))
        {
          return false; // Character mismatch
        }
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd)
    {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      return allStars(patArr, patIdxStart, patIdxEnd);
    }

    // process pattern between stars. padIdxStart and patIdxEnd point
    // always to a '*'.
    while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd)
    {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++)
      {
        if (patArr[i] == '*')
        {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1)
      {
        // Two stars next to each other, skip the first one.
        patIdxStart++;
        continue;
      }
      // Find the pattern between padIdxStart & padIdxTmp in str between
      // strIdxStart & strIdxEnd
      int patLength = (patIdxTmp - patIdxStart - 1);
      int strLength = (strIdxEnd - strIdxStart + 1);
      int foundIdx = -1;
      strLoop:
      for (int i = 0; i <= strLength - patLength; i++)
      {
        for (int j = 0; j < patLength; j++)
        {
          ch = patArr[patIdxStart + j + 1];
          if (ch != '?')
          {
            if (different(caseSensitive, ch,
              strArr[strIdxStart + i + j]))
            {
              continue strLoop;
            }
          }
        }

        foundIdx = strIdxStart + i;
        break;
      }

      if (foundIdx == -1)
      {
        return false;
      }

      patIdxStart = patIdxTmp;
      strIdxStart = foundIdx + patLength;
    }

    // All characters in the string are used. Check if only '*'s are left
    // in the pattern. If so, we succeeded. Otherwise failure.
    return allStars(patArr, patIdxStart, patIdxEnd);
  }

  private static boolean allStars(char[] chars, int start, int end)
  {
    for (int i = start; i <= end; ++i)
    {
      if (chars[i] != '*')
      {
        return false;
      }
    }
    return true;
  }

  private static boolean different(
    boolean caseSensitive, char ch, char other)
  {
    return caseSensitive
      ? ch != other
      : Character.toUpperCase(ch) != Character.toUpperCase(other);
  }
}
