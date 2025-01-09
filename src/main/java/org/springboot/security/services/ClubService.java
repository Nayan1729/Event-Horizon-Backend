package org.springboot.security.services;

import org.modelmapper.ModelMapper;
import org.springboot.security.dtos.RegisterClubRequest;
import org.springboot.security.entities.Club;
import org.springboot.security.entities.Role;
import org.springboot.security.entities.RoleName;
import org.springboot.security.entities.User;
import org.springboot.security.repositories.ClubRepository;
import org.springboot.security.repositories.MyUserDetailsRepository;
import org.springboot.security.repositories.RoleRepository;
import org.springboot.security.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
            if(this.clubRepository.findClubByName(request.getName()).isPresent()){
                throw new ApiException("Club with the same name already exists" , HttpStatus.CONFLICT.value());
            }
            //Source , Destination
            // Basically argument1 ==> argument2
            Club club = this.modelMapper.map(request, Club.class);
            club.setEmail(email);
            this.clubRepository.save(club);
            User currentUser = this.myUserDetailsRepository.findByEmail(email);
            Set<Role> userRoles = currentUser.getRoles();
            Optional<Role> clubAdminRole = roleRepository.findByName(RoleName.CLUB_ADMIN);
            if(userRoles.stream().noneMatch(role -> role.getName().equals("ROLE_CLUB_ADMIN"))){
                // Since the role is optional
                userRoles.add(clubAdminRole.get());
                myUserDetailsRepository.save(currentUser);
                System.out.println("Role and club added");
            }
            return club;
        }catch (ApiException e){
            throw new ApiException(e.getMessage() , e.getStatusCode());
        }catch (Exception e){
            throw new ApiException(e.getMessage() , 500);
        }
    }
}
