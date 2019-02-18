package com.mongo.domain.constants;

import java.util.stream.Stream;

public class ClientRole {

	public final static String APP = "APP";

	public static String[] listAsString() {
		return Stream.of(APP).toArray(String[]::new);
	}
}
