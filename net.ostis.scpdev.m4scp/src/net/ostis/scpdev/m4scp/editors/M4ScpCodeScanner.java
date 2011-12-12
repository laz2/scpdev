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
package net.ostis.scpdev.m4scp.editors;

import java.util.List;

import net.ostis.scpdev.editors.ColorManager;
import net.ostis.scpdev.editors.SequenceRule;
import net.ostis.scpdev.editors.scp.IScpWords;
import net.ostis.scpdev.editors.scs.ISCsColorConstants;
import net.ostis.scpdev.editors.scs.SCsCodeScanner;
import net.ostis.scpdev.editors.scs.SCsWordDetector;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;

/**
 * Scanner for m4scp program partition.
 *
 * @author Dmitry Lazurkin
 */
public class M4ScpCodeScanner extends SCsCodeScanner {

    public static String[] M4SCP_BRACKETS = new String[] {
            "([", "])", "[[", "]]", "{[", "]}", "[{", "}]"
    };

    public static String[] M4SCP_RESERVED = new String[] {
            "program", "procedure", "catch", "end", "label"
    };

    public M4ScpCodeScanner(ColorManager manager) {
        super(manager);
    }

    @Override
    protected List<IRule> createRules(List<IRule> rules) {
        IToken operator = getToken(IM4ScpColorConstants.M4SCP_OPERATOR);
        IToken bracket = getToken(IM4ScpColorConstants.SCS_OPERATOR);
        IToken special = getToken(ISCsColorConstants.SCS_SPECIAL);
        IToken defaultToken = getToken(ISCsColorConstants.SCS_DEFAULT);

        WordRule keywordRule = new WordRule(new SCsWordDetector(), defaultToken);
        addWords(keywordRule, IScpWords.SCP_OPERATORS, operator);
        addWords(keywordRule, M4SCP_RESERVED, bracket);
        addWords(keywordRule, IScpWords.SCP_ATTRIBUTES, special);
        addWords(keywordRule, IScpWords.SCP_ORDINALS, special);
        rules.add(keywordRule);

        rules.add(new SequenceRule(bracket, M4SCP_BRACKETS));

        return super.createRules(rules);
    }
}
