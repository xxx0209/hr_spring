package com.hr.service;

import com.hr.entity.Comment;
import com.hr.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }
}

//    public List<Comment> findByPostId(Long postId) {
//        return commentRepository.findByPost_IdOrderByCreateDateDesc(postId);
//    }
