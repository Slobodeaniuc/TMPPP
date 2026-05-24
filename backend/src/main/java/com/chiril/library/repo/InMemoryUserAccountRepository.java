package com.chiril.library.repo;

import com.chiril.library.domain.UserAccount;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryUserAccountRepository implements UserAccountRepository {
    private final Map<String, UserAccount> byUsername  = new LinkedHashMap<>();
    private final Map<String, UserAccount> byMemberId  = new LinkedHashMap<>();

    @Override
    public void register(UserAccount account) {
        byUsername.put(account.getUsername(), account);
        byMemberId.put(account.getMemberId(), account);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public Optional<UserAccount> findByMemberId(String memberId) {
        return Optional.ofNullable(byMemberId.get(memberId));
    }

    @Override
    public List<UserAccount> findAll() {
        return new ArrayList<>(byUsername.values());
    }
}
