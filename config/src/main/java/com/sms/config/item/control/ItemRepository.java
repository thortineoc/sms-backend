package com.sms.config.item.control;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ItemRepository extends CrudRepository<ItemJPA, String> {

    void deleteAllByKey(String key);
}
