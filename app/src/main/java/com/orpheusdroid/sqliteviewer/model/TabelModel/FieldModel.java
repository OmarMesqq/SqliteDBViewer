package com.orpheusdroid.sqliteviewer.model.TabelModel;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class FieldModel {
    private String fieldName;
    private String fieldType;
    private int notNull;
    private int pk;
    private String def;

    public FieldModel() {
    }

    public FieldModel(String fieldName, String fieldType, int notNull, int pk, String def) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.notNull = notNull;
        this.pk = pk;
        this.def = def;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public int getNotNull() {
        return notNull;
    }

    public void setNotNull(int notNull) {
        this.notNull = notNull;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getHeaderName() {
        return this.fieldName + " (" + this.fieldType + ")";
    }
}
