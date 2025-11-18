package com.hr.service;

import com.hr.dto.*;
import com.hr.dto.member.*;
import com.hr.entity.Member;
import com.hr.entity.PositionHistory;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${uploadPath}")
    private String uploadDir;

    @Value("${productImageLocation}")
    private String productImageLocation ; // 기본 값 :


    public void save(MemberDto memberDto) {

        //패스워드 암호화
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Member member = memberDto.toEntity();

        Member test = new Member();


        memberRepository.save(member);
    }

    public Boolean existsById(String memberId) {
        return memberRepository.existsById(memberId);
    }

    public Optional<MemberDto> findById(String memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        return Optional.ofNullable(BaseDto.fromEntity(member, MemberDto.class));

    }

    public List<SimpleMemberDto> findAll() {

        return memberRepository.findAll()
                .stream()
                //.peek(m -> Hibernate.initialize(m.getPosition()))
                .map(p -> SimpleMemberDto.fromEntity(p, SimpleMemberDto.class))
                .collect(Collectors.toList());
    }

    public MemberUpdateDto updateMember(MemberUpdateDto dto) throws IOException {
        Member member = memberRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setGender(dto.getGender());
        member.setHiredate(dto.getHiredate());
        member.setAddress(dto.getAddress());

        // 패스워드 변경
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 프로필 이미지 처리
        MultipartFile file = dto.getProfileImage();
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = member.getId() + "_" + System.currentTimeMillis() + extension;

            File dest = Paths.get(productImageLocation, fileName).toFile();
            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            // 기존 파일 삭제
            if (member.getProfileImage() != null) {
                File oldFile = Paths.get(productImageLocation, member.getProfileImage()).toFile();
                if (oldFile.exists()) oldFile.delete();
            }

            member.setProfileImage(fileName);
        }
        memberRepository.save(member);
        dto.setPassword(null);
        dto.setProfileImage(null);

        return dto;
    }

    // 회원 정보 조회 (DTO 반환)
    public MemberUpdateDto getMember(String memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        PositionHistory firstHistory = null;
        if (!member.getHistories().isEmpty()) {
            firstHistory = member.getHistories().get(0); // 이미 정렬되어 있으므로 첫 요소가 첫 기록
        }

        MemberUpdateDto memberUpdateDto = MemberUpdateDto.fromEntity(member, MemberUpdateDto.class);

        memberUpdateDto.setPassword(null);
        if (member.getProfileImage() != null) {
            memberUpdateDto.setProfileImageUrl("/images/" + member.getProfileImage());
        }

        if (firstHistory != null) {
            memberUpdateDto.setPositionName(firstHistory.getNewPosition().getPositionName());
        }

        return memberUpdateDto;
    }

    public Page<MemberResponseDto> searchMembers(MemberSearchRequestDto dto) {
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        Page<Member> pageResult;

        switch (dto.getSearchType()) {
            case "username":
                pageResult = memberRepository.findByIdContaining(dto.getKeyword(), pageable);
                break;
            case "name":
                pageResult = memberRepository.findByNameContaining(dto.getKeyword(), pageable);
                break;
            case "position":
                pageResult = memberRepository.findByPositionNameContaining(dto.getKeyword(), pageable);
                break;
            case "hireDate":
                pageResult = memberRepository.findByHiredate(dto.getHireDate(), pageable);
                break;
            case "all":
            default:
                pageResult = memberRepository.findAll(pageable);
        }

        return pageResult.map(m -> new MemberResponseDto(
                m.getId(),
                m.getName(),
                m.getPosition() != null ? m.getPosition().getPositionName() : "-",
                m.getGender(),
                m.getEmail(),
                m.getMemberRole().name(),
                m.getMemberRole().getLabel(),
                m.getHiredate(),
                m.getProfileImage()
        ));
    }

    @Transactional
    public void updateMemberRole(MemberRoleUpdateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.setMemberRole(dto.getMemberRole());
        memberRepository.save(member);
    }


}
