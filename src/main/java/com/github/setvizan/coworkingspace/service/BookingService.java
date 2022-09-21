package com.github.setvizan.coworkingspace.service;

import com.github.setvizan.coworkingspace.exceptions.BookingNotFoundException;
import com.github.setvizan.coworkingspace.model.BookingEntity;
import com.github.setvizan.coworkingspace.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    public BookingEntity create(BookingEntity booking){
        log.info("Executing create booking for user {}", booking.getMember().getId());
        return this.bookingRepository.save(booking);
    }

    public List<BookingEntity> all(){
        log.info("Executing get all bookings");
        return this.bookingRepository.findAll();
    }

    public List<BookingEntity> allByMemberId(UUID memberId){
        log.info("Executing get all by member id");
        return this.bookingRepository.findAllByMemberId(memberId);
    }

    public BookingEntity oneById(UUID bookingId){
        log.info("Executing get single booking by id {}", bookingId);
        return this.bookingRepository.findById(bookingId)
                                    .orElseThrow(
                                            () -> new BookingNotFoundException("Booking with id " + bookingId + " not found" )
                                    );
    }

    public BookingEntity update(BookingEntity booking, UUID bookingId){
        log.info("Execute update booking by Id {}", bookingId);
        this.oneById(bookingId);
        booking.setId(bookingId);
        return this.bookingRepository.save(booking);
    }

    public void delete(UUID bookingId){
        log.info("Execute delete booking by Id {}", bookingId);
        this.bookingRepository.deleteById(bookingId);
    }
}
