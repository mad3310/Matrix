package com.letv.common.model;

public interface ISoftDelete {
    public static final String FIELD_DELETED = "deleted";
    
    boolean isDeleted();
    void setDeleted(boolean deleted);
}
