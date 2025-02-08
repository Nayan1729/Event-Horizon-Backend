package org.springboot.event_horizon.services;

import jakarta.validation.Valid;
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
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
    @Autowired @Lazy
    private  ClubService clubService;

    public Event registerEvent(int clubId,Event event) throws ApiException {

            Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
            if(!clubPresent.isPresent()){
                throw new ApiException("No club with the given id found" , 404);
            }
        Optional<Event> presentEvent = this.eventRepository.findByTitle(event.getTitle());
            if(presentEvent.isPresent()){
                throw new ApiException("Event with title " + event.getTitle() + " already exists" , 404);
            }
            event.setTotalAttendance(0);
            event.setClub(clubPresent.get());
        return this.eventRepository.save(event);
    }

    public void deleteEvent( int eventId) throws ApiException {
        User currentUser = userService.getLoggedInUser();
        int clubId = clubService.getClubIdByEmail(currentUser.getEmail());
        Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);

        if(!clubPresent.isPresent()){
            throw new ApiException("U are not allowed to delete an event", 404);
        }
        Club club = clubPresent.get();

        Optional<Event> event = this.eventRepository.findById(eventId);


        if(!event.isPresent()){
            throw new ApiException("No event with the given id found" , 404);
        }
        Event eventPresent = event.get();
        if(eventPresent.getClub().getClubId() != clubId){
            throw new ApiException("U are not allowed to delete this event", 401);
        }
    }

    public EventResponseDTO getEvent(int eventId) throws ApiException {

        Event eventPresent = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException("No event with the given id found" , 404));
        System.out.println(eventPresent.getSpeakers());
        EventResponseDTO eventResponseDTO = this.modelMapper.map(eventPresent, EventResponseDTO.class);
        System.out.println(eventResponseDTO.getSpeakers());
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

    public int updateEvent( Event event) throws ApiException {
        User currentUser = userService.getLoggedInUser();
        int clubId = clubService.getClubIdByEmail(currentUser.getEmail());
        System.out.println(clubId);
        Optional<Club> clubPresent = this.clubRepository.findByClubId(clubId);
        Club club = clubPresent.get();
        System.out.println(club.getClubId());
        Optional<Event> dbEvent = this.eventRepository.findById(event.getId());
        if(!dbEvent.isPresent()){
            throw new ApiException("No event with the given id found" , 404);
        }
        Event presentEvent = dbEvent.get();
        System.out.println(presentEvent.getClub().getClubId());
        event.setClub(clubPresent.get());
        if(presentEvent.getClub().getClubId() != clubId){
            throw new ApiException("U are not allowed to update this event", 401);
        }
        System.out.println("Update event");
        if(presentEvent.getCompletedRegistrations()>event.getTotalRegistrations()) {
            throw new ApiException("Invalid value of eventRegistrations", 404);
        }
        presentEvent.setTitle(event.getTitle());
        presentEvent.setDescription(event.getDescription());
        presentEvent.setLocation(event.getLocation());
        presentEvent.setDate(event.getDate());
        presentEvent.setStartTime(event.getStartTime());
        presentEvent.setEndTime(event.getEndTime());
        presentEvent.setStatus(event.getStatus());
        presentEvent.setTotalRegistrations(event.getTotalRegistrations());
        presentEvent.setSpeakers(event.getSpeakers());
         return this.eventRepository.save(event).getId();
    }
}
