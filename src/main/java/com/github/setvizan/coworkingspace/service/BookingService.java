package com.github.setvizan.coworkingspace.service;

import com.github.setvizan.coworkingspace.model.BookingEntity;
import com.github.setvizan.coworkingspace.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    public List<BookingEntity> getBookingEntities(){
        return bookingRepository.findAll();
    }
}
