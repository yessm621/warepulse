# 재고/물류 WMS 이벤트 시스템 (WarePulse)

## 📌 프로젝트 소개

🏷 프로젝트 명: WarePulse

🗓️ 프로젝트 기간: 2025.12~2026.02

### ✅ 서비스 소개

> `입고 → 적재 → 출고 → 재고 조정`까지 물류센터 안의 모든 흐름을 관리하는 시스템
>
- **재고 정확도·동시성·이벤트 기반 입출고 관리** 중심.
- 물류센터에서 실제로 사용하는 시스템을 구현하고자 함.

### ✅ 주요 기술

- 이벤트 기반 재고 증가/감소 처리
- 동시성 제어(Optimistic Lock / Redis Lock)
- 재고 오차 방지 로직(Adjustment)

---

## 📌 기술 스택

### Backend & Cloud

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.9
- **Infrastructure:** Spring Cloud (Gateway, Eureka, Config)
- **Security:** Spring Security (JWT)

### Communication

- **동기화 통신:** Spring Cloud OpenFeign
- **비동기 통신:** Apache Kafka

### Data

- **ORM:** Spring Data JPA
- **RDB:** MySQL 8

---

## 📌 프로젝트 구조

```
📦warepulse-multi
 ┣ 📂adjustment-service
 ┣ 📂apigateway-service
 ┣ 📂config-service
 ┣ 📂inventory-service
 ┣ 📂receive-service
 ┣ 📂service-discovery
 ┣ 📂shipment-service
 ┗ 📂warepulse
```

![이미지 설명](https://github.com/user-attachments/assets/65ff1e68-d5fd-428e-ab1a-962d4fb6944d)

---

## 📌 프로세스

### 입고 프로세스

![이미지 설명](https://github.com/user-attachments/assets/82127940-e6b7-4608-936e-77d3639ff20d)

### 출고 프로세스

![이미지 설명](https://github.com/user-attachments/assets/f7a8b68c-4972-404f-a6c1-fe4c8d5e10ed)

### 재고 조정(실사) 프로세스

![이미지 설명](https://github.com/user-attachments/assets/de78a00a-df5c-4bfc-8565-50e71c6a2c82)

---

## 📌 ERD

![이미지 설명](https://github.com/user-attachments/assets/7afa8f0d-5e19-4b3c-8023-92393d760a0b)

---

## 📌 API 명세서 (Swagger API)

![1](https://github.com/user-attachments/assets/b5848eec-6e1c-4091-b3a7-2159433d8f3f)

![2](https://github.com/user-attachments/assets/b7483366-2047-4b20-bbcc-a4f7915966cb)

![3](https://github.com/user-attachments/assets/7b0ffd1d-39c3-453b-a444-2d5e26d6f44d)

![4](https://github.com/user-attachments/assets/0c51bd31-a225-41f1-85ee-769bdac1d0e3)
