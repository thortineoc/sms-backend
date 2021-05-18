package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.model.homework.FileJPA;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface FileRespository extends CrudRepository<FileJPA, Long> {

    @NonNull
    List<FileJPA> findAll();

    Optional<FileJPA> findById(Long id);

    void deleteByRelationIdAndType(Long id, FileLinkDTO.Type type);

    List<FileJPA> findAllByRelationIdAndType(Long id, FileLinkDTO.Type type);

    void deleteAllByIdIn(List<Long> id);

    void deleteByRelationId(Long Id);


}
