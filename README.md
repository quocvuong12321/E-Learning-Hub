# E-Learning Hub: All-in-One Platform
Giải pháp toàn diện cho xây dựng thương hiệu cá nhân và kinh doanh tri thức trực tuyến.


### Tổng quan dự án
E-learning Hub là hệ thống tích hợp hai chức năng cốt lõi: Blog chuyên sâu để chia sẻ kiến thức và Hệ thống quản lý đào tạo (LMS) để thương mại hóa các khóa học.

### Tech Stack & Giải pháp kỹ thuật
Hệ thống sử dụng bộ công nghệ hiện đại tiêu chuẩn doanh nghiệp nhằm đảm bảo tính ổn định và bảo mật:
+ **Framework chính:** Java Spring Boot đảm bảo hiệu năng cao và khả năng mở rộng hệ thống
+ **Xác thực & Bảo mật:** Spring Security + JWT (Stateless Authentication).
+ **Database:** MySQL
+ **Video Streaming:** Tích hợp API từ bên thứ 3 (Vimeo/Youtube)

### Điểm nhấn Logic nghiệp vụ (Business Logic Deep-dive)
+ Cơ chế Sequential Learning: Backend xử lý việc kiểm tra điều kiện hoàn thành bài học trước đó dựa trên dữ liệu lưu trữ tiến trình.
+ Logic Assessment: Hệ thống tự động chấm điểm bài tập trắc nghiệm và áp dụng logic điều kiện (Target Score > 80%) để cấp quyền mở khóa nội dung tiếp theo
