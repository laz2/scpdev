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
package net.ostis.scpdev.editors.scs;

import java.util.LinkedList;
import java.util.List;

import net.ostis.scpdev.editors.ColorManager;
import net.ostis.scpdev.editors.SequenceRule;
import net.ostis.scpdev.editors.WhitespaceDetector;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Scanner for block of scs text.
 *
 * @author Dmitry Lazurkin
 */
public class SCsCodeScanner extends RuleBasedScanner {

    public static char SCS_REFERENCE_START = '$';

    public static String[] SCS_SPECIALS = new String[] {
            "node", "arc", "undf", "const", "var", "metavar", "pos", "neg", "fuz"
    };

    public static String[] SCS_ARCS = new String[] {
            "->", "<-", "->>", "<<-", "->>>", "<<<-", "~>", "<~", "~>>", "<<~", "~>>>", "<<<~", "/>", "</", "/>>",
            "<</", "/>>>", "<<</"
    };

    public static String[] SCS_BRACKETS = new String[] {
            "{", "}", "{.", ".}", "{..", "..}", "(", ")", "[", "[]", "]", "[.", ".]", "[..", "..]"
    };

    public static String[] SCS_OPERATORS = new String[] {
            ":", "=n=", "=c=", "=", ",", ";"
    };

    protected ColorManager colorManager;

    /**
     * Rule for detecting special SCs nodes which starts with '@'.
     */
    protected class SpecialStartRule extends WordRule {

        protected char startChar;

        public SpecialStartRule(char startChar) {
            super(new SCsWordDetector());
            this.startChar = startChar;
        }

        public SpecialStartRule(char startChar, IToken defaultToken) {
            super(new SCsWordDetector(), defaultToken);
            this.startChar = startChar;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            char c = (char) scanner.read();

            if (c == startChar) {
                return super.evaluate(scanner);
            } else {
                scanner.unread();
                return Token.UNDEFINED;
            }
        }

        @Override
        protected void unreadBuffer(ICharacterScanner scanner) {
            super.unreadBuffer(scanner);
            scanner.unread(); // '@' character
        }

    }

    public SCsCodeScanner(ColorManager manager) {
        colorManager = manager;
        List<IRule> rules = createRules(new LinkedList<IRule>());
        setRules((IRule[]) rules.toArray(new IRule[rules.size()]));
    }

    protected IToken getToken(RGB color) {
        return new Token(new TextAttribute(colorManager.getColor(color)));
    }

    protected void addWords(WordRule rule, String[] words, IToken token) {
        for (int i = 0; i < words.length; i++)
            rule.addWord(words[i], token);
    }

    protected List<IRule> createRules(List<IRule> rules) {
        IToken idString = getToken(ISCsColorConstants.SCS_ID_STRING);
        IToken contentString = getToken(ISCsColorConstants.SCS_CONTENT_STRING);
        IToken operator = getToken(ISCsColorConstants.SCS_OPERATOR);
        IToken special = getToken(ISCsColorConstants.SCS_SPECIAL);
        IToken defaultToken = getToken(ISCsColorConstants.SCS_DEFAULT);

        // scs idtf as string path
        rules.add(new SingleLineRule("\"", "\"", idString, '\\'));

        // content string
        rules.add(new SingleLineRule("/\"", "\"/", contentString, '\\'));

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new WhitespaceDetector(), defaultToken));

        // special scs elements
        WordRule specialRule = new SpecialStartRule('@');
        addWords(specialRule, SCS_SPECIALS, special);
        rules.add(specialRule);

        // scs-references
        WordRule referenceRule = new SpecialStartRule('$', special);
        rules.add(referenceRule);

        // scs attribute
        rules.add(new SequenceRule(operator, SCS_ARCS, SCS_OPERATORS, SCS_BRACKETS));

        setDefaultReturnToken(defaultToken);

        return rules;
    }

}
