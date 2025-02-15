package org.springboot.event_horizon.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.AttendanceDTO;
import org.springboot.event_horizon.dtos.RegisterForEventDTO;
import org.springboot.event_horizon.entities.Event;
import org.springboot.event_horizon.entities.RegisterForEvent;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.repositories.RegisterForEventRepository;
import org.springboot.event_horizon.repositories.EventRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterForEventService {
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final RegisterForEventRepository registerForEventRepository;
    @Autowired
    @Lazy
    private ClubService clubService;
    private final ModelMapper modelMapper;
    @Autowired
    private AwsService awsService;

    public void registerForEvent(int eventId, RegisterForEvent registerForEvent) throws ApiException {
        User user = this.userService.getLoggedInUser();
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(()->new ApiException("No event found" , 404));
        if(event.getCompletedRegistrations() == event.getTotalRegistrations()){
            throw new ApiException("No more slots remaining" , 409);
        }
        System.out.println(user);
        boolean isAlreadyRegistered = this.isUserRegisteredForEvent(user,eventId);
        if (isAlreadyRegistered) {
            throw new ApiException("User is already registered for this event", 400);
        }
        event.setCompletedRegistrations(event.getCompletedRegistrations() + 1);
        registerForEvent.setUser(user);
        registerForEvent.setEvent(event);
        this.registerForEventRepository.save(registerForEvent);
    }

public List<RegisterForEventDTO> getAllRegistrationsByStatus(String status,int eventId) throws ApiException {

    Event e = this.eventRepository.findById(eventId)
            .orElseThrow(()->new ApiException("No such event found" , 404));
    List<RegisterForEvent> registrationForEvent = this.registerForEventRepository.findByStatusAndEventId(status ,eventId ).get();

    System.out.println(registrationForEvent);

        return registrationForEvent
                .stream()
                .map(registration->{
                    RegisterForEventDTO register =  modelMapper.map(registration, RegisterForEventDTO.class);
                    register.setId(registration.getId());
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
        if (register.getStatus().equals("APPROVED")){
            throw new ApiException("Registration already processed", 400);
        }
        System.out.println(register.getEvent().getCompletedRegistrations());
        register.setStatus("APPROVED");
        register.getEvent().setCompletedRegistrations(register.getEvent().getCompletedRegistrations() + 1);
        System.out.println(register.getEvent().getCompletedRegistrations());
        this.registerForEventRepository.save(register);
    }
    public void rejectRegistration(int registerId) throws ApiException {
        RegisterForEvent register =  this.registerForEventRepository.findById(registerId)
                .orElseThrow(()->new ApiException("No such registration found" , 404));
        if (register.getStatus().equals("APPROVED")){
            throw new ApiException("Registration already processed", 400);
        }
        register.setStatus("REJECTED");
        this.registerForEventRepository.save(register);
    }
    public boolean isUserRegisteredForEvent(User loggedInUser,int eventId){
        return this.registerForEventRepository.existsByUserIdAndEventIdAndStatus(loggedInUser.getId() , eventId , "ACCEPTED");
    }

    public List<AttendanceDTO> getUnattendedRegistrations(int eventId) throws ApiException {
        User currentUser = this.userService.getLoggedInUser();
         Event event = this.eventRepository.findById(eventId).orElseThrow(()->new ApiException("No such event found" , 404));
         if(!event.getClub().getEmail().equals(currentUser.getEmail())){
             throw new ApiException("You are not allowed to make this request" , 400);
         }
        List <RegisterForEvent> registerForEvents = this.registerForEventRepository.findByStatusAndEventId("APPROVED" , eventId )
                .orElseThrow(()->new ApiException("No registrations found" , 404));
         String title = event.getTitle();
         List<AttendanceDTO> attendances = new ArrayList<>();
        registerForEvents.stream().forEach(register->{
            AttendanceDTO attendance = new AttendanceDTO();
            attendance.setId(register.getId());
            attendance.setName(register.getUser().getName());
            attendance.setEmail(register.getUser().getEmail());
            attendance.setTitle(title);
            attendance.setImageUrl(awsService.getImageUrl(register.getUser().getImageUrl()));
            attendance.setAttendanceStatus(register.isAttended());
            attendances.add(attendance);
        });
        return attendances;
    }

    public AttendanceDTO acceptAttendance(int registerId) throws ApiException {
        RegisterForEvent registerForEvent   = this.registerForEventRepository.findById(registerId)
                .orElseThrow(()->new ApiException("No such registration found" , 404));
        if(!registerForEvent.getStatus().equals("APPROVED")){
            throw new ApiException("Your registration was not accepted", 400);
        }
        Event event = registerForEvent.getEvent();
        registerForEvent.setAttended(true);
        event.setTotalAttendance(event.getTotalAttendance() + 1);
        System.out.println(registerForEvent.getEvent().getTotalAttendance());
         this.registerForEventRepository.save(registerForEvent);
         AttendanceDTO attendance = new AttendanceDTO();
         attendance.setId(registerForEvent.getEvent().getId());
         attendance.setName(registerForEvent.getUser().getName());
         attendance.setEmail(registerForEvent.getUser().getEmail());
         attendance.setTitle(registerForEvent.getEvent().getTitle());
         attendance.setImageUrl(awsService.getImageUrl(registerForEvent.getUser().getImageUrl()));
         attendance.setAttendanceStatus(registerForEvent.isAttended());
         return attendance;
    }
}

