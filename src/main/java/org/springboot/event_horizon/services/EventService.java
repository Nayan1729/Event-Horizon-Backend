package org.springboot.event_horizon.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.EventResponseDTO;
import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.entities.Event;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.repositories.ClubRepository;
import org.springboot.event_horizon.repositories.EventRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Autowired @Lazy
    private  RegisterForEventService registerForEventService;
    private final UserService userService;

    public Event registerEvent(int clubId,Event event) throws ApiException {

            Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
            if(!clubPresent.isPresent()){
                throw new ApiException("No club with the given id found" , 404);
            }
            event.setTotalAttendance(0);
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
    public List<EventResponseDTO> getAllEvents() throws ApiException {
        List <Event> upcomingEvents =  this.eventRepository.findAll();
        User user = this.userService.getLoggedInUser();
        return upcomingEvents
                .stream()
                .map(event -> {
                    EventResponseDTO e = modelMapper.map(event, EventResponseDTO.class);
                    e.setClubId(event.getClub().getClubId());
                    e.setClubName(event.getClub().getName());
                    e.setRegistered(registerForEventService.isUserRegisteredForEvent(user,event.getId()));
                    System.out.println(e.isRegistered());
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
