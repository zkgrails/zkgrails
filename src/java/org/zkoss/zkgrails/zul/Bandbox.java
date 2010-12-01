package org.zkoss.zkgrails.zul;

public class Bandbox extends org.zkoss.zul.Bandbox {

    private static final long serialVersionUID = -3041156090501549864L;

    public Bandbox() {
        super();
    }

    @Override
    protected String coerceToString(Object value) {
        return value != null ? value.toString(): "";
    }

}