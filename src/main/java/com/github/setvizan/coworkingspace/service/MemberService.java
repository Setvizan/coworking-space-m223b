package com.github.setvizan.coworkingspace.service;

import com.github.setvizan.coworkingspace.exceptions.MemberHasBookingsException;
import com.github.setvizan.coworkingspace.exceptions.MemberNotFoundException;
import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public MemberEntity create(MemberEntity member){
        log.info("Executing create new member with email {}", member.getEmail());
        return this.memberRepository.save(member);
    }

    public List<MemberEntity> all(){
        log.info("Executing get all members from database");
        return this.memberRepository.findAll();
    }

    public MemberEntity oneById(UUID memberId){
        log.info("Executing get single member by id {}", memberId);
        return this.memberRepository.findById(memberId)
                                    .orElseThrow(
                                            () -> new MemberNotFoundException("Member with id " + memberId + " not found" )
                                    );
    }

    @Transactional
    public MemberEntity update(MemberEntity member, UUID memberId){
        log.info("Execute update member by Id {}", memberId);
        this.oneById(memberId);
        member.setId(memberId);
        return this.memberRepository.save(member);
    }

    @Transactional
    public void delete(UUID memberId){
        MemberEntity member = oneById(memberId);
        if(!member.getBookings().isEmpty()){
            throw new MemberHasBookingsException("Member still has open bookings");
        }
        this.memberRepository.delete(member);
    }

    public MemberEntity oneByEmail(String email){
        return this.memberRepository.findByEmail(email)
                                    .orElseThrow(
                                            () -> new MemberNotFoundException("Member with email " + email + " not found" )
                                    );
    }
}
