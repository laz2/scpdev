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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.SetUtils;
import net.ostis.sc.memory.TupUtils;

/**
 * Class represents scp-program.
 *
 * @author Dmitry Lazurkin
 */
public class SCPProgram {

	private SCSession session;
	private SCAddr sign;

	private String shortName;
	private String fullName;

	private SCAddr constsAddr;
	private SCAddr varsAddr;
	private SCAddr prmsAddr;

	private Map<SCAddr, String> var2name = new TreeMap<SCAddr, String>();

	private Map<SCAddr, SCPOperator> operators = new TreeMap<SCAddr, SCPOperator>();

	// ===================================================================================
	// START Cache of scp-programs.
	// ===================================================================================

	private static Map<SCAddr, SCPProgram> cache = new TreeMap<SCAddr, SCPProgram>();

	/**
	 * Return cached scp-program if exists else create new, cache it and return.
	 */
	public static SCPProgram getCacheOrNew(SCSession session, SCAddr sign) {
		synchronized (cache) {
			SCPProgram program = getCache(sign);
			if (program == null)
				program = new SCPProgram(session, sign);
			return program;
		}
	}

	/**
	 * Cache supplied scp-program.
	 */
	public static void putCache(SCPProgram process) {
		synchronized (cache) {
			if (!cache.containsKey(process.getSign()))
				cache.put(process.getSign(), process);
		}
	}

	/**
	 * Return from cached scp-program, which's sign equals to supplied.
	 * If no cache scp-program for supplied sign then return null.
	 */
	public static SCPProgram getCache(SCAddr sign) {
		synchronized (cache) {
			return cache.get(sign);
		}
	}

	/**
	 * Remove from cache scp-program with supplied sign.
	 */
	public static void removeCache(SCAddr sign) {
		synchronized (cache) {
			cache.remove(sign);
		}
	}

	/**
	 * @return collection of all cached scp-programs.
	 */
	public static Collection<SCPProgram> getAllCached() {
		synchronized (cache) {
			return cache.values();
		}
	}

	/**
	 * Remove all cached scp-programs.
	 */
	public static void clearCache() {
		synchronized (cache) {
			cache.clear();
		}
	}

	// ===================================================================================
	// END Cache of scp-programs.
	// ===================================================================================

	public SCPProgram(SCSession session, SCAddr sign) {
		this.session = session;
		this.sign = sign;
		fetch();
		putCache(this);
	}

	private void fetch() {
		shortName = session.getIdtf(sign);
		fullName = sign.getSegment().getURI() + "/" + shortName;

		SCAddr[] components = TupUtils.unpack(session, sign, SCPKeynodes.const_, SCPKeynodes.var_, SCPKeynodes.prm_);
		constsAddr = components[0];
		varsAddr = components[1];
		prmsAddr = components[2];

		var2name.clear();
		SetUtils.fill(session, varsAddr, var2name);
	}

	public SCPOperator getOperator(SCAddr sign) {
		synchronized (operators) {
			SCPOperator operator = operators.get(sign);
			if (operator == null) {
				operator = new SCPOperator(session, this, sign);
				operators.put(sign, operator);
			}
			return operator;
		}
	}

	public Set<SCAddr> getVariables() {
		return var2name.keySet();
	}

	public String getVariableName(SCAddr variable) {
		return var2name.get(variable);
	}

	public boolean hasVariables() {
		return !var2name.isEmpty();
	}

	public SCAddr getSign() {
		return sign;
	}

	public SCAddr getConstsAddr() {
		return constsAddr;
	}

	public SCAddr getVarsAddr() {
		return varsAddr;
	}

	public SCAddr getPrmsAddr() {
		return prmsAddr;
	}

	public String getShortName() {
		return shortName;
	}

	public String getFullName() {
		return fullName;
	}

	@Override
	public String toString() {
		return fullName;
	}
}
