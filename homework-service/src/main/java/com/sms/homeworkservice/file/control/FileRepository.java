package com.sms.homeworkservice.file.control;

import com.sms.model.homework.FileJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface FileRepository extends CrudRepository<FileJPA, Long> {

    @NonNull
    List<FileJPA> findAll();

    Optional<FileJPA> findById(Long id);

    void deleteAllByRelationIdAndType(Long id, String type);

    void deleteAllByOwnerId(String id);

    @Modifying
    @Query("DELETE FROM FileJPA file WHERE file.relationId IN :answerID AND file.type = 'ANSWER' OR file.relationId IN :homeworkID AND file.type = 'HOMEWORK'")
    void deleteHomeworksAndAnswersFiles(List<Long> answerID,  Long homeworkID);

}
