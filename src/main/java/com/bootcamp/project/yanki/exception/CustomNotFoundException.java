package com.bootcamp.project.yanki.exception;

public class CustomNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8299533233042901359L;

public CustomNotFoundException(String message) {
    super(message);
  }
}
