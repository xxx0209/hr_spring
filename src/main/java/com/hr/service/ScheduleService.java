package com.hr.service;

import com.hr.dto.member.ScheduleDto;
import com.hr.entity.Category;
import com.hr.entity.Member;
import com.hr.entity.Schedule;
import com.hr.repository.CategoryRepository;
import com.hr.repository.MemberRepository;
import com.hr.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    //private final AttendanceRepository attendanceRepository;

    // 회원별 일정 조회 (관리자 또는 본인)
    public List<ScheduleDto> getSchedulesByMember(String memberId) {

        List<Schedule> schedules = scheduleRepository.findByMemberId(memberId);

        return schedules.stream().map(schedule -> {
            ScheduleDto dto = ScheduleDto.fromEntity(schedule, ScheduleDto.class);
            dto.setMemberId(schedule.getMember().getId());
            dto.setCategoryId(schedule.getCategory().getCategoryId());
            dto.setColor(schedule.getCategory().getColor());
            // 근태 정보 추가
//            LocalDate workDate = schedule.getStart().toLocalDate();
//            attendanceRepository.findByMemberIdAndWorkDate(memberId, workDate)
//                    .ifPresent(att -> {
//                        dto.setCheckInTime(att.getCheckInTime());
//                        dto.setCheckOutTime(att.getCheckOutTime());
//                    });

            return dto;
        }).collect(Collectors.toList());
    }

    // 일정 등록
    public ScheduleDto save(ScheduleDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        Category category = dto.getCategoryId() != null
                ? categoryRepository.findById(dto.getCategoryId()).orElse(null)
                : null;

        Schedule schedule = dto.toEntity();
        schedule.setMember(member);
        schedule.setCategory(category);
        Schedule saved = scheduleRepository.save(schedule);

        ScheduleDto scheduleDto = ScheduleDto.fromEntity(saved, ScheduleDto.class);
        scheduleDto.setMemberId(member.getId());
        scheduleDto.setCategoryId(category != null ? category.getCategoryId() : null);

        return scheduleDto;
    }

    // 일정 삭제
    public void delete(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
