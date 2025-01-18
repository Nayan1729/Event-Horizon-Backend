package org.springboot.event_horizon.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.RegisterForEventDTO;
import org.springboot.event_horizon.entities.Event;
import org.springboot.event_horizon.entities.RegisterForEvent;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.repositories.RegisterForEventRepository;
import org.springboot.event_horizon.repositories.EventRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterForEventService {
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final RegisterForEventRepository registerForEventRepository;
    private final ModelMapper modelMapper;

    public void registerForEvent(int eventId, RegisterForEvent registerForEvent) throws ApiException {
        User user = this.userService.getLoggedInUser();
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(()->new ApiException("No event found" , 404));

        System.out.println(user);
        boolean isAlreadyRegistered = registerForEventRepository.existsByUserAndEvent(user, event);
        if (isAlreadyRegistered) {
            throw new ApiException("User is already registered for this event", 400);
        }
        registerForEvent.setUser(user);
        registerForEvent.setEvent(event);
        this.registerForEventRepository.save(registerForEvent);
    }

public List<RegisterForEventDTO> getAllRegistrationsByStatus(String status,int eventId) throws ApiException {
    Event e = this.eventRepository.findById(eventId)
            .orElseThrow(()->new ApiException("No such event found" , 404));
    List<RegisterForEvent> registrationForEvent = this.registerForEventRepository.findByStatus(status)
            .orElseThrow(()->new ApiException("No "+status+ "  registrations found" , 404));
    if(registrationForEvent.isEmpty()){
        throw new ApiException("No "+status+ " registrarions found" , 404);
    }
        return registrationForEvent
                .stream()
                .map(registration->{
                    RegisterForEventDTO register =  modelMapper.map(registration, RegisterForEventDTO.class);
                    register.setEventTitle(registration.getEvent().getTitle());
                    System.out.println(register.getEventTitle());
                    register.setUserEmail(registration.getUser().getEmail());
                    System.out.println(register);
                    return register;
                }).collect(Collectors.toList());
        }

    public void approveRegistration(int registerId) throws ApiException {
        RegisterForEvent register =  this.registerForEventRepository.findById(registerId)
                .orElseThrow(()->new ApiException("No such event found" , 404));
        if (register.getStatus().equals("APPROVED")) {
            throw new ApiException("Registration already processed", 400);
        }
        register.setStatus("APPROVED");
        this.registerForEventRepository.save(register);
    }
    public void rejectRegistration(int registerId) throws ApiException {
        RegisterForEvent register =  this.registerForEventRepository.findById(registerId)
                .orElseThrow(()->new ApiException("No such registration found" , 404));
        if (register.getStatus().equals("APPROVED")) {
            throw new ApiException("Registration already processed", 400);
        }
        register.setStatus("REJECTED");
        this.registerForEventRepository.save(register);
    }
}
