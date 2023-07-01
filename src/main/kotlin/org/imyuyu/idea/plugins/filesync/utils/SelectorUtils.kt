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
package org.imyuyu.idea.plugins.filesync.utils

/**
 *
 * This is a utility class used by selectors and DirectoryScanner. The
 * functionality more properly belongs just to selectors, but unfortunately
 * DirectoryScanner exposed these as protected methods. Thus we have to
 * support any subclasses of DirectoryScanner that may access these methods.
 *
 *
 * This is a Singleton.
 *
 * @since 1.5
 */
object SelectorUtils {
    /**
     * Retrieves the instance of the Singleton.
     *
     * @return singleton instance
     */
    // val instance: SelectorUtils = SelectorUtils()
    /**
     * Tests whether or not a string matches against a pattern.
     * The pattern may contain two special characters:<br></br>
     * '*' means zero or more characters<br></br>
     * '?' means one and only one character
     *
     * @param pattern       The pattern to match against.
     * Must not be `null`.
     * @param str           The string which must be matched against the pattern.
     * Must not be `null`.
     * @param caseSensitive Whether or not matching should be performed
     * case sensitively.
     * @return `true` if the string matches against the pattern,
     * or `false` otherwise.
     */
    /**
     * Tests whether or not a string matches against a pattern.
     * The pattern may contain two special characters:<br></br>
     * '*' means zero or more characters<br></br>
     * '?' means one and only one character
     *
     * @param pattern The pattern to match against.
     * Must not be `null`.
     * @param str     The string which must be matched against the pattern.
     * Must not be `null`.
     * @return `true` if the string matches against the pattern,
     * or `false` otherwise.
     */
    @JvmOverloads
    fun match(pattern: String, str: String, caseSensitive: Boolean = true): Boolean {
        val patArr = pattern.toCharArray()
        val strArr = str.toCharArray()
        var patIdxStart = 0
        var patIdxEnd = patArr.size - 1
        var strIdxStart = 0
        var strIdxEnd = strArr.size - 1
        var ch: Char
        var containsStar = false
        for (aPatArr in patArr) {
            if (aPatArr == '*') {
                containsStar = true
                break
            }
        }
        if (!containsStar) {
            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false // Pattern and string do not have the same size
            }
            for (i in 0..patIdxEnd) {
                ch = patArr[i]
                if (ch != '?') {
                    if (different(caseSensitive, ch, strArr[i])) {
                        return false // Character mismatch
                    }
                }
            }
            return true // String matches against pattern
        }
        if (patIdxEnd == 0) {
            return true // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while (true) {
            ch = patArr[patIdxStart]
            if (ch == '*' || strIdxStart > strIdxEnd) {
                break
            }
            if (ch != '?') {
                if (different(caseSensitive, ch, strArr[strIdxStart])) {
                    return false // Character mismatch
                }
            }
            patIdxStart++
            strIdxStart++
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            return allStars(patArr, patIdxStart, patIdxEnd)
        }

        // Process characters after last star
        while (true) {
            ch = patArr[patIdxEnd]
            if (ch == '*' || strIdxStart > strIdxEnd) {
                break
            }
            if (ch != '?') {
                if (different(caseSensitive, ch, strArr[strIdxEnd])) {
                    return false // Character mismatch
                }
            }
            patIdxEnd--
            strIdxEnd--
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            return allStars(patArr, patIdxStart, patIdxEnd)
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            var patIdxTmp = -1
            for (i in patIdxStart + 1..patIdxEnd) {
                if (patArr[i] == '*') {
                    patIdxTmp = i
                    break
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++
                continue
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            val patLength = patIdxTmp - patIdxStart - 1
            val strLength = strIdxEnd - strIdxStart + 1
            var foundIdx = -1
            strLoop@ for (i in 0..strLength - patLength) {
                for (j in 0 until patLength) {
                    ch = patArr[patIdxStart + j + 1]
                    if (ch != '?') {
                        if (different(
                                caseSensitive, ch,
                                strArr[strIdxStart + i + j]
                            )
                        ) {
                            continue@strLoop
                        }
                    }
                }
                foundIdx = strIdxStart + i
                break
            }
            if (foundIdx == -1) {
                return false
            }
            patIdxStart = patIdxTmp
            strIdxStart = foundIdx + patLength
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        return allStars(patArr, patIdxStart, patIdxEnd)
    }

    private fun allStars(chars: CharArray, start: Int, end: Int): Boolean {
        for (i in start..end) {
            if (chars[i] != '*') {
                return false
            }
        }
        return true
    }

    private fun different(
        caseSensitive: Boolean, ch: Char, other: Char
    ): Boolean {
        return if (caseSensitive) ch != other else ch.uppercaseChar() != other.uppercaseChar()
    }
}
