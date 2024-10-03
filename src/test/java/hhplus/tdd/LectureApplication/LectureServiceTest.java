package hhplus.tdd.LectureApplication;

import hhplus.tdd.LectureApplication.service.LectureService;
import hhplus.tdd.LectureApplication.dto.ApplicationDTO;
import hhplus.tdd.LectureApplication.dto.LectureDTO;
import hhplus.tdd.LectureApplication.entity.Application;
import hhplus.tdd.LectureApplication.entity.Lecture;
import hhplus.tdd.LectureApplication.repository.ApplicationRepositoryJPA;
import hhplus.tdd.LectureApplication.repository.LectureRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LectureServiceTest {

	@Mock
	ApplicationRepositoryJPA applicationRepositoryJPA;

	@Mock
	LectureRepositoryJPA lectureRepositoryJPA;

	@InjectMocks
	LectureService lectureService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Nested
	@DisplayName("수강신청 기능 테스트")
	class ApplyLectureTests {

		@Test
		@DisplayName("수강신청 성공")
		public void applyLectureSuccess() {
			// Given
			Long lectureId = 1L;
			Long studentId = 100L;
			Lecture lecture = new Lecture();
			lecture.setId(lectureId);
			lecture.setCurrent(5L);
			lecture.setCapacity(10L);

			when(lectureRepositoryJPA.findByIdWithLock(lectureId)).thenReturn(Optional.of(lecture));
			when(applicationRepositoryJPA.save(any(Application.class))).thenReturn(new Application());

			// When
			Application application = lectureService.applyLecture(lectureId, studentId);

			// Then
			assertNotNull(application);
			verify(lectureRepositoryJPA).save(lecture);
			verify(applicationRepositoryJPA).save(any(Application.class));
		}

		@Test
		@DisplayName("강의 ID가 유효하지 않은 경우 수강신청에 실패한다")
		public void applyLectureFailsInvalidLectureId() {
			// Given
			Long invalidLectureId = 999L;
			Long studentId = 100L;

			when(lectureRepositoryJPA.findByIdWithLock(invalidLectureId)).thenReturn(Optional.empty());

			// When & Then
			RuntimeException exception = assertThrows(RuntimeException.class, () -> {
				lectureService.applyLecture(invalidLectureId, studentId);
			});
			assertEquals("유효하지 않은 강의 ID 입니다.", exception.getMessage());
		}

		@Test
		@DisplayName("정원이 초과된 경우 수강신청에 실패한다")
		public void applyLectureFailsCapacityExceeded() {
			// Given
			Long lectureId = 1L;
			Long studentId = 100L;
			Lecture lecture = new Lecture();
			lecture.setId(lectureId);
			lecture.setCurrent(10L); // 현재 인원이 최대 인원과 같음
			lecture.setCapacity(10L);

			when(lectureRepositoryJPA.findByIdWithLock(lectureId)).thenReturn(Optional.of(lecture));

			// When & Then
			IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
				lectureService.applyLecture(lectureId, studentId);
			});
			assertEquals("정원을 초과할 수 없습니다.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("강의 목록 조회 기능 테스트")
	class LectureListTests {

		@Test
		@DisplayName("신청 가능한 강의 목록 조회 성공")
		public void getAvailableLecturesSuccess() {
			// Given
			LocalDate date = LocalDate.now();
			Lecture lecture1 = new Lecture();
			lecture1.setId(1L);
			lecture1.setTitle("Java Lecture");
			lecture1.setInstructor("John Doe");
			lecture1.setDate(date.plusDays(1));
			lecture1.setCapacity(10L);
			lecture1.setCurrent(5L);

			Lecture lecture2 = new Lecture();
			lecture2.setId(2L);
			lecture2.setTitle("Spring Lecture");
			lecture2.setInstructor("Jane Doe");
			lecture2.setDate(date.plusDays(2));
			lecture2.setCapacity(10L);
			lecture2.setCurrent(3L);

			List<Lecture> mockLectures = Arrays.asList(lecture1, lecture2);

			when(lectureRepositoryJPA.findAllByDateGreaterThanEqual(date)).thenReturn(mockLectures);

			// When
			List<LectureDTO> result = lectureService.getAvailableLectures(date);

			// Then
			assertNotNull(result);
			assertEquals(2, result.size());
			verify(lectureRepositoryJPA).findAllByDateGreaterThanEqual(date);
		}
	}

	@Nested
	@DisplayName("수강신청 목록 조회 기능 테스트")
	class CompletedLectureTests {

		@Test
		@DisplayName("특정 유저의 수강신청 목록 조회 성공")
		public void getCompletedLecturesByUserSuccess() {
			// Given
			Long studentId = 100L;

			Lecture lecture1 = new Lecture();
			lecture1.setId(1L);
			lecture1.setTitle("Java Lecture");
			lecture1.setInstructor("John Doe");
			lecture1.setDate(LocalDate.now());
			lecture1.setCapacity(10L);
			lecture1.setCurrent(5L);

			Lecture lecture2 = new Lecture();
			lecture2.setId(2L);
			lecture2.setTitle("Spring Lecture");
			lecture2.setInstructor("Jane Doe");
			lecture2.setDate(LocalDate.now().plusDays(1));
			lecture2.setCapacity(10L);
			lecture2.setCurrent(3L);

			Application application1 = new Application();
			application1.setId(1L);
			application1.setStudentId(studentId);
			application1.setLecture(lecture1);

			Application application2 = new Application();
			application2.setId(2L);
			application2.setStudentId(studentId);
			application2.setLecture(lecture2);

			List<Application> mockApplications = Arrays.asList(application1, application2);

			when(applicationRepositoryJPA.findAllByStudentId(studentId)).thenReturn(mockApplications);

			// When
			List<ApplicationDTO> result = lectureService.getCompletedLectures(studentId);

			// Then
			assertNotNull(result);
			assertEquals(2, result.size());
			verify(applicationRepositoryJPA).findAllByStudentId(studentId);
		}
	}
}
