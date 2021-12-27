package dh0023.springtest.mockito.item.service;

import dh0023.springtest.mockito.item.domain.Item;

public class ItemService {

    public Item getItem() {
        return new Item("0000000001", "10");
    }

    public boolean isOnlineMallItem() {
        return true;
    }

    public void deleteItem() {

    }
}
