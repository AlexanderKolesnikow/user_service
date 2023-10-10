package com.kite.kolesnikov.user_service.repository;

import com.kite.kolesnikov.user_service.entity.ContentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
