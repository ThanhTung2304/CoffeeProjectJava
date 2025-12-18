package org.example.controller;

import org.example.entity.Account;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

import java.util.List;

public class AccountController {

    private final AccountService accountService;


    public AccountController() {
        this.accountService = new AccountServiceImpl();
    }

    /**
     * Lấy danh sách tất cả account
     */
    public List<Account> getAllAccount(){
        return accountService.findAll();
    }

    /**
     * Tạo mới account
     */
    public void createAccount(String username, String password, String role, boolean is_active){
        Account account = new Account(username, password, role, is_active);
        accountService.create(account);
    }


    /**
     * Cập nhật account
     */
    public void updateAccount(Account account){
        accountService.update(account);
    }

    /**
     * Xóa account theo ID
     */
    public void deleteAccount(Account account){
        accountService.deleteById(account.getId());
    }

    /**
     * Tìm account theo username
     */

}
