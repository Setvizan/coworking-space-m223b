package com.github.setvizan.coworkingspace.repository;

import com.github.setvizan.coworkingspace.model.BookingEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends CrudRepository<BookingEntity, UUID> {
    List<BookingEntity> findAll();

    @Query("SELECT b FROM BookingEntity b where b.member.id = :memberId")
    List<BookingEntity> findByMemberId(UUID memberId);
}
