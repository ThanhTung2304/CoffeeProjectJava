    package org.example.controller;

    import org.example.entity.Account;
    import org.example.service.AccountService;
    import org.example.service.impl.AccountServiceImpl;

<<<<<<< HEAD
    import java.util.List;
=======
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
>>>>>>> ecfe38d73e833e2fa2ae5e11b7f6a5a370c88483

    public class AccountController {

        private final AccountService accountService;

<<<<<<< HEAD

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
=======
    public AccountController() {
        this.accountService = new AccountServiceImpl();
    }

    /* ================= LOAD ALL ================= */
    public List<Account> loadAll() {
        return accountService.findAll();
    }

    /* ================= SEARCH / FILTER ================= */
    public List<Account> search(String keyword, String status) {

        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        String st = Objects.requireNonNullElse(status, "Tất cả");

        return accountService.findAll()
                .stream()
                .filter(a ->
                        key.isEmpty()
                                || a.getUsername().toLowerCase().contains(key)
                )
                .filter(a -> {
                    if ("Tất cả".equals(st)) return true;
                    return st.equals(a.isActive() ? "Hoạt động" : "Khóa");
                })
                .collect(Collectors.toList());
    }

    /* ================= FIND ================= */
    public Account findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username không hợp lệ");
        }

        Account acc = accountService.findByUsername(username.trim());
        if (acc == null) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        return acc;
    }

    /* ================= ADD ================= */
    public void add(String username, String password, String role, boolean active) {

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username không được để trống");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password không được để trống");
        }

        if (accountService.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Account account = new Account(
                username.trim(),
                password,
                role,
                active
        );

        accountService.create(account);
    }

    /* ================= UPDATE ================= */
    public void update(int id, String username, String password, String role, boolean active) {

        if (id <= 0) {
            throw new RuntimeException("ID không hợp lệ");
        }

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username không được để trống");
        }

        Account acc = accountService.findById((long) id);
        if (acc == null) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        acc.setUsername(username);
        acc.setPassword(password);
        acc.setRole(role);
        acc.setActive(active);

        accountService.update(acc);
    }

    /* ================= DELETE ================= */
    public void delete(int id) {
        if (id <= 0) {
            throw new RuntimeException("ID không hợp lệ");
        }
        accountService.deleteById(id);
    }


}
>>>>>>> ecfe38d73e833e2fa2ae5e11b7f6a5a370c88483
