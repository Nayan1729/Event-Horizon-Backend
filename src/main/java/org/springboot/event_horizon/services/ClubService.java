package org.springboot.event_horizon.services;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.*;
import org.springboot.event_horizon.entities.*;
import org.springboot.event_horizon.repositories.*;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService{

    //Fix the output of the adding clubMembers
    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final MyUserDetailsRepository userDetailsRepository;
    private final MyUserDetailsRepository myUserDetailsRepository;
    private final MyUserDetailsRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final EventRepository eventRepository;
    private final RegisterForEventService registerForEventService;
    @Autowired  @Lazy
    private EventService eventService;

    public Club registerClub(RegisterClubRequest request, String email) throws ApiException {
        try{
            if(this.clubRepository.findByName(request.getName()).isPresent()){
                throw new ApiException("Club or club request with the same name already exists" , HttpStatus.CONFLICT.value());
            }
            //Source , Destination
            // Basically argument1 ==> argument2
            Club club = this.modelMapper.map(request, Club.class);
            club.setEmail(email);
            club.setStatus("PENDING");
            this.clubRepository.save(club);
            // Upgrade role of the user
            return club;
        }catch (ApiException e){
            throw new ApiException(e.getMessage() , e.getStatusCode());
        }catch (Exception e){
            throw new ApiException(e.getMessage() , 500);
        }
    }

    public Club approveClubRequest(int id) throws ApiException {

            Optional<Club> club = this.clubRepository.findByClubId(id);

            if(!club.isPresent()){
                throw new ApiException("No club with the given id found",HttpStatus.NOT_FOUND.value());
            }
            club.get().setStatus("APPROVED");
            club.get().setUpdatedAt(new Date());
            Club approvedClub = this.clubRepository.save(club.get());
            User currentUser = myUserDetailsRepository.findByEmail(club.get().getEmail()).get();
            System.out.println(currentUser);
            Set<Role> userRoles = currentUser.getRoles();

            Optional<Role> clubAdminRole = roleRepository.findByName(RoleName.CLUB_ADMIN);

            if (clubAdminRole.isPresent() && userRoles.stream().noneMatch(role -> role.getName().equals(clubAdminRole.get().getName()))) {
                userRoles.add(clubAdminRole.get());
                myUserDetailsRepository.save(currentUser);
            }
        return approvedClub;
    }
    public Club rejectClubRequest(int id) throws ApiException {

        Optional<Club> club = this.clubRepository.findByClubId(id);
        if(!club.isPresent()){
            throw new ApiException("No club with the given id found",400);
        }
        club.get().setStatus("REJECTED");
        club.get().setUpdatedAt(new Date());
        Club approvedClub = this.clubRepository.save(club.get());
        return approvedClub;
    }
    public BatchAddClubMemberRequestDTO addClubMembers(int clubId, List<AddMembersRequestDTO>membersRequest) throws ApiException {
            Optional<Club> existingClub = this.clubRepository.findByClubId(clubId);

            if(!existingClub.isPresent()){
                throw new ApiException("No club with the given id found",404);
            }
            Club club = existingClub.get();
            List<ClubMember> addedMembers = new ArrayList<>();
            List<FailedMemberResponse> failedMemberResponses = new ArrayList<>();

            for (AddMembersRequestDTO member : membersRequest){
                Optional<User> presentUser = this.userRepository.findByEmail(member.getEmail());
                if(club.getEmail() == member.getEmail() ){
                    failedMemberResponses.add(new FailedMemberResponse(member.getEmail() , "Cant the club's email"));
                    continue;
                }
                if(!presentUser.isPresent()){
                    failedMemberResponses.add(new FailedMemberResponse(member.getEmail() , "User with this email doesn't exist"));
                    continue;
                }

                    User user = presentUser.get();
                // Check if the user is already a member of the club
                boolean isAlreadyMember = club.getClubMembers().stream()
                        .anyMatch(m -> m.getUser().getId() == user.getId());
                    if(isAlreadyMember){
                        failedMemberResponses.add(new FailedMemberResponse(member.getEmail() , "User with this email already exists"));
                        continue;
                    }
                    ClubMember newMember = new ClubMember();
                    newMember.setClub(club);
                    newMember.setUser(user);
                    newMember.setDesignation(member.getDesignation());
                    club.getClubMembers().add(newMember);
                    clubRepository.save(club);
                    addedMembers.add(newMember);
            }
           return new BatchAddClubMemberRequestDTO(addedMembers,failedMemberResponses);
    }

    public Set<EventSummaryDTO> getClubEvents(int clubId) throws ApiException {
        Optional<Club> existingClub = this.clubRepository.findByClubId(clubId);
        if(!existingClub.isPresent()){
            throw new ApiException("No club with the given id found",404);
        }
        Club club = existingClub.get();
        Set<Event> events = club.getEvents();
        if(events.isEmpty()){
            throw new ApiException("No club events found",404);
        }

        // Filter and map pending events
        return events.stream()
                .map(event -> {
                    int pendingCount = (int) event.getRegisterForEvents().stream()
                            .filter(registration -> "PENDING".equals(registration.getStatus()))
                            .count();


                        EventSummaryDTO eventSummary = modelMapper.map(event, EventSummaryDTO.class);
                        eventSummary.setPendingRegistrations(pendingCount); // Set the derived field
                        return eventSummary;

                     // Skip events with no pending registrations
                })
                .filter(Objects::nonNull) // Remove null values from skipped events
                .collect(Collectors.toSet());
    }

    public List<ClubDTO> getAllClubs()throws ApiException {
        List<Club> clubs = this.clubRepository.findByStatus("APPROVED")
                .orElseThrow(()->new ApiException("No clubs found" , 404));
        return clubs
                .stream()
                 .map(club -> {
                   ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
                   clubDTO.setMembersCount(club.getClubMembers().size());
                   clubDTO.setEventsCount(club.getEvents().size());
                   return clubDTO;
                })
                .collect(Collectors.toList());
    }

    public int getClubIdByEmail(String email) throws ApiException {
        if(email == null || email.isEmpty()){
            throw new ApiException("Email cannot be empty",400);
        }
        return clubRepository.findByEmail(email).getClubId();
    }


    public ClubDetailsDTO getClubDetails(int clubId) throws ApiException {
        Club club = this.clubRepository.findByClubId(clubId).get();
        ClubDetailsDTO clubDetails =  modelMapper.map(club, ClubDetailsDTO.class);
        System.out.println(clubDetails);
        List<ClubMemberDTO> clubMemberDTOS = this.getClubMembers(clubId);
        System.out.println("clubMemberDTOS: " + clubMemberDTOS);
        System.out.println(clubMemberDTOS);
        clubDetails.setMembers(clubMemberDTOS);
        System.out.println(clubDetails);
        clubDetails.setMembersCount(club.getClubMembers().size());
        List<EventResponseDTO> events = this.eventService.getAllClubsEvents(clubId);
        clubDetails.setEventsCount(events.size());
        clubDetails.setEventsDTO(events);
        return clubDetails;
    }

    public List<ClubMemberDTO> getClubMembers(int clubId) throws ApiException {
        Optional<List<ClubMember>> clubMembers = this.clubMemberRepository.findByClubClubId(clubId);
         
        return  clubMembers.get().stream().map(clubMember -> {
           ClubMemberDTO clubMemberDTO =  this.modelMapper.map(clubMember, ClubMemberDTO.class);
           clubMemberDTO.setEmail(clubMember.getUser().getEmail());
//           clubMemberDTO.setName(clubMember.getUser().getName());
            return clubMemberDTO;
        }).toList();
    }

    public List<ClubRequestDTO> getAllClubRequests() throws ApiException {
       Optional<List<Club>> clubRequests =  this.clubRepository.findByStatus("PENDING");
       if(!clubRequests.isPresent()){
           throw new ApiException("No pending club requests found",404);
       }

       List<Club> clubs = clubRequests.get();
       if(clubs.isEmpty()){
           throw new ApiException("No clubs found",404);
       }
        System.out.println("clubRequests: " + clubs);
      return clubs.stream().map(club -> modelMapper.map(club, ClubRequestDTO.class)).collect(Collectors.toList());
    }


    public List<RegistrationDTO> getAllPendingEventRegistrationsOfClub(String email) throws ApiException {
        int clubId = this.getClubIdByEmail(email);
        List<Event> events = this.eventRepository.findAllByClubId(clubId)
                .orElseThrow(()->new ApiException("No events found ",404));
       List<Event> filteredEvents =  events.stream().filter(e->!e.getStatus().equals("PAST")).toList();
        System.out.println(filteredEvents);
       return filteredEvents.stream().map(e->{
           RegistrationDTO registrationDTO = modelMapper.map(e, RegistrationDTO.class);
            try {
                registrationDTO.setPendingRegistrations(registerForEventService.getAllRegistrationsByStatus("PENDING" ,e.getId() ));
            } catch (ApiException ex) {
                throw new RuntimeException(ex);
            }
            return registrationDTO;
        }).toList();
    }
}


