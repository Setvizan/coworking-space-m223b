package com.github.setvizan.coworkingspace.controller;

import com.github.setvizan.coworkingspace.exceptions.NoPermissionException;
import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    /*
    * Create user moved to AuthController
    */

    @Operation(
            summary = "get users",
            description = "return all users"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    ResponseEntity<List<MemberEntity>> getMembers() {
        return ResponseEntity.ok(this.memberService.all());
    }

    @Operation(
            summary = "get member by Id",
            description = "return member by id"
    )
    @GetMapping("/{id}")
    ResponseEntity<MemberEntity> getMemberById(@PathVariable(name = "id") UUID memberId, Authentication authentication){
        if(isAdmin(authentication)){
            return ResponseEntity.ok(this.memberService.oneById(memberId));
        }

        if(StringUtils.equals(authentication.getName(), memberId.toString())){
            return ResponseEntity.ok(this.memberService.oneById(memberId));
        }
        throw new NoPermissionException("Not enough permissions");
    }

    @Operation(
            summary = "update member",
            description = "update a single member"
    )
    @PutMapping("/{id}")
    ResponseEntity<MemberEntity> updateUserById(@PathVariable(name = "id") UUID memberId, @RequestBody MemberEntity member, Authentication authentication){
        if(isAdmin(authentication)){
            return ResponseEntity.ok(this.memberService.update(member, memberId));
        }

        if(StringUtils.equals(authentication.getName(), memberId.toString())){
            return ResponseEntity.ok(this.memberService.update(member, memberId));
        }
        throw new NoPermissionException("Not enough permissions");
    }

    @Operation(
            summary = "delete member",
            description = "delete a single member"
    )
    @DeleteMapping("/{id}")
    ResponseEntity<String> updateUserById(@PathVariable(name = "id") UUID memberId, Authentication authentication){
        if(isAdmin(authentication)){
            this.memberService.forceDelete(memberId);
            return ResponseEntity.ok("Successfully forcefully removed member with id " + memberId);
        }

        if(StringUtils.equals(authentication.getName(), memberId.toString())){
            this.memberService.delete(memberId);
            return ResponseEntity.ok("Successfully removed member with id " + memberId);
        }

        throw new NoPermissionException("Not enough permissions");
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
