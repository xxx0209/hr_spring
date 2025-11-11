package com.hr.service;

import com.hr.dto.member.RequestDto;
import com.hr.dto.member.ScheduleDto;
import com.hr.dto.member.ScheduleGroupDto;
import com.hr.entity.Category;
import com.hr.entity.Member;
import com.hr.entity.Request;
import com.hr.entity.Schedule;
import com.hr.repository.CategoryRepository;
import com.hr.repository.MemberRepository;
import com.hr.repository.RequestRepository;
import com.hr.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    //private final AttendanceRepository attendanceRepository;

    // 회원별 일정 조회 (관리자 또는 본인)
    public ScheduleGroupDto getSchedulesByMemberAndMonth(String memberId, String month) {

        LocalDateTime startOfMonth = null;
        LocalDateTime endOfMonth = null;

        if (month != null && !month.isEmpty()) {
            YearMonth ym = YearMonth.parse(month);
            startOfMonth = ym.atDay(1).atStartOfDay();
            endOfMonth = ym.atEndOfMonth().atTime(LocalTime.MAX);
        }

        List<Schedule> schedules = (startOfMonth != null)
                ? scheduleRepository.findByMemberIdAndStartBetween(memberId, startOfMonth, endOfMonth)
                : scheduleRepository.findByMemberId(memberId);

        ScheduleGroupDto scheduleGroupDto = new ScheduleGroupDto();

        schedules.forEach(schedule -> {
            ScheduleDto dto = ScheduleDto.fromEntity(schedule, ScheduleDto.class);
            dto.setTitle("[".concat(schedule.getCategory().getName())
                    .concat("] ")
                    .concat(dto.getTitle()));
            dto.setMemberId(schedule.getMember().getId());
            dto.setCategoryId(schedule.getCategory().getCategoryId());
            dto.setColor(schedule.getCategory().getColor());

            // 스케줄 데이터
            scheduleGroupDto.getScheduleDtoList().add(dto);
        });

        //휴가
        List<Request> requests = (startOfMonth != null)
                ? requestRepository.findByMemberIdAndRequestTypeAndStatusAndStartDateBetween(memberId, "휴가", "승인", startOfMonth, endOfMonth)
                : requestRepository.findByMemberId(memberId);

        requests.forEach(request -> {
            RequestDto dto = RequestDto.fromEntity(request, RequestDto.class);
            dto.setTitle("[휴가]");
            dto.setStart(request.getStartDate());
            dto.setEnd(request.getEndDate());
            // 스케줄 데이터
            scheduleGroupDto.getRequestDtoList().add(dto);
        });

        return scheduleGroupDto;
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
        assert category != null : "카테고리 아이디가 null입니다.";
        scheduleDto.setTitle("[".concat(category.getName())
                                .concat("] ")
                                .concat(scheduleDto.getTitle()));
        scheduleDto.setMemberId(member.getId());
        scheduleDto.setCategoryId(category.getCategoryId());

        return scheduleDto;
    }

    // 일정 삭제
    public void delete(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
