package org.example.entity;

import java.time.LocalDateTime;

    public class Customer {

        private int id;
        private String name;
        private String phone;
        private String email;
        private int totalPoint;
        private LocalDateTime createdTime;
        private LocalDateTime updateTime;

        public Customer() {}

        public Customer(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.totalPoint = 0;
        }

        // ===== GET / SET =====
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getTotalPoint() { return totalPoint; }
        public void setTotalPoint(int totalPoint) { this.totalPoint = totalPoint; }

        public LocalDateTime getCreatedTime() { return createdTime; }
        public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

        public LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    }


