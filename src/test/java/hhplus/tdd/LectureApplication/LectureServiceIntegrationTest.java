package hhplus.tdd.LectureApplication;

import hhplus.tdd.LectureApplication.entity.Application;
import hhplus.tdd.LectureApplication.entity.Lecture;
import hhplus.tdd.LectureApplication.repository.ApplicationRepositoryJPA;
import hhplus.tdd.LectureApplication.repository.LectureRepositoryJPA;
import hhplus.tdd.LectureApplication.service.LectureService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LectureServiceIntegrationTest {

	@Autowired
	private LectureRepositoryJPA lectureRepositoryJPA;

	@Autowired
	private ApplicationRepositoryJPA applicationRepositoryJPA;

	@Autowired
	private LectureService lectureService;

	@BeforeEach
	public void setUp() {
		Lecture lecture = new Lecture();
		lecture.setTitle("");
		lecture.setInstructor("렌");
		lecture.setCapacity(30L);
		lecture.setCurrent(0L);
		lecture.setDate(java.time.LocalDate.now());

		lectureRepositoryJPA.save(lecture);
	}

	@Test
	@DisplayName("멀티스레드 환경에서 강의 정원을 초과하지 않고 수강 신청 처리")
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	void testApplyLectureConcurrency_WithCapacityLimit() throws InterruptedException {
		// Given
		Lecture lecture = lectureRepositoryJPA.findAll().get(0);
		Long lectureId = lecture.getId();
		Long capacity = lecture.getCapacity();
		long numberOfRequests = capacity + 20;

		ExecutorService executorService = Executors.newFixedThreadPool((int) numberOfRequests);
		CountDownLatch latch = new CountDownLatch((int) numberOfRequests);
		AtomicInteger successfulApplications = new AtomicInteger();
		AtomicInteger failedApplications = new AtomicInteger();

		for (int i = 0; i < numberOfRequests; i++) {
			final Long studentId = (long) i + 1;
			executorService.submit(() -> {
				try {
					lectureService.applyLecture(lectureId, studentId);
					successfulApplications.incrementAndGet();
				} catch (Exception e) {
					failedApplications.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		// Then
		assertEquals(capacity, successfulApplications.get());
		assertEquals(numberOfRequests - capacity, failedApplications.get());
		Lecture updatedLecture = lectureRepositoryJPA.findById(lectureId).orElseThrow();
		assertEquals((Long) (long) capacity, updatedLecture.getCurrent());
	}

	@Test
	@DisplayName("같은 사용자가 동일한 특강에 대해 신청 성공하지 못하도록 처리되었는지 테스트")
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	void shouldNotAllowDuplicateApplicationsBySameStudentForSameLecture() throws InterruptedException {
		// Given
		Lecture lecture = lectureRepositoryJPA.findAll().get(0);
		Long lectureId = lecture.getId();
		Long studentId = 1L;
		long numberOfRequests = 5;

		ExecutorService executorService = Executors.newFixedThreadPool((int) numberOfRequests);
		CountDownLatch latch = new CountDownLatch((int) numberOfRequests);
		AtomicInteger successfulApplications = new AtomicInteger();
		AtomicInteger failedApplications = new AtomicInteger();

		for (int i = 0; i < numberOfRequests; i++) {
			executorService.submit(() -> {
				try {
					lectureService.applyLecture(lectureId, studentId);
					successfulApplications.incrementAndGet();
				} catch (Exception e) {
					failedApplications.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		// Then
		assertEquals(1, successfulApplications.get());
		assertEquals(4, failedApplications.get());
		Application storedApplication = applicationRepositoryJPA.findByStudentIdAndLectureId(studentId, lectureId).orElseThrow();;
		assertEquals(storedApplication.getLecture().getId(), lectureId);
		assertEquals(storedApplication.getStudentId(), studentId);
	}
}
