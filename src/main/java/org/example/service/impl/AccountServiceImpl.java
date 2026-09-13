package org.example.service.impl;

import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AccountService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return accountRepository.findByUsername(username);
    }

    @Override
    public void create(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account không được null");
        }

        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username không được để trống");
        }

        if (existsByUsername(account.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        if (account.getPassword() != null && !account.getPassword().startsWith("$2a$")) {
            account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        }

        accountRepository.save(account);
    }

    public Account findById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void update(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account không được null");
        }

        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username không được để trống");
        }

        if (account.getPassword() == null || account.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password không được trống");
        }

        if (!account.getPassword().startsWith("$2a$")) {
            account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        }

        accountRepository.update(account);
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        accountRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return accountRepository.existsByUsername(username);
    }
}
