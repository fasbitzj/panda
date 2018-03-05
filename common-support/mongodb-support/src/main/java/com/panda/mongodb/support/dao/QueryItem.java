package com.panda.mongodb.support.dao;

import java.io.Serializable;

public class QueryItem implements Serializable {

    private static final long serialVersionUID = -1L;

    private String param;
    private Object value;  // Array or collection
    private Object start;   // When formula is BETWEEN, use start and end
    private Object end;
    private Formula formula;

    private boolean ignoreCase = false;

    public QueryItem() {}

    public QueryItem(String param, Object value, Formula formula) {
        this.param = param;
        this.value = value;
        this.formula = formula;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getStart() {
        return start;
    }

    public void setStart(Object start) {
        this.start = start;
    }

    public Object getEnd() {
        return end;
    }

    public void setEnd(Object end) {
        this.end = end;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}
