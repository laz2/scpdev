/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
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
package net.ostis.scpdev.debug.core.model;

import org.apache.commons.lang3.Validate;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCIterator;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCConstraintBase;
import net.ostis.sc.memory.SCType;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.STDConstraints;
import net.ostis.sc.memory.TupUtils;

/**
 * Class represents scp-operator.
 *
 * @author Dmitry Lazurkin
 */
public class SCPOperator {

	private final SCSession session;
	private final SCPProgram program;
	private final SCAddr sign;

	private String type;
	private String shortName;
	private String fullName;
	private int line = -1;

	public SCPOperator(SCSession session, SCPProgram program, SCAddr sign) {
		this.session = session;
		this.program = program;
		this.sign = sign;
		fetch();
	}

	private void fetch() {
		shortName = session.getIdtf(sign);
		fullName = sign.getSegment().getURI() + "/" + shortName;

		SCAddr el = TupUtils.get(session, sign, SCPKeynodes.operator_line_);
		if (el != null)
			line = session.getContent(el).getInteger();

		SCIterator iter = session.createIterator(new SCConstraintBase(STDConstraints.CONSTR_3l2_f_a_a_a_f, SCPKeynodes.operatorTypeSCP,
				SCType.ARC_CONST_POS, SCType.EMPTY, SCType.ARC_CONST_POS, sign));
		Validate.isTrue(!iter.isOver());
		SCAddr typeSign = iter.getValue(2);
		type = session.getIdtf(typeSign);
	}

	public SCPProgram getProgram() {
		return program;
	}

	public SCAddr getSign() {
		return sign;
	}

	public String getType() {
		return type;
	}

	public String getShortName() {
		return shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public int getLine() {
		return line;
	}

	public boolean hasDebugInfo() {
		return line != -1;
	}

	@Override
	public String toString() {
		return fullName;
	}
}
