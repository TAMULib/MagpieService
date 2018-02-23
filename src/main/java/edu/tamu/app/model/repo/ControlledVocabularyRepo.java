package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.repo.custom.ControlledVocabularyRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ControlledVocabularyRepo extends WeaverRepo<ControlledVocabulary>, ControlledVocabularyRepoCustom {

    public ControlledVocabulary create(String name);

    public ControlledVocabulary findByValue(String value);

}
