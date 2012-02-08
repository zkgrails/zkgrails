/* ListboxModelDynamicMethods.java

Copyright (C) 2009 Chanwit Kaewkasi

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.zkoss.zk.grails;

import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;
import java.util.*;

public class ListboxModelDynamicMethods {

    @SuppressWarnings("rawtypes")
    public static void setModel(Listbox delegate, Object list) {
        if(list instanceof Set) {
            ArrayList<Object> newList = new ArrayList<Object>();
            for(Object e: (Set<?>)list) {
                newList.add(e);
            }
            list = newList;
        }

        if(list instanceof java.util.List) {
            Object model = delegate.getModel();
            if(model != null) {
                if(model instanceof BindingListModelList) {
                    BindingListModelList blml = (BindingListModelList)model;
                    blml.clear();
                    blml.addAll((java.util.List<?>)list);
                    return;
                } else {
                    delegate.setModel(new BindingListModelList((java.util.List<?>)list, false));
                    return;
                }
            } else {
                delegate.setModel(new BindingListModelList((java.util.List<?>)list, false));
                return;
            }
        }

        if (list instanceof org.zkoss.zul.GroupsModel) {
            delegate.setModel((org.zkoss.zul.GroupsModel)list);
        } else {
            delegate.setModel((org.zkoss.zul.ListModel)list);
        }
    }
}
