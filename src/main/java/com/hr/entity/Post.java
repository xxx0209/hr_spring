package com.hr.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;                       // posts.post_id

    @Column(nullable = false)
    private String title;                  // posts.title

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;                // posts.content

    // DB는 ENUM('공지사항','자유게시판') -> 자바에선 String으로 매핑
    @Column(nullable = false)
    private String category;               // posts.category

    @Column(name = "create_id", nullable = false)
    private String createId;               // posts.create_id (FK to members.member_id)

    @Column(nullable = false)
    private Integer views = 0;             // posts.views (자바에서 기본 0 보장)

    // DB에서 자동 채움 -> JPA에선 읽기 전용
    @Column(name = "create_date", insertable = false, updatable = false)
    private LocalDateTime createDate;      // posts.create_date

    @Column(name = "update_date", insertable = false, updatable = false)
    private LocalDateTime updateDate;      // posts.update_date
}
