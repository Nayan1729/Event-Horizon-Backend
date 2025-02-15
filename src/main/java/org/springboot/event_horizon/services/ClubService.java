package org.springboot.event_horizon.services;
import jakarta.validation.Valid;
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
    @Autowired
    private AwsService awsService;

    @Autowired @Lazy
    private UserService userService;


    public Club registerClub(RegisterClubRequest request , String email) throws ApiException {

            if(this.clubRepository.findByName(request.getName()).isPresent()){
                throw new ApiException("Club or club request with the same name already exists" , HttpStatus.CONFLICT.value());
            }
        if(this.clubRepository.findByEmail(request.getName()).isPresent()){
            throw new ApiException("Cant register yourself as another club" , HttpStatus.CONFLICT.value());
        }
            //Source , Destination
            // Basically argument1 ==> argument2
            Club club = this.modelMapper.map(request, Club.class);
            String image = awsService.saveImageToS3(request.getIcon());
            System.out.println(image);
            club.setIcon(image);
            club.setEmail(email);
            club.setStatus("PENDING");
            Club registeredClub = this.clubRepository.save(club);
            registeredClub.setIcon(awsService.getImageUrl(image));
            return registeredClub;
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

//    public Set<EventSummaryDTO> getClubEvents(int clubId) throws ApiException {
//        Optional<Club> existingClub = this.clubRepository.findByClubId(clubId);
//        if(!existingClub.isPresent()){
//            throw new ApiException("No club with the given id found",404);
//        }
//        Club club = existingClub.get();
//        Set<Event> events = club.getEvents();
//        if(events.isEmpty()){
//            throw new ApiException("No club events found",404);
//        }
//
//        // Filter and map pending events
//        return events.stream()
//                .map(event -> {
//                    int pendingCount = (int) event.getRegisterForEvents().stream()
//                            .filter(registration -> "PENDING".equals(registration.getStatus()))
//                            .count();
//
//                        EventSummaryDTO eventSummary = modelMapper.map(event, EventSummaryDTO.class);
//                        eventSummary.setPendingRegistrations(pendingCount); // Set the derived field
//                        return eventSummary;
//
//                     // Skip events with no pending registrations
//                })
//                .filter(Objects::nonNull) // Remove null values from skipped events
//                .collect(Collectors.toSet());
//    }
    public ClubMemberDTO addClubMember(int clubId , AddClubMembersRequestDTO addClubMembersRequestDTO) throws ApiException {
        User currentUser  = userService.getLoggedInUser();
        Club club = this.clubRepository.findByClubId(clubId).get();
        if(!club.getEmail().equals(currentUser.getEmail())){
            throw new ApiException("Cant add members to a different club",400);
        }
        User user = userService.getUserByEmail(addClubMembersRequestDTO.getEmail())
                .orElseThrow(()-> new ApiException("User with the above email doesn't exist",404));
        if(club.getClubMembers().stream().anyMatch(member->
            member.getUser().getEmail().equals(addClubMembersRequestDTO.getEmail()))){
            throw new ApiException(user.getEmail()+" is already a member of the club",400);
        }
        ClubMember clubMember = new ClubMember();
        clubMember.setClub(club);
        clubMember.setUser(user);
        clubMember.setDesignation(addClubMembersRequestDTO.getDesignation());
        club.getClubMembers().add(clubMember);
        this.clubMemberRepository.save(clubMember);
        ClubMemberDTO clubMemberDTO = modelMapper.map(addClubMembersRequestDTO, ClubMemberDTO.class);
        clubMemberDTO.setName(user.getName());
        clubMemberDTO.setImageUrl(user.getImageUrl());
        clubMemberDTO.setId(clubMember.getId());
        return clubMemberDTO;
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
                   clubDTO.setIcon(awsService.getImageUrl(club.getIcon()));
                   return clubDTO;
                })
                .collect(Collectors.toList());
    }

    public Club getClubByEmail(String email) throws ApiException {
        if(email == null || email.trim().isEmpty()){
            throw new ApiException("Email cannot be empty",400);
        }
        return clubRepository.findByEmail(email).orElseThrow(()->new ApiException("No club with the given email doesn't exist",404));
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
        clubDetails.setIcon(awsService.getImageUrl(club.getIcon()));
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
        int clubId = this.getClubByEmail(email).getClubId();
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

    public int updateClubDetails(int clubId, RegisterClubRequest clubRequest) throws ApiException {
        User user = this.userService.getLoggedInUser();
        Club club = this.clubRepository.findByClubId(clubId).get();
        String clubEmail = club.getEmail();
        if (!clubEmail.equals(user.getEmail())) {
            throw new ApiException("You can't edit this club",401);
        }
        club.setName(clubRequest.getName());
        club.setDescription(clubRequest.getDescription());
        if(clubRequest.getIcon() != null){
            club.setIcon(awsService.saveImageToS3(clubRequest.getIcon()));
        }
        this.clubRepository.save(club);
        return club.getClubId();
    }
    public int deleteClub(int clubId) throws ApiException {
        Club club = this.clubRepository.findByClubId(clubId).orElseThrow(()->new ApiException("No club found with the given id",404));
        User user = this.userService.getLoggedInUser();

        if (!user.getEmail().equals(club.getEmail())) {
            throw new ApiException("You can't delete this club",401);
        }
        awsService.deleteImageFromS3(club.getIcon());
        Role clubAdminRole = roleRepository.findByName(RoleName.valueOf("CLUB_ADMIN"))
                .orElseThrow(()->new ApiException("Can't find the role admin",404));
        if (user.getRoles().contains(clubAdminRole)) {
            user.getRoles().remove(clubAdminRole);
            this.userRepository.save(user);
        }
        this.clubRepository.delete(club);
        return clubId;
    }

    public void deleteClubMember(int id) throws ApiException {
        ClubMember member = this.clubMemberRepository.findById(id)
                .orElseThrow(()->new ApiException("No member found with the given id",404));
        User user = this.userService.getLoggedInUser();
        Club club = member.getClub();
        if (!user.getEmail().equals(club.getEmail())) {
            throw new ApiException("You can't delete this member",404);
        }
        club.getClubMembers().remove(member);
        this.clubMemberRepository.delete(member);
    }

    public ClubMemberDTO editClubMember(@Valid ClubMemberDTO memberRequest) throws ApiException {
        User currentUser = this.userService.getLoggedInUser();
        ClubMember previousMember = this.clubMemberRepository.findById(memberRequest.getId()).get();
        Club club = previousMember.getClub();
        if(!currentUser.getEmail().equals(club.getEmail())){
            throw new ApiException("You can't edit this club",404);
        }
        User userInEmail = this.userRepository.findByEmail(memberRequest.getEmail())
                .orElseThrow(()->new ApiException("No user found for the given email",404));

        if(club.getClubMembers().stream().anyMatch(clubMember ->
                clubMember.getUser().getEmail().equals(userInEmail.getEmail()))){
            throw new ApiException(userInEmail.getEmail()+" is already a member of the club",400);
        }

        previousMember.setUser(userInEmail);
        previousMember.setDesignation(memberRequest.getDesignation());
        ClubMember editedMember =  this.clubMemberRepository.save(previousMember);

        // Already has email and designation set
        memberRequest.setName(editedMember.getUser().getName());
        memberRequest.setId(editedMember.getId());
        memberRequest.setImageUrl(editedMember.getUser().getImageUrl());

        return memberRequest;
    }

    public List<EventSummaryDTO> getAllClubEventsByStatus(String status) throws ApiException {
        User currentUser = this.userService.getLoggedInUser();
        Club club = this.getClubByEmail(currentUser.getEmail());
        return eventService.getAllClubsEventsByStatus(club.getClubId(), status);
    }
}