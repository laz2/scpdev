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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Scanner for detecting SCs source file partitions.
 *
 * @author Dmitry Lazurkin
 */
public class SCsPartitionScanner extends RuleBasedPartitionScanner {

    public final static String SCs_COMMENT = "__scs_comment";
    public final static String SCs_INCLUDE = "__scs_include";

    public SCsPartitionScanner() {
        List<IPredicateRule> rules = new LinkedList<IPredicateRule>();

        IToken comment = new Token(SCs_COMMENT);
        IToken include = new Token(SCs_INCLUDE);

        // set rule for single line comments
        rules.add(new SingleLineComment(comment));
        // set rule for multi-line comments
        rules.add(new MultiLineComment(comment));

        // #include directive
        rules.add(new EndOfLineRule("#include", include));

        setPredicateRules((IPredicateRule[]) rules.toArray(new IPredicateRule[rules.size()]));
    }
}
