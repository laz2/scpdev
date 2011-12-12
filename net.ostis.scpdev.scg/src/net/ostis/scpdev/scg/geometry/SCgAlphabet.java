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
package net.ostis.scpdev.scg.geometry;

import java.util.HashMap;
import java.util.Map;

import net.ostis.scpdev.scg.model.ConstType;
import net.ostis.scpdev.scg.model.NodeStructType;
import net.ostis.scpdev.scg.model.PosType;

public class SCgAlphabet {

	/** Constant types */
	public enum SCgConstType {

	}

	/** Structural types fo nodes */
	public enum SCgNodeStructType {

	}

	/** Positive types */
	public enum SCgPosType {

	}

	private static Map<String, ConstType> constTypes = new HashMap<String, ConstType>();
	private static Map<String, PosType> posTypes = new HashMap<String, PosType>();
	private static Map<String, NodeStructType> structTypes = new HashMap<String, NodeStructType>();

	static {
		constTypes.put("const", ConstType.Const);
		constTypes.put("var", ConstType.Var);
		constTypes.put("meta", ConstType.Meta);

		posTypes.put("pos", PosType.Positive);
		posTypes.put("neg", PosType.Negative);
		posTypes.put("fuz", PosType.Fuzzy);

		structTypes.put("not_define", NodeStructType.NotDefine);
		structTypes.put("general_node", NodeStructType.General);
		structTypes.put("predmet", NodeStructType.Predmet);
		structTypes.put("nopredmet", NodeStructType.NoPredmet);
		structTypes.put("symmetry", NodeStructType.Symmetry);
		structTypes.put("asymmetry", NodeStructType.Asymmetry);
		structTypes.put("attribute", NodeStructType.Attribute);
		structTypes.put("relation", NodeStructType.Relation);
		structTypes.put("atom", NodeStructType.Atom);
		structTypes.put("group", NodeStructType.Group);
	}

}
