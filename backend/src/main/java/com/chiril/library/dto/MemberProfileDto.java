package com.chiril.library.dto;

import com.chiril.library.domain.UserAccount;

public record MemberProfileDto(String memberId, String displayName, String email) {
    public static MemberProfileDto from(UserAccount account) {
        return new MemberProfileDto(account.getMemberId(), account.getDisplayName(), account.getEmail());
    }
}
