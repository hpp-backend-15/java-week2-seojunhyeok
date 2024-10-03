package hhplus.tdd.LectureApplication.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LectureDTO {
	private Long lectureId;

	private String title;

	private String instructor;

	private LocalDate date;

	private Long capacity;

	private Long current;
}