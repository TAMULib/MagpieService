package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.custom.ControlledVocabularyRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ControlledVocabularyRepoImpl extends AbstractWeaverRepoImpl<ControlledVocabulary, ControlledVocabularyRepo> implements ControlledVocabularyRepoCustom {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public synchronized ControlledVocabulary create(String value) {
        ControlledVocabulary cv = controlledVocabularyRepo.findByValue(value);
        if (cv == null) {
            cv = controlledVocabularyRepo.save(new ControlledVocabulary(value));
        }
        return cv;
    }

    @Override
    protected String getChannel() {
        return "/channel/cv";
    }

}
