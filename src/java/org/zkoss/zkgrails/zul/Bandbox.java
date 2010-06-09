package org.zkoss.zkgrails.zul;

public class Bandbox extends org.zkoss.zul.Bandbox {

    public Bandbox() {
        super();
    }

    @Override
    protected String coerceToString(Object value) {
		return value != null ? value.toString(): "";
	}

}