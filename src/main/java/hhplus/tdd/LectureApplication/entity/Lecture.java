package hhplus.tdd.LectureApplication.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String instructor;

	private LocalDate date;

	private Long capacity;

	private Long current;

	@OneToMany(mappedBy = "lecture")
	private List<Application> applications;

	public Lecture(Long id, String title, String instructor, LocalDate date, Long capacity, Long current) {
		this.id = id;
		this.title = title;
		this.instructor = instructor;
		this.date = date;
		this.capacity = capacity;
		this.current = current;
	}
}