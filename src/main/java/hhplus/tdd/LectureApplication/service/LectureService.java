package hhplus.tdd.LectureApplication.service;

import hhplus.tdd.LectureApplication.dto.ApplicationDTO;
import hhplus.tdd.LectureApplication.dto.LectureDTO;
import hhplus.tdd.LectureApplication.entity.Lecture;
import hhplus.tdd.LectureApplication.entity.Application;
import hhplus.tdd.LectureApplication.repository.ApplicationRepositoryJPA;
import hhplus.tdd.LectureApplication.repository.LectureRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
	private final LectureRepositoryJPA lectureRepositoryJPA;
	private final ApplicationRepositoryJPA applicationRepositoryJPA;

	@Transactional
	public Application applyLecture(Long lectureId, Long studentId) {
		//강의 조회
		Lecture lecture = lectureRepositoryJPA
				                  .findById(lectureId)
				                  .orElseThrow(() -> new RuntimeException("유효하지 않은 강의 ID 입니다."));

		//현재 강의 수강인원 증가
		Long current = lecture.getCurrent();

		if (current >= lecture.getCapacity()) {
			throw new IllegalStateException("정원을 초과할 수 없습니다.");
		}

		lecture.setCurrent(current + 1);
		lectureRepositoryJPA.save(lecture);

		//수강신청 내역 생성
		Application application = new Application();
		application.setLecture(lecture);
		application.setStudentId(studentId);

		return applicationRepositoryJPA.save(application);
	}

	@Transactional(readOnly = true)
	public List<LectureDTO> getAvailableLectures(LocalDate date) {
		if (date == null) {
			date = LocalDate.now();
		}

		return lectureRepositoryJPA.findAllByDateGreaterThanEqual(date)
				       .stream()
				       .map(lecture -> {
					       LectureDTO dto = new LectureDTO();
					       dto.setLectureId(lecture.getId());
					       dto.setTitle(lecture.getTitle());
					       dto.setInstructor(lecture.getInstructor());
					       dto.setDate(lecture.getDate());
					       dto.setCapacity(lecture.getCapacity());
					       dto.setCurrent(lecture.getCurrent());
					       return dto;
				       })
				       .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ApplicationDTO> getCompletedLectures(Long studentId) {
		return applicationRepositoryJPA.findAllByStudentId(studentId)
				       .stream()
				       .map(application -> {
					       ApplicationDTO dto = new ApplicationDTO();
					       dto.setApplicationId(application.getId());
					       dto.setStudentId(application.getStudentId());

					       // Lecture 정보를 LectureScheduleResponseDTO로 변환
					       LectureDTO lectureDTO = new LectureDTO();
					       lectureDTO.setLectureId(application.getLecture().getId());
					       lectureDTO.setTitle(application.getLecture().getTitle());
					       lectureDTO.setInstructor(application.getLecture().getInstructor());
					       lectureDTO.setDate(application.getLecture().getDate());
					       lectureDTO.setCapacity(application.getLecture().getCapacity());
					       lectureDTO.setCurrent(application.getLecture().getCurrent());

					       dto.setLecture(lectureDTO);

					       return dto;
				       })
				       .collect(Collectors.toList());
	}

}