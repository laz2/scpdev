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
package net.ostis.scpdev.ui.model;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Dmitry Lazurkin
 */
public class AggregatetWorkbenchLabelProvider implements IBaseLabelProvider, ILabelProvider, IColorProvider, IFontProvider {

    private WorkbenchLabelProvider aggregate;

    public static ILabelProvider getDecoratingWorkbenchLabelProvider() {
        return new DecoratingLabelProvider(new AggregatetWorkbenchLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager()
                        .getLabelDecorator());
    }

    public AggregatetWorkbenchLabelProvider() {
        aggregate = new WorkbenchLabelProvider();
    }

    public AggregatetWorkbenchLabelProvider(WorkbenchLabelProvider aggregate) {
        this.aggregate = aggregate;
    }

    @Override
    public Image getImage(Object element) {
        return aggregate.getImage(element);
    }

    @Override
    public String getText(Object element) {
        return aggregate.getText(element);
    }

    @Override
    public Font getFont(Object element) {
        return aggregate.getFont(element);
    }

    @Override
    public Color getForeground(Object element) {
        return aggregate.getForeground(element);
    }

    @Override
    public Color getBackground(Object element) {
        return aggregate.getBackground(element);
    }

    @Override
    public void dispose() {
        aggregate.dispose();
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
        aggregate.addListener(listener);
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return aggregate.isLabelProperty(element, property);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        aggregate.removeListener(listener);
    }

}
