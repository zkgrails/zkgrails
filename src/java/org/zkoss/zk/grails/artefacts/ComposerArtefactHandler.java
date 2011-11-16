/* ComposerArtefactHandler.java

Copyright (C) 2008, 2009 Chanwit Kaewkasi

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

package org.zkoss.zk.grails.artefacts;

import org.codehaus.groovy.grails.commons.*;

/**
 *
 * @author Chanwit Kaewkasi (chanwit@gmail.com)
 *
*/
public class ComposerArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Composer";

    public ComposerArtefactHandler() {
        super(TYPE, GrailsComposerClass.class,
            DefaultGrailsComposerClass.class,
            DefaultGrailsComposerClass.COMPOSER,
            false);
    }

    public boolean isArtefactClass(@SuppressWarnings("rawtypes") Class clazz) {
        return super.isArtefactClass(clazz) &&
               !DomainClassArtefactHandler.isDomainClass(clazz);
    }
}
