package com.serverless.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemDTO {

    private String id;
    private String item;
    private String createdAt;
    private boolean itemStatus;

    public ItemDTO(String id, String item, String createdAt, boolean itemStatus) {
        this.id = id;
        this.item = item;
        this.createdAt = createdAt;
        this.itemStatus = itemStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(boolean itemStatus) {
        this.itemStatus = itemStatus;
    }


}
