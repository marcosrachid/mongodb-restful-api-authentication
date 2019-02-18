package com.mongo.domain.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Role {

	public final static String ADMIN = "ADMIN";
	public final static String USER = "USER";
	public final static String ANONYMOUS = "ANONYMOUS";

	public static Set<String> set() {
		return Stream.of(ANONYMOUS, USER, ADMIN).collect(Collectors.toSet());
	}
}
