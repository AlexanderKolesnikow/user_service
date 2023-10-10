package com.kite.kolesnikov.user_service.repository;

import com.kite.kolesnikov.user_service.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
