package org.springboot.security.services;

import org.modelmapper.ModelMapper;
import org.springboot.security.dtos.RegisterClubRequest;
import org.springboot.security.entities.*;
import org.springboot.security.repositories.ClubRepository;
import org.springboot.security.repositories.MyUserDetailsRepository;
import org.springboot.security.repositories.RoleRepository;
import org.springboot.security.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MyUserDetailsRepository userDetailsRepository;
    @Autowired
    private MyUserDetailsRepository myUserDetailsRepository;


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
// User currentUser = this.myUserDetailsRepository.findByEmail(email);
//            Set<Role> userRoles = currentUser.getRoles();
//            Optional<Role> clubAdminRole = roleRepository.findByName(RoleName.CLUB_ADMIN);
//            if(userRoles.stream().noneMatch(role -> role.getName().equals("ROLE_CLUB_ADMIN"))){
//                // Since the role is optional
//                userRoles.add(clubAdminRole.get());
//                myUserDetailsRepository.save(currentUser);
//                System.out.println("Role and club added");
//            }
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
            throw new ApiException("No club with the given id found",400);
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
}
