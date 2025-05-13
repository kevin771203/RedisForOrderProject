package org.kevinlin.springbootmall.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    private List<BuyItem> buyItemsList;

    public List<BuyItem> getBuyItemsList() {
        return buyItemsList;
    }
    public void setBuyItemsList(List<BuyItem> buyItemsList) {
        this.buyItemsList = buyItemsList;
    }

}
