package org.example.service.impl;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.repository.AccountRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;

    public AuthServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
        this.employeeRepository = new EmployeeRepositoryImpl();
    }

    // ================= LOGIN =================
    @Override
    public Account login(LoginRequest request) {

        if (request.getUsername() == null || request.getPassword() == null)
            return null;

        Account account = accountRepository.findByUsername(request.getUsername());

        if (account == null || !account.isActive())
            return null;

        if (!account.getPassword().equals(request.getPassword()))
            return null;

        return account;
    }

    // ================= REGISTER =================
    @Override
    public boolean register(RegisterRequest request) {

        validateInfo(request);

        // Lấy role từ form
        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        }

        Account account = new Account(
                request.getUsername(),
                request.getPassword(),
                role,
                true
        );

        // Lưu account
        accountRepository.save(account);

        // 👉 NẾU LÀ STAFF → TẠO EMPLOYEE
        if ("STAFF".equalsIgnoreCase(role)) {
            Employee employee = new Employee();
            employee.setName(request.getUsername());
            employee.setPosition("Staff");
            employee.setAccountId(account.getId());

            employeeRepository.save(employee);
        }

        return true;
    }

    // ================= CHECK USERNAME =================
    @Override
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    // ================= VALIDATE =================
    private void validateInfo(RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank())
            throw new RuntimeException("Username không được để trống");

        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new RuntimeException("Password không được để trống");

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank())
            throw new RuntimeException("Confirm Password không được để trống");

        if (!request.isPasswordConfirmed())
            throw new RuntimeException("Password và ConfirmPassword không khớp");

        if (accountRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username đã tồn tại");
    }
}
