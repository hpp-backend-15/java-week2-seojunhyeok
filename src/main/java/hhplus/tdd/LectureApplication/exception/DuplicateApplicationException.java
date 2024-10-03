package hhplus.tdd.LectureApplication.exception;

public class DuplicateApplicationException extends RuntimeException {
	public DuplicateApplicationException(String message) {
		super(message);
	}
}