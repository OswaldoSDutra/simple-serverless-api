package com.serverless.config;

public enum DataBaseTables {

    ITEM_TABLE("ItemTableNew");

    private final String value;

    DataBaseTables(final String table) {
        this.value = table;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
