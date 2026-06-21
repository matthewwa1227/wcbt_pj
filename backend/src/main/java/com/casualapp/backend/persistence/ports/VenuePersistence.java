package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.Venue;

import java.util.List;
import java.util.Optional;

public interface VenuePersistence {
    Optional<Venue> findById(Long venueId);

    Optional<Venue> findByName(String name);

    List<Venue> findAll();

    List<Venue> findByDistrictId(Long districtId);

    Venue save(Venue venue);

    Venue update(Venue venue);

    boolean deleteById(Long venueId);
}