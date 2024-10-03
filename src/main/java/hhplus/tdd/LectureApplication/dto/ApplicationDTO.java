package hhplus.tdd.LectureApplication.dto;

import lombok.Data;

@Data
public class ApplicationDTO {
	private Long applicationId;

	private Long studentId;

	private LectureDTO lecture;
}
