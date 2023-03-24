package com.mo.mosazing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		String realData = "가,abc,100,\"1,000\",\"400,000,000\",RXT100";		
		String tempData = realData;
		Pattern ptrn = Pattern.compile("\"(.*?)\"");	// 따옴표 안에 있는 패턴 추출.
		Matcher matcher = ptrn.matcher(tempData);
		System.out.println("수정전 : " + tempData);
		while(matcher.find()) {
			System.out.println(matcher.group());
			tempData = matcher.replaceFirst(matcher.group().replace(",", "").replace("\"", ""));
			matcher = ptrn.matcher(tempData);
		}
		System.out.println("수정후 : " + tempData);
		System.out.println("원본 : " + realData);
	}
}
