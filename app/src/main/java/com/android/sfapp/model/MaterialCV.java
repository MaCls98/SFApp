package com.android.sfapp.model;

public class MaterialCV {

    private int materialId;
    private String materialType;
    private String quantity;
    private String date;
    private String price;

    public MaterialCV(int materialId, String materialType, String quantity, String date, String price) {
        this.materialId = materialId;
        this.materialType = materialType;
        this.quantity = quantity;
        this.date = date;
        this.price = price;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MaterialCV{" +
                "materialId=" + materialId +
                ", materialType='" + materialType + '\'' +
                ", quantity='" + quantity + '\'' +
                ", date='" + date + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
