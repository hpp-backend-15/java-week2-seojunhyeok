package hhplus.tdd.LectureApplication.controller;

import hhplus.tdd.LectureApplication.dto.ApplicationDTO;
import hhplus.tdd.LectureApplication.dto.LectureDTO;
import hhplus.tdd.LectureApplication.entity.Application;
import hhplus.tdd.LectureApplication.service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/lectures")
public class LectureController {

	private final LectureService lectureService;

	public LectureController(LectureService lectureService) {
		this.lectureService = lectureService;
	}

	// 특강 신청 API
	@PostMapping("/apply")
	public ResponseEntity<Application> applyForLecture(
			@RequestParam("lectureId") Long lectureId,
			@RequestParam("studentId") Long studentId) {
		Application application = lectureService.applyLecture(lectureId, studentId);
		return ResponseEntity.ok(application);
	}

	@GetMapping("/available")
	public List<LectureDTO> getAvailableLectures(
			@RequestParam(value = "date", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return lectureService.getAvailableLectures(date);
	}

	@GetMapping("/completed")
	public List<ApplicationDTO> getCompletedLectures(
			@RequestParam Long studentId
	) {
		return lectureService.getCompletedLectures(studentId);
	}
}
