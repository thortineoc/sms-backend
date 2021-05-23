package com.sms.config.item.boundary;

import com.sms.config.item.control.ItemJPA;
import com.sms.config.item.control.ItemRepository;
import com.sms.config.item.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("request")
public class ItemResource {

    @Autowired
    ItemRepository itemRepository;

    public Optional<Item> getById(String id) {
        return itemRepository.findById(id)
                .map(ItemJPA::getValue)
                .map(Item::new);
    }

    public void saveItem(String key, byte[] value) {
        ItemJPA item = new ItemJPA();
        item.setKey(key);
        item.setValue(value);
        itemRepository.save(item);
    }

    public void deleteItem(String key) {
        itemRepository.deleteAllByKey(key);
    }
}
