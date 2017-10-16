package com.fillodeos.sdr.webhook.domain.repository;

import com.fillodeos.sdr.webhook.domain.Person;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

}
