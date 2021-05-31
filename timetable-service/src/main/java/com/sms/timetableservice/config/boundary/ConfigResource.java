package com.sms.timetableservice.config.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sms.api.common.JDK8Mapper;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.config.item.boundary.ItemResource;
import com.sms.config.item.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@Scope("request")
@RequestMapping("/config")
public class ConfigResource {

    private static final String CONFIG_ID = "timetables:lessonConfig";
    private static final JDK8Mapper MAPPER = new JDK8Mapper();

    @Autowired
    ItemResource itemResource;

    @PostMapping
    public ResponseEntity<Object> saveConfig(@RequestBody TimetableConfigDTO config) throws JsonProcessingException {
        byte[] configBytes = MAPPER.writeValueAsBytes(config);
        itemResource.saveItem(CONFIG_ID, configBytes);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<TimetableConfigDTO> getConfig() {
        return getConfigDTO().map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteConfig() {
        itemResource.deleteItem(CONFIG_ID);
        return ResponseEntity.noContent().build();
    }

    public Optional<TimetableConfigDTO> getConfigDTO() {
        Optional<Item> item = itemResource.getById(CONFIG_ID);
        if (!item.isPresent()) {
            return Optional.empty();
        }
        try {
            TimetableConfigDTO config = item.get().get(TimetableConfigDTO.class);
            return Optional.of(config);
        } catch (IOException e) {
            throw new IllegalStateException("Could not deserialize timetable configuration.");
        }
    }
}
