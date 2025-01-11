package org.springboot.security.services;

import lombok.RequiredArgsConstructor;
import org.springboot.security.entities.Club;
import org.springboot.security.entities.Event;
import org.springboot.security.repositories.ClubRepository;
import org.springboot.security.repositories.EventRepository;
import org.springboot.security.utilities.ApiException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final ClubRepository clubRepository;
    private final EventRepository eventRepository;

    public Event registerEvent(int clubId,Event event) throws ApiException {

            Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
            if(!clubPresent.isPresent()){
                throw new ApiException("No club with the given id found" , 404);
            }
            event.setClub(clubPresent.get());
        return this.eventRepository.save(event);
    }

    public void deleteEvent(int clubId, int eventId) throws ApiException {
        Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
        if(!clubPresent.isPresent()){
            throw new ApiException("No club with the given id found" , 404);
        }
        Optional<Event> event = this.eventRepository.findById(eventId);
        if(!event.isPresent()){
            throw new ApiException("No event with the given id found" , 404);
        }
        this.eventRepository.delete(event.get());
    }

    public Event getEvent(int clubId, int eventId) throws ApiException {

        Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
        if(!clubPresent.isPresent()){
            throw new ApiException("No club with the given id found" , 404);
        }
        Optional<Event> eventPresent = this.eventRepository.findById(eventId);
        if(!eventPresent.isPresent()){
            throw  new ApiException("No Club with the given id Found",404);
        }
        return eventPresent.get();
    }
}
