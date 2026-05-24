package com.chiril.library.domain;

public final class UserAccount {
    private final String memberId;
    private final String username;
    private final String passwordHash;
    private final String displayName;
    private final String email;

    public UserAccount(String memberId, String username, String passwordHash,
                       String displayName, String email) {
        this.memberId = memberId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.email = email;
    }

    public String getMemberId()    { return memberId; }
    public String getUsername()    { return username; }
    public String getPasswordHash(){ return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getEmail()       { return email; }
}
