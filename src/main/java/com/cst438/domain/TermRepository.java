package com.cst438.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TermRepository extends CrudRepository<Term, Integer> {

    Term findByYearAndSemester( int year, String semester);

    @Query("select t from Term t join Section s on t.termId = s.term.termId order by 1 desc")
    List<Term> findAllByOrderByTermIdDesc();
}
