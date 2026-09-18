# API Specification Document

Tài liệu đặc tả toàn bộ danh sách RESTful API Endpoints cho hệ thống ôn luyện thi (Reading, Listening, Writing, Vocabulary, Streak, Dashboard, v.v.).

---

## 1. Authentication (Xác thực)
> **Yêu cầu:** Public hoặc Refresh Token

| Method | Endpoint | Mô tả | Yêu cầu |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Đăng ký tài khoản mới | Public |
| `POST` | `/api/auth/login` | Đăng nhập hệ thống | Public |
| `POST` | `/api/auth/refresh` | Cấp lại access token từ refresh token | Refresh Token |
| `POST` | `/api/auth/logout` | Thu hồi refresh token / Đăng xuất | Authenticated |

---

## 2. User Profile (Thông tin người dùng)
> **Yêu cầu:** Cần đăng nhập (`User` / `Admin`)

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/users/me` | Xem thông tin cá nhân của người dùng hiện tại |
| `PUT` | `/api/users/me` | Cập nhật thông tin cá nhân |

---

## 3. Reading Module

### 3.1. User (Reading)
> **Yêu cầu:** Cần đăng nhập

| Method | Endpoint | Mô tả | Trạng thái |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reading/passages` | Lấy danh sách toàn bộ đề thi reading | Done |
| `GET` | `/api/reading/passages/{id}` | Lấy chi tiết một đề thi kèm câu hỏi | Done |
| `POST` | `/api/reading/passages/{id}/submit` | Nộp bài đọc để chấm điểm | Done |
| `GET` | `/api/reading/passages/submissions` | Lấy toàn bộ lịch sử nộp bài reading | Done |
| `GET` | `/api/reading/passages/submissions/{id}` | Xem chi tiết kết quả & lời giải một bài reading | Done |
| `GET` | `/api/reading/test-groups` | Lấy danh sách các bài Full Test Reading | - |
| `GET` | `/api/reading/test-groups/{id}/passages` | Lấy toàn bộ các passage trong một bài full test | - |

### 3.2. Admin (Reading)
> **Yêu cầu:** Role `Admin`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/admin/reading/passages` | Tạo passage mới |
| `PUT` | `/api/admin/reading/passages/{id}` | Cập nhật thông tin passage |
| `DELETE` | `/api/admin/reading/passages/{id}` | Xóa passage |
| `POST` | `/api/admin/reading/passages/{id}/questions` | Thêm câu hỏi vào passage |
| `PUT` | `/api/admin/reading/questions/{id}` | Cập nhật một câu hỏi |
| `DELETE` | `/api/admin/reading/questions/{id}` | Xóa một câu hỏi |
| `POST` | `/api/admin/reading/test-groups` | Tạo nhóm full test reading mới |

---

## 4. Listening Module

### 4.1. User (Listening)
> **Yêu cầu:** Cần đăng nhập

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/listening/tracks` | Lấy danh sách bài nghe |
| `GET` | `/api/listening/tracks/{id}` | Lấy chi tiết bài nghe kèm câu hỏi (ẩn đáp án - dùng DTO) |
| `POST` | `/api/listening/tracks/{id}/submit` | Nộp bài nghe để chấm điểm |
| `GET` | `/api/listening/submissions` | Lấy lịch sử các bài nghe đã làm |
| `GET` | `/api/listening/submissions/{id}` | Lấy kết quả & chi tiết lời giải 1 bài nghe đã làm |
| `GET` | `/api/listening/test-groups` | Lấy danh sách full test listening |
| `GET` | `/api/listening/test-groups/{id}/tracks` | Lấy danh sách bài nghe thuộc 1 bài full test |

### 4.2. Admin (Listening)
> **Yêu cầu:** Role `Admin`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/admin/listening/tracks` | Tạo bài nghe mới |
| `PUT` | `/api/admin/listening/tracks/{id}` | Sửa thông tin bài nghe |
| `DELETE` | `/api/admin/listening/tracks/{id}` | Xóa bài nghe |
| `POST` | `/api/admin/listening/tracks/{id}/questions` | Thêm câu hỏi vào bài nghe |
| `PUT` | `/api/admin/listening/questions/{id}` | Sửa câu hỏi |
| `DELETE` | `/api/admin/listening/questions/{id}` | Xóa câu hỏi |

---

## 5. Writing Module

### 5.1. User (Writing)
> **Yêu cầu:** Cần đăng nhập

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/writing/prompts` | Lấy danh sách đề writing |
| `GET` | `/api/writing/prompts/{id}` | Xem chi tiết 1 đề writing |
| `POST` | `/api/writing/prompts/{id}/submit` | Nộp bài để AI chấm điểm & nhận xét |
| `GET` | `/api/writing/submissions` | Xem lịch sử các bài writing đã nộp |
| `GET` | `/api/writing/submissions/{id}` | Xem lại feedback chi tiết của 1 bài nộp |
| `GET` | `/api/writing/submissions/{id}/export` | Xuất feedback bài viết ra định dạng PDF |

### 5.2. Admin (Writing)
> **Yêu cầu:** Role `Admin`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/admin/writing/prompts` | Tạo đề writing mới |
| `PUT` | `/api/admin/writing/prompts/{id}` | Chỉnh sửa đề writing |
| `DELETE` | `/api/admin/writing/prompts/{id}` | Xóa đề writing |

---

## 6. General Test Groups (Quản lý Full Test tổng hợp)
> **Yêu cầu:** Role `Admin`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/admin/test-groups` | Tạo 1 bộ Full Test mới |
| `PUT` | `/api/admin/test-groups/{id}` | Sửa thông tin bộ Full Test |
| `DELETE` | `/api/admin/test-groups/{id}` | Xóa bộ Full Test |

---

## 7. Vocabulary (Sổ từ vựng & Flashcards)
> **Yêu cầu:** Cần đăng nhập

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/vocabulary` | Lưu từ mới vào sổ từ vựng |
| `GET` | `/api/vocabulary` | Lấy danh sách từ vựng cá nhân |
| `DELETE` | `/api/vocabulary/{id}` | Xóa một từ vựng khỏi danh mục |
| `GET` | `/api/vocabulary/flashcards` | Lấy danh sách từ vựng để học theo thẻ Flashcard |
| `POST` | `/api/vocabulary/{id}/review` | Ghi nhận kết quả ôn tập (Spaced Repetition / Leitner) |

---

## 8. Streak & Dashboard (Tiến độ học tập)
> **Yêu cầu:** Cần đăng nhập

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/streak` | Xem thông tin chuỗi ngày học liên tục (streak) hiện tại |
| `GET` | `/api/dashboard/stats` | Lấy số liệu tổng quan (thời gian học, điểm số trung bình, tiến độ) |
| `GET` | `/api/dashboard/recent-activities` | Lấy danh sách hoạt động học tập gần đây |