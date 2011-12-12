/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OSTIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.editors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Detect predefined sequences.
 *
 * @author Dmitry Lazurkin
 */
public class SequenceRule implements IRule {
    /** Token to return for this rule */
    private final IToken token;

    /** Set of all operators start characters */
    private Set<Character> starts = new HashSet<Character>();

    /** Set of all operators start characters */
    private Set<Character> parts = new HashSet<Character>();

    /** Set of all operators */
    private Set<String> words = new HashSet<String>();

    /** Max lenght of operators */
    private int maxWordLen = 0;

    public static void collectStartsParts(String[] words, Collection<Character> starts, Collection<Character> parts) {
        Assert.isNotNull(starts);

        for (String word : words) {
            Assert.isTrue(StringUtils.isNotEmpty(word));

            if (starts != null)
                starts.add(word.charAt(0));

            if (parts != null)
                for (int i = 1; i < word.length(); i++)
                    parts.add(word.charAt(i));
        }
    }

    public static boolean isStringsConstain(String[] strings, char c) {
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            for (int j = 0; j < s.length(); j++)
                if (s.charAt(j) == c)
                    return true;
        }

        return false;
    }

    /**
     * Creates a new scs operator rule.
     *
     * @param token Token to use for this rule
     * @param sequencesArrays Arrays of words to detect
     */
    public SequenceRule(IToken token, String[]... sequencesArrays) {
        this.token = token;

        for (String[] sequences : sequencesArrays) {
            collectStartsParts(sequences, starts, parts);

            for (String sequence : sequences) {
                words.add(sequence);

                if (sequence.length() > maxWordLen)
                    maxWordLen = sequence.length();
            }
        }
    }

    public IToken evaluate(ICharacterScanner scanner) {
        char c = (char) scanner.read();

        StringBuffer buffer = new StringBuffer(maxWordLen);
        buffer.append(c);

        int successLength = 0;

        if (starts.contains(c)) {
            if (words.contains(buffer.toString()))
                successLength = 1;

            while (buffer.length() <= maxWordLen) {
                c = (char) scanner.read();

                if (parts.contains(c)) {
                    buffer.append(c);

                    if (words.contains(buffer.toString()))
                        successLength = buffer.length();
                } else {
                    scanner.unread();
                    break;
                }
            }
        }

        for (int i = buffer.length() - successLength; i != 0; i--)
            scanner.unread();

        return (successLength != 0) ? token : Token.UNDEFINED;
    }
}
