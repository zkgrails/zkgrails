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
