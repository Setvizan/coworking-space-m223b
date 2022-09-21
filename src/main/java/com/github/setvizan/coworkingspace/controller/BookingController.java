package com.github.setvizan.coworkingspace.controller;

import com.github.setvizan.coworkingspace.exceptions.NoPermissionException;
import com.github.setvizan.coworkingspace.model.BookingEntity;
import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.model.enumerate.Status;
import com.github.setvizan.coworkingspace.service.BookingService;
import com.github.setvizan.coworkingspace.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/bookings")
public class BookingController {

    private final MemberService memberService;
    private final BookingService bookingService;

    public BookingController(BookingService bookingService, MemberService memberService){
        this.bookingService = bookingService;
        this.memberService = memberService;
    }

    @Operation(
            summary = "create booking",
            description = "allows one to create a booking"
    )
    @PostMapping
    ResponseEntity<BookingEntity> createBooking(@RequestBody BookingEntity booking) {
        MemberEntity member = this.memberService.oneById(booking.getMemberId());
        booking.setMember(member);
        booking.setStatus(Status.PENDING);
        return ResponseEntity.ok(this.bookingService.create(booking));
    }

    @Operation(
            summary = "get all bookings",
            description = "allows one to get all bookings, for regular members it only returns their own bookings"
    )
    @GetMapping
    ResponseEntity<List<BookingEntity>> getAllBookings(Authentication authentication) {
        if(isAdmin(authentication)){
            return ResponseEntity.ok(this.bookingService.all());
        }
        UUID memberId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(this.bookingService.allByMemberId(memberId));
    }

    @Operation(
            summary = "get single booking by id",
            description = "allows one to get a single booking"
    )
    @GetMapping("/{id}")
    ResponseEntity<BookingEntity> getBookingById(@PathVariable(name = "id") UUID bookingId, Authentication authentication) {
        BookingEntity booking = this.bookingService.oneById(bookingId);
        if(isAdmin(authentication)){
            return ResponseEntity.ok(booking);
        }
        if(StringUtils.equals(authentication.getName(), booking.getMember().getId().toString())){
            return ResponseEntity.ok(booking);
        }

        throw new NoPermissionException("Not enough permissions");
    }

    @Operation(
            summary = "update single booking by id",
            description = "allows one to update a single booking"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    ResponseEntity<BookingEntity> updateBookingById(@PathVariable(name = "id") UUID bookingId, @RequestBody BookingEntity booking) {
        if(ObjectUtils.isEmpty(booking.getMember())){
            MemberEntity member = this.memberService.oneById(booking.getMemberId());
            booking.setMember(member);
        }
        return ResponseEntity.ok(this.bookingService.update(booking, bookingId));
    }

    @Operation(
            summary = "delete single booking by id",
            description = "allows one to delete a single booking"
    )
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteBookingById(@PathVariable(name = "id") UUID bookingId, Authentication authentication) {
        BookingEntity booking = this.bookingService.oneById(bookingId);
        if(isAdmin(authentication)){
            this.bookingService.delete(booking.getId());
            return ResponseEntity.ok("Successfully removed booking with id " + bookingId);
        }

        if(StringUtils.equals(authentication.getName(), booking.getMember().getId().toString())){
            this.bookingService.delete(booking.getId());
            return ResponseEntity.ok("Successfully removed booking with id " + bookingId);
        }

        throw new NoPermissionException("Not enough permissions");
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
