package com.proptiger.qa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileUtil {
	
	
	public static void main(String args[]){
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")+ "Pixie/tempFile2.jpeg");
		String URL="http://cdn.home-designing.com/wp-content/uploads/2014/10/simple-luxury-bedroom-design.jpeg";
		System.out.println(getFileSize(URL, file));
		
	}
	public static boolean getFileSize(String urlString, File destination) {   
		boolean flag=false;
        try {	
            URL website = new URL(urlString);
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(destination);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            double kilobytes = (destination.length() / 1024);
    		System.out.println(kilobytes+" KBs");
    		if(kilobytes>0){
    			flag=true;
    		}
        } catch (Exception e) {
            //e.printStackTrace();
        }
		return flag; 
    }

}
