package com.chiril.library.repo;

import com.chiril.library.domain.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    void register(UserAccount account);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByMemberId(String memberId);
    List<UserAccount> findAll();
}
