-- ==========================================
-- 회원 기본 데이터 (members)
-- ==========================================
INSERT INTO members (
    member_id, name, password, member_role, gender, hiredate, email, address,
    position_id, create_id, update_id, create_date, update_date
) VALUES
('admin', '관리자', '{noop}1234', 'ROLE_ADMIN', '남성', '20250101', 'admin@example.com', '서울특별시 중구',
 NULL, 'system', 'system', NOW(), NOW()),
('user1', '홍길동', '{noop}1234', 'ROLE_USER', '남성', '20250102', 'hong@example.com', '서울특별시 강남구',
 NULL, 'system', 'system', NOW(), NOW()),
('user2', '김철수', '{noop}1234', 'ROLE_USER', '남성', '20250103', 'kim@example.com', '경기도 성남시',
 NULL, 'system', 'system', NOW(), NOW()),
('user3', '이영희', '{noop}1234', 'ROLE_USER', '여성', '20250104', 'lee@example.com', '부산광역시 해운대구',
 NULL, 'system', 'system', NOW(), NOW());

-- ==========================================
-- 카테고리 기본 데이터 (categories)
-- ==========================================
INSERT INTO categories (
    name, color, create_id, update_id, create_date, update_date
) VALUES
('회의', '#0d6efd', 'system', 'system', NOW(), NOW()),
('출장', '#198754', 'system', 'system', NOW(), NOW()),
('교육', '#ffc107', 'system', 'system', NOW(), NOW()),
('휴가', '#dc3545', 'system', 'system', NOW(), NOW());