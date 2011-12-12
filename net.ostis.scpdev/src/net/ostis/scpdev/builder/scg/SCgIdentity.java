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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry Lazurkin
 */
public class SCgIdentity {

    private String idtf = null;

    private List<SCgObject> views = new LinkedList<SCgObject>();

    private boolean writed = false;

	private String mainId = null;

    public boolean isWrited() {
		return writed;
	}

	public void setWrited(boolean writed) {
		this.writed = writed;
	}

	public String getMainId() {
		return mainId;
	}

	public void setMainId(String mainId) {
		this.mainId = mainId;
	}

    public SCgIdentity() {
    }

    public SCgIdentity(String idtf) {
        this.idtf = idtf;
    }

    public void setIdtf(String idtf) {
        this.idtf = idtf;
    }

    public String getIdtf() {
        return idtf;
    }

    public boolean hasIdtf() {
        return StringUtils.isNotEmpty(idtf);
    }

    public void addView(SCgObject view) {
        views.add(view);
    }

    public void setViews(List<SCgObject> views) {
        assert views != null;
        this.views = views;
    }

    public List<SCgObject> getViews() {
        return views;
    }

}
