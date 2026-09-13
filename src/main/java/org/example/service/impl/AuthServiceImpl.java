package org.example.service.impl;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.event.DataChangeEventBus;
import org.example.repository.AccountRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {

    private final AccountRepository  accountRepository;
    private final EmployeeRepository employeeRepository;

    public AuthServiceImpl() {
        this.accountRepository  = new AccountRepositoryImpl();
        this.employeeRepository = new EmployeeRepositoryImpl();
    }

    @Override
    public Account login(LoginRequest request) {

        if (request.getUsername() == null || request.getPassword() == null)
            return null;

        Account account = accountRepository.findByUsername(request.getUsername());

        if (account == null || !account.isActive())
            return null;

        String storedPassword = account.getPassword();
        String inputPassword = request.getPassword();

        boolean passwordMatch;
        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            passwordMatch = BCrypt.checkpw(inputPassword, storedPassword);
        } else {
            passwordMatch = inputPassword.equals(storedPassword);
            if (passwordMatch) {
                account.setPassword(BCrypt.hashpw(inputPassword, BCrypt.gensalt()));
                accountRepository.update(account);
            }
        }

        if (!passwordMatch)
            return null;

        return account;
    }

    @Override
    public boolean register(RegisterRequest request) {

        validateInfo(request);

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        Account account = new Account(
                request.getUsername(),
                hashedPassword,
                role,
                true
        );

        accountRepository.save(account);

        Account saved = accountRepository.findByUsername(request.getUsername());

        if ("STAFF".equalsIgnoreCase(role) && saved != null) {
            Employee employee = new Employee();
            employee.setName(request.getUsername());
            employee.setPhone("");
            employee.setPosition("Staff");
            employee.setAccountId(saved.getId());
            employee.setUsername(saved.getUsername());

            employeeRepository.save(employee);
        }

        DataChangeEventBus.notifyChange();

        return true;
    }

    @Override
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    private void validateInfo(RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank())
            throw new RuntimeException("Username không được để trống");

        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new RuntimeException("Password không được để trống");

        if (request.getPassword().length() < 6)
            throw new RuntimeException("Password phải có ít nhất 6 ký tự");

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank())
            throw new RuntimeException("Confirm Password không được để trống");

        if (!request.isPasswordConfirmed())
            throw new RuntimeException("Password và ConfirmPassword không khớp");

        if (accountRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username đã tồn tại");
    }
}
