package com.github.setvizan.coworkingspace.repository;

import com.github.setvizan.coworkingspace.model.MemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface MemberRepository extends CrudRepository<MemberEntity, UUID> {
    List<MemberEntity> findAll();
}
