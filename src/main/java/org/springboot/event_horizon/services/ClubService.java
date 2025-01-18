package org.springboot.event_horizon.services;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.*;
import org.springboot.event_horizon.entities.*;
import org.springboot.event_horizon.repositories.ClubRepository;
import org.springboot.event_horizon.repositories.MyUserDetailsRepository;
import org.springboot.event_horizon.repositories.RoleRepository;
import org.springboot.event_horizon.utilities.ApiException;
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
}
