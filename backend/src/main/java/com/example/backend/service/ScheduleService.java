
package com.example.backend.service;
import com.example.backend.dto.*;
import com.example.backend.repository.*;
import com.example.backend.entity.*;
import com.example.backend.enums.TimeSlotStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    public List<DayResponse> getAvailableDaysForDoctor(Long doctorId) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        LocalDate today = LocalDate.now();

        LocalDate nextFriday = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));
        LocalDate endOfNextWeek = nextFriday.plusWeeks(1);

        return schedules.stream()
                .flatMap(schedule -> {
                    LocalDate firstDate = today
                            .with(java.time.temporal.TemporalAdjusters.nextOrSame(schedule.getDayOfWeek()));

                    LocalDate secondDate = firstDate.plusWeeks(1);

                    return java.util.stream.Stream.of(firstDate, secondDate)
                            .filter(date -> !date.isAfter(endOfNextWeek))
                            .map(date -> mapToResponse(schedule, date));
                })
                .sorted(java.util.Comparator.comparing(DayResponse::getDate))
                .toList();
    }

    private DayResponse mapToResponse(Schedule schedule, LocalDate actualDate) {
        DayResponse response = new DayResponse();
        response.setDayName(schedule.getDayOfWeek().name());
        response.setDate(actualDate);
        response.setStartTime(schedule.getStartTime().toString());
        response.setEndTime(schedule.getEndTime().toString());
        return response;
    }
    
    public List<SlotResponse> getAvailableSlotsForDoctorAndDate(Long doctorId, LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findBySchedule_Doctor_IdAndDateAndStatusOrderByStartTimeAsc(doctorId, date, TimeSlotStatus.AVAILABLE);
        return timeSlots.stream()
                .map(slot -> {
                    SlotResponse response = new SlotResponse();
                    response.setSlotId(slot.getId());
                    response.setStartTime(slot.getStartTime().toString());
                    response.setEndTime(slot.getEndTime().toString());
                    return response;
                })
                .toList();
    }
}