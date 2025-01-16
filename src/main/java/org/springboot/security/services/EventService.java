package org.springboot.security.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.security.dtos.EventResponseDTO;
import org.springboot.security.entities.Club;
import org.springboot.security.entities.Event;
import org.springboot.security.repositories.ClubRepository;
import org.springboot.security.repositories.EventRepository;
import org.springboot.security.utilities.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final ClubRepository clubRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

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

    public EventResponseDTO getEvent(int eventId) throws ApiException {

        Event eventPresent = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException("No event with the given id found" , 404));
        EventResponseDTO eventResponseDTO = this.modelMapper.map(eventPresent, EventResponseDTO.class);
        eventResponseDTO.setId(eventPresent.getClub().getClubId());
        eventResponseDTO.setClubName(eventPresent.getClub().getName());
        return eventResponseDTO;
    }
    public List<EventResponseDTO> getAllClubsEvents(int clubId) throws ApiException {

        Club club = this.clubRepository.findByClubId(clubId)
                .orElseThrow(() -> new ApiException("No club with the given id found" , 404));

            List<Event> clubEvents = this.eventRepository.findAllByClubId(clubId)
                    .orElseThrow(() -> new ApiException("No events found for the given club found" , 404));
            return clubEvents
                    .stream()
                    .map(event -> {
                        EventResponseDTO e = modelMapper.map(event, EventResponseDTO.class);
                        e.setClubId(event.getClub().getClubId());
                        e.setClubName(event.getClub().getName());
                        return e;
                    }).collect(Collectors.toList());


    }
    public List<EventResponseDTO> getAllUpcomingEvents() throws ApiException {
        List  <Event> upcomingEvents =  this.eventRepository.findByStatus("UPCOMING")
                .orElseThrow(()-> new ApiException("No upcoming events found" , 404));
        return upcomingEvents
                .stream()
                .map(event -> {
                    EventResponseDTO e = modelMapper.map(event, EventResponseDTO.class);
                    e.setClubId(event.getClub().getClubId());
                    e.setClubName(event.getClub().getName());
                    System.out.println(e.getClubName());
                    return e;
                }).collect(Collectors.toList());
    }

    public void changeEventStatus(int eventId, String status) throws ApiException {
        Event e = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException("No event with the given id found" , 404));
        if (!e.getStatus().equals(status)) {
            throw new ApiException("Event status is already "+status , 404);
        }
        e.setStatus(status);
        this.eventRepository.save(e);
    }
}
