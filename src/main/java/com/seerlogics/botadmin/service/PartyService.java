package com.seerlogics.botadmin.service;

import com.lingoace.exception.jpa.UnknownTypeException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Organization;
import com.seerlogics.commons.model.Party;
import com.seerlogics.commons.model.Person;
import com.seerlogics.commons.repository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional
public class PartyService extends BaseServiceImpl<Party> {

    @Autowired
    private PartyRepository partyRepository;

    public Party initParty(String type) {
        if (Party.PART_TYPE.PERSON.name().toLowerCase().equals(type)) {
            return new Person();
        } else if (Party.PART_TYPE.ORGANIZATION.name().toLowerCase().equals(type)) {
            return new Organization();
        }
        throw new UnknownTypeException("Unknown type = " + type);
    }

    @Override
    public Collection<Party> getAll() {
        return partyRepository.findAll();
    }

    @Override
    public Party getSingle(Long id) {
        return partyRepository.getOne(id);
    }

    @Override
    public Party save(Party object) {
        return partyRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        partyRepository.deleteById(id);
    }
}
