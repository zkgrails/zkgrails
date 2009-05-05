/* Snippets.java

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

package org.zkoss.zkgrails.scripting;

public class Snippets {

    static final String ALERT =
        "import org.zkoss.zul.Messagebox\n" +
        "\n" +
        "alert = { text ->\n" +
        "	Messagebox.show(text, \"Alert\", Messagebox.OK, Messagebox.QUESTION)\n" +
        "}\n" +
        "\n";

    static final String IMPORTS =
        "import org.zkoss.zk.ui.*;\n" +
        "import org.zkoss.zk.ui.event.*;\n" +
        "import org.zkoss.zkplus.databind.*;\n" +
        "import org.zkoss.zul.*\n" +
        "import static org.zkoss.zkgrails.Helper.*\n" +
        "\n";
}
