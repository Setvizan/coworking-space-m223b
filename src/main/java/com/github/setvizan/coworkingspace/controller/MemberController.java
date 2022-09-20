package com.github.setvizan.coworkingspace.controller;

import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
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

    @Operation(
            summary = "create member",
            description = "allows one to register a new member."
    )
    @PostMapping
    ResponseEntity<MemberEntity> registerMember(@RequestBody MemberEntity member) {
        return ResponseEntity.ok(this.memberService.create(member));
    }

    @Operation(
            summary = "get users",
            description = "return all users"
    )
    @GetMapping
    ResponseEntity<List<MemberEntity>> getMembers() {
        return ResponseEntity.ok(this.memberService.all());
    }

    @Operation(
            summary = "get member by Id",
            description = "return member by id"
    )
    @GetMapping("/{id}")
    ResponseEntity<MemberEntity> getMemberById(@PathVariable(name = "id") UUID memberId){
        return ResponseEntity.ok(this.memberService.oneById(memberId));
    }

    @Operation(
            summary = "update member",
            description = "update a single member"
    )
    @PutMapping("/{id}")
    ResponseEntity<MemberEntity> updateUserById(@PathVariable(name = "id") UUID memberId, @RequestBody MemberEntity member){
        return ResponseEntity.ok(this.memberService.update(member, memberId));
    }

    @Operation(
            summary = "delete member",
            description = "delete a single member"
    )
    @DeleteMapping("/{id}")
    ResponseEntity<String> updateUserById(@PathVariable(name = "id") UUID memberId){
        this.memberService.delete(memberId);
        return ResponseEntity.ok("success");
    }
}
