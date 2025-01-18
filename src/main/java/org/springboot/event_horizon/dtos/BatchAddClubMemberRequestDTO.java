package org.springboot.event_horizon.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springboot.event_horizon.entities.ClubMember;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchAddClubMemberRequestDTO{
    private List<ClubMember> addedMembers;
    private List<FailedMemberResponse> failedMembers;
}
