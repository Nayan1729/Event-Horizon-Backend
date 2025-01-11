package org.springboot.security.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springboot.security.entities.ClubMember;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchAddClubMemberRequestDTO{
    private List<ClubMember> addedMembers;
    private List<FailedMemberResponse> failedMembers;
}
