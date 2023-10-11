package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.ContentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
