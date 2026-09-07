package com.jeremy.warehouse.models.StockTrans;

public enum StockTransactionType {
    IN("NHAP", "Nhập kho"){
        @Override
        public int calculateQuantity(int current, int change) {
            return current + change;  // Nhập: tăng số lượng
        }
    },
    OUT("XUAT", "Xuất kho"){
        @Override
        public int calculateQuantity(int current, int change) {
            if (current < change) {
                throw new IllegalStateException("Không đủ hàng");
            }
            return current - change;  // Xuất: giảm số lượng
        }
    };

    private String code;
    private String description;

    StockTransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public abstract int calculateQuantity(int current, int change);
}
