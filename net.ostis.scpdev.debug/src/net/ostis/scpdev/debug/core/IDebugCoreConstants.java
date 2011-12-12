/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSTIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.debug.core;

/**
 * @author Dmitry Lazurkin
 */
public interface IDebugCoreConstants {

	/**
	 * Unique identifier for the Scpdev debug model (value <code>net.ostis.scpdev.debug</code>).
	 */
	public static final String ID_DEBUG_MODEL = "net.ostis.scpdev.debug";

	public static final String ATTR_PROJECT = ID_DEBUG_MODEL + ".ATTR_PROJECT";
	public static final String ATTR_VERBOSE_EXEC = ID_DEBUG_MODEL + ".ATTR_VERBOSE_EXEC";

	public static final String ATTR_RUN_MODE = ID_DEBUG_MODEL + ".ATTR_RUN_MODE";

	public static final String RUN_MODE_DEFAULT = "run_mode_default";

	public static final String RUN_MODE_PROGRAM_FROM_PATH = "run_mode_program_path";

	public static final String ATTR_PROGRAM_PATH = ID_DEBUG_MODEL + ".ATTR_PROGRAM_PATH";

	public static final String RUN_MODE_TESTSUITE_FROM_PATH = "run_mode_testsuite";

	public static final String ATTR_TESTSUITE_PATH = ID_DEBUG_MODEL + ".ATTR_TESTSUITE_PATH";

	public static final String ATTR_DIAGNOSTIC_EXEC = ID_DEBUG_MODEL + ".ATTR_DIAGNOSTIC_EXEC";
	public static final String ATTR_STAY_EXEC = ID_DEBUG_MODEL + ".ATTR_STAY_EXEC";
	public static final String ATTR_STOP_ON_ERROR = ID_DEBUG_MODEL + ".ATTR_STOP_ON_ERROR";
}
