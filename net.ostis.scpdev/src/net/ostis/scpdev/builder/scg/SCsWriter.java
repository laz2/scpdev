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
package net.ostis.scpdev.builder.scg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Dmitry Lazurkin
 */
public class SCsWriter extends OutputStreamWriter {

	public static final int SC_CONST = 1;
	public static final int SC_VAR = 2;
	public static final int SC_METAVAR = 3;

	public static final int SC_SET = 10;

	public static final String POS_ARC_BEG = "-";
	public static final String FUZ_ARC_BEG = "~";
	public static final String NEG_ARC_BEG = "/";

	public static final String CONST_ARC_END = ">";
	public static final String VAR_ARC_END = ">>";
	public static final String METAVAR_ARC_END = ">>>";

	public static final String ARC_CONST_POS = POS_ARC_BEG + CONST_ARC_END;
	public static final String ARC_CONST_FUZ = FUZ_ARC_BEG + CONST_ARC_END;
	public static final String ARC_CONST_NEG = NEG_ARC_BEG + CONST_ARC_END;

	public static final String ARC_VAR_POS = POS_ARC_BEG + VAR_ARC_END;
	public static final String ARC_VAR_FUZ = FUZ_ARC_BEG + VAR_ARC_END;
	public static final String ARC_VAR_NEG = NEG_ARC_BEG + VAR_ARC_END;

	public static final String ARC_METAVAR_POS = POS_ARC_BEG + METAVAR_ARC_END;
	public static final String ARC_METAVAR_FUZ = FUZ_ARC_BEG + METAVAR_ARC_END;
	public static final String ARC_METAVAR_NEG = NEG_ARC_BEG + METAVAR_ARC_END;

	public static final String CONST_SET_BEG = "{";
	public static final String CONST_SET_END = "}";

	public static final String VAR_SET_BEG = "{.";
	public static final String VAR_SET_END = ".}";

	public static final String METAVAR_SET_BEG = "{..";
	public static final String METAVAR_SET_END = "..}";

	public static final String CONST_SYS_BEG = "[";
	public static final String CONST_SYS_END = "]";

	public static final String VAR_SYS_BEG = "[.";
	public static final String VAR_SYS_END = ".]";

	public static final String METAVAR_SYS_BEG = "[..";
	public static final String METAVAR_SYS_END = "..]";

	public static final String CONST_ATTR = ":";
	public static final String VAR_ATTR = "::";
	public static final String METAVAR_ATTR = ":::";

	public static final String SYNONYM = "=";

	public static final String CONT_STR = "=c=";
	public static final String CONT_NUM = "=n=";

	public static final String SEPARATOR = ";";

	private static final String TMP_CONST_ID_FORMAT = "$%d";
	private static final String TMP_VAR_ID_FORMAT = "$_%d";
	private static final String TMP_METAVAR_ID_FORMAT = "$__%d";

	private int counterId = 0;

	private int tab = 0;

	public SCsWriter(OutputStream out) throws UnsupportedEncodingException {
		super(out, "cp1251");
	}

	public void incTab() {
		tab++;
	}

	public void decTab() {
		tab--;
		Validate.isTrue(tab >= 0);
	}

	private void writeTabs() throws IOException {
		for (int i = tab; i != 0; i--)
			super.write("\t");
	}

	public String genId(int type) {
		int tmp = counterId;
		counterId++;

		String id = null;
		switch (type) {
			case SC_CONST:
				id = String.format(TMP_CONST_ID_FORMAT, tmp);
				break;
			case SC_VAR:
				id = String.format(TMP_VAR_ID_FORMAT, tmp);
				break;
			case SC_METAVAR:
				id = String.format(TMP_METAVAR_ID_FORMAT, tmp);
				break;
			default:
				break;
		}

		return id;
	}

	public void comment(String comment) throws IOException {
		writeTabs();

		write("// ");
		write(comment);
		write("\n");
	}

	public String node(String idtf, int type) throws IOException {
		writeTabs();

		if (StringUtils.isNotEmpty(idtf)) {
			write(String.format("\"%s\" = ", idtf));
		}

		String id = genId(type);
		String beg = null;
		String end = null;
		if (type == SC_CONST) {
			beg = CONST_SET_BEG;
			end = CONST_SET_END;
		} else if (type == SC_VAR) {
			beg = VAR_SET_BEG;
			end = VAR_SET_END;
		} else if (type == SC_METAVAR) {
			beg = METAVAR_SET_BEG;
			end = METAVAR_SET_END;
		}

		write(String.format("%s = %s %s;\n", id, beg, end));

		return id;
	}

	public String arc(String from, String idtf, String type, String to, boolean reference) throws IOException {
		writeTabs();

		if (StringUtils.isNotEmpty(idtf)) {
			write(String.format("\"%s\" = ", idtf));
		}

		if (reference) {
			if (type.endsWith(METAVAR_ARC_END)) {
				idtf = genId(SC_METAVAR);
			} else if (type.endsWith(VAR_ARC_END)) {
				idtf = genId(SC_VAR);
			} else if (type.endsWith(CONST_ARC_END)) {
				idtf = genId(SC_CONST);
			}

			write(String.format("%s = ( ", idtf));
		}

		write(String.format("%s %s %s", from, type, to));

		if (reference) {
			write(" )");
		}

		write(";\n");

		return idtf;
	}

	public void arc(String from, String idtf, String type, String to) throws IOException {
		arc(from, idtf, type, to, false);
	}

}
