package org.example.service.impl;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;

    public AuthServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    @Override
    public Account login(LoginRequest request) {
        System.out.println("Login running with username: "  + request.getUsername());

        if(request.getUsername()==null || request.getPassword()==null){
            System.out.println("Username or Password is null");
            return null;
        }

        Account account = accountRepository.findByUsername(request.getUsername());
        if(account==null){
            System.out.println("Login failed: account not found");
            return null;
        }

        if(!account.isActive()){
            System.out.println("Login failed: account is inactive");
            return null;
        }

        if(!account.getPassword().equals(request.getPassword())){
            System.out.println("Login failed: incorrect password");
            return null;
        }

        System.out.println("login successful: "+account.getUsername());
        return account;
    }

    /**
     * Đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    @Override
    public boolean register(RegisterRequest request) {
        System.out.println("Register new account with username: "+ request.getUsername());
        validateInfo(request); //Kiểm tra dữ liệu đăng ký trước khi lưu

        Account account = new Account(
                request.getUsername(),
                request.getPassword(),
                "USER",
                true
        );

        accountRepository.save(account);
        System.out.println("Registration succesful for username: "+ request.getUsername());

        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    // ===================== PRIVATE METHODS =====================
    public void validateInfo(RegisterRequest request){
        if(request.getUsername()==null || request.getUsername().isBlank()){
            throw new RuntimeException("Username Không được để trống");
        }

        if(request.getPassword()==null || request.getPassword().isBlank()){
            throw new RuntimeException("Password Không được để trống");
        }

        if(request.getConfirmPassword()==null || request.getConfirmPassword().isBlank()){
            throw new RuntimeException("Confirm Password không được để trống");
        }

        //check duplicate username
        if(accountRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username đã tồn tại");
        }

        if(!request.isPasswordConfirmed()){
            throw new RuntimeException("Password và ConfirmPassword không khớp!");
        }
    }

}
