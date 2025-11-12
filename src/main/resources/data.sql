-- ==========================================
-- 회원 기본 데이터 (members)
-- ==========================================
INSERT INTO members (
    member_id, name, password, member_role, gender, hiredate, email, address,
    position_id, create_id, update_id, create_date, update_date
) VALUES
('admin', '관리자', '{noop}1234', 'ROLE_ADMIN', 'MALE', '20250101', 'admin@example.com', '서울특별시 중구',
 NULL, 'system', 'system', NOW(), NOW()),
('user1', '홍길동', '{noop}1234', 'ROLE_USER', 'MALE', '20250102', 'hong@example.com', '서울특별시 강남구',
 NULL, 'system', 'system', NOW(), NOW()),
('user2', '김철수', '{noop}1234', 'ROLE_USER', 'MALE', '20250103', 'kim@example.com', '경기도 성남시',
 NULL, 'system', 'system', NOW(), NOW()),
('user3', '이영희', '{noop}1234', 'ROLE_USER', 'FEMALE', '20250104', 'lee@example.com', '부산광역시 해운대구',
 NULL, 'system', 'system', NOW(), NOW()),
('admin2', '박관리', '{noop}1234', 'ROLE_ADMIN', 'FEMALE', '20250105', 'admin2@example.com', '서울특별시 마포구',
 NULL, 'system', 'system', NOW(), NOW());

-- ==========================================
-- 카테고리 기본 데이터 (categories)
-- ==========================================
INSERT INTO categories (
    active, name, color, create_id, update_id, create_date, update_date
) VALUES
(1, '업무', '#0d6efd', 'system', 'system', NOW(), NOW()),
(1, '회의', '#198754', 'system', 'system', NOW(), NOW()),
(1, '교육', '#ffc107', 'system', 'system', NOW(), NOW()),
(1, '외근', '#dc3545', 'system', 'system', NOW(), NOW()),
(1, '출장', '#0dfd61', 'system', 'system', NOW(), NOW());

-- ==========================================
-- 직급 기본 데이터 (positions)
-- ==========================================
INSERT INTO `positions` (
  `active`,
  `create_date`,
  `update_date`,
  `position_code`,
  `position_name`,
  `create_id`,
  `description`,
  `update_id`
) VALUES
(1, NOW(), NOW(), 'CEO', '대표이사', 'system', '회사 최고경영자, 전사 의사결정 책임자', 'system'),
(1, NOW(), NOW(), 'VP', '부사장', 'system', 'CEO 보좌, 주요 조직(본부·사업부) 총괄', 'system'),
(1, NOW(), NOW(), 'SED', '전무', 'system', '경영 전략 및 중간 관리자 총괄', 'system'),
(1, NOW(), NOW(), 'ED', '상무', 'system', '부서 운영 관리, 주요 프로젝트 총괄', 'system'),
(1, NOW(), NOW(), 'DIR.', '이사', 'system', '팀/부서 책임자', 'system'),
(1, NOW(), NOW(), 'GM', '부장', 'system', '부서 운영 및 인력/성과 관리', 'system'),
(1, NOW(), NOW(), 'DGM', '차장', 'system', '부장 보좌, 실무 리더 역할', 'system'),
(1, NOW(), NOW(), 'MGR', '과장', 'system', '실무 리더, 팀 운영 및 실무 조정', 'system'),
(1, NOW(), NOW(), 'AM', '대리', 'system', '실무 중심, 독립 업무 수행', 'system'),
(1, NOW(), NOW(), 'SS', '주임', 'system', '숙련된 실무 담당자', 'system'),
(1, NOW(), NOW(), 'STF', '사원', 'system', '기본 실무 수행자, 신입 직원', 'system'),
(1, NOW(), NOW(), 'IN', '인턴', 'system', '수습 / 인턴', 'system');
