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
package net.ostis.scpdev.debug.core.model;

import java.net.URI;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCMemory;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCKeynodes;
import net.ostis.sc.memory.SCMemoryHelper;
import net.ostis.sc.memory.URIUtils;

/**
 * Program for testing scp-debugger.
 *
 * @author Dmitry Lazurkin
 */
public class SCPDebuggerTest {

	public static void main(String[] args) throws Exception {
		SCMemory memory = SCMemoryHelper.createMemory(new URI("rgp://localhost:47805"));
		memory.connect();
		SCSession session = memory.login();

		try {
			SCKeynodes.init(session);
			SCPDebuggerClient client = new SCPDebuggerClient(session);

			try {
				SCAddr program = URIUtils.findElementByURI(session, "/nsm_search_content_test/test");
				client.setBreakpoint(program, 39);

//				client.resume();
//				client.terminate();
			} finally {
				client.close();
			}
		} finally {
			session.close();
		}
	}

}
