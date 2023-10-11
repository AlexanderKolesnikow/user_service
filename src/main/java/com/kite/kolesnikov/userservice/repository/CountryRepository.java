package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
