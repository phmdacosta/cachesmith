package com.cachesmith.example.models;

import com.cachesmith.library.annotations.AutoIncrement;
import com.cachesmith.library.annotations.Column;
import com.cachesmith.library.annotations.PrimaryKey;
import com.cachesmith.library.annotations.Unique;
import com.cachesmith.library.util.DataType;

public class TesteJava {
    @PrimaryKey
    @AutoIncrement
    @Unique
    private int pk;

    @Column(name="name")
    private String nameTeste;

    @Column(type = DataType.BLOB)
    private Object obj;

    private Integer integer;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getNameTeste() {
        return nameTeste;
    }

    public void setNameTeste(String nameTeste) {
        this.nameTeste = nameTeste;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
