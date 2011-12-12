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

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * SCs aware word detector.
 *
 * @author Dmitry Lazurkin
 */
public class SCsWordDetector implements IWordDetector {

    private static final Pattern START_PATTERN = Pattern.compile("[\\x80-\\xff$A-Za-z0-9_-]");
    private static final Pattern PART_PATTERN = Pattern.compile("[\\x80-\\xff$A-Za-z0-9_]");

    public boolean isWordStart(char c) {
        return START_PATTERN.matcher("" + c).matches();
    }

    public boolean isWordPart(char c) {
        return PART_PATTERN.matcher("" + c).matches();
    }

}
