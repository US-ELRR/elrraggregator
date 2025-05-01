package com.deloitte.elrr.aggregator.utils;

import java.util.Arrays;

public class ArrayToString {

	public static String convertArrayToString(String[] strings) {

		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(strings).forEach(stringBuffer::append);
		return stringBuffer.toString();

	}

}
