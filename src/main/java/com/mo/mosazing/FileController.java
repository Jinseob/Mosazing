package com.mo.mosazing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.util.ResourceUtils;

public class FileController {
	public void fileCreate(String path, String stepnm, String msg, boolean newLine) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String filenm = "/" + sdf.format(cal.getTime()) + ".txt";
		File file;
		BufferedWriter writer;
		try {
			file = new File(ResourceUtils.getFile("classpath:log").getAbsolutePath() + filenm);
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(msg);
			if(newLine) writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void siseFileCreate(String msg) {
		String filenm = "/sise.txt";
		File file;
		BufferedWriter writer;
		try {
			file = new File(ResourceUtils.getFile("classpath:sise").getAbsolutePath() + filenm);
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(msg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
