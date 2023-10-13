package com.kite.kolesnikov.userservice.repository.recommendation;

import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {


    Page<Recommendation> findAllByReceiverId(long receiverId, Pageable pageable);

    Page<Recommendation> findAllByAuthorId(long authorId, Pageable pageable);

    Optional<Recommendation> findById(long id);

    @Query(nativeQuery = true, value = """
            SELECT r.* FROM recommendation AS r
            WHERE author_id = :authorId AND receiver_id = :receiverId
            ORDER BY  created_at DESC
            LIMIT 1;
            """)
    Recommendation findLastByAuthorAndReceiver(long authorId, long receiverId);

    boolean existsById(long id);
}
