/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vesey.documentable.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.activation.MimetypesFileTypeMap;

import org.jboss.logging.Logger;

/**
 *
 * @author Richard
 */
public class FileUtils {

	private static final Logger log = Logger.getLogger(FileUtils.class.toString());

	public static String getValidDocumentFilename(String fullPath) {

		Path p = Paths.get(fullPath);

		String folderPath = p.getParent().toString();
		String originalFileName = p.getFileName().toString();

		String newFileName = getValidDocumentFilename(originalFileName, folderPath);
		return folderPath + File.separator + newFileName;
	}

	// returns filename only for target folder path
	public static String getValidDocumentFilename(String originalFileName, String folderPath) {
		int count = 0;
		String cleanFileName = Utils.clean(originalFileName);
		cleanFileName = Utils.deSpace(cleanFileName);
		String possibleFullPath = getFullPath(folderPath, cleanFileName);

		while (count < 100) {

			File possibleFile = new File(possibleFullPath);

			if (possibleFile.exists()) {
				count++;
				cleanFileName = Utils.getAlternateFileName(cleanFileName, count);
				possibleFullPath = getFullPath(folderPath, cleanFileName);
			} else {
				return cleanFileName;
			}
		}
		return null;
	}

	/**
	 * Saves the given data to the given full path file name.
	 *
	 * @param fileName
	 *            - full path of file to save (or overwrite if it is already there).
	 * @param fileData
	 *            - data to save.
	 * @return - true if ok, otherwise false.
	 */
	public static File saveFile(String fileName, byte[] fileData) {
		log.info("saveFile; Start. fileName = " + fileName + ", file Data length = " + fileData.length);
		File theFile;
		FileOutputStream fos = null;
		fileName = getValidDocumentFilename(fileName);

		theFile = new File(fileName);

		log.info("saveFile: File being saved = " + theFile.getAbsolutePath());
		theFile.getParentFile().mkdirs();

		try {
			fos = new FileOutputStream(fileName, false);
			fos.write(fileData);
		} catch (Exception e) {
			log.error("saveFile: Error saving file: " + e.getMessage());
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e1) {
					log.error("saveFile: Error closing file: " + e1.getMessage());
				}
			}
		}
		// log.info("saveFile: End (Success)");
		return theFile;
	}

	public static File saveOrOverwriteFile(String fileName, byte[] fileData) {
		log.info("saveOrOverwriteFile; Start. fileName = " + fileName + ", file Data length = " + fileData.length);
		File theFile;
		FileOutputStream fos = null;

		theFile = new File(fileName);

		log.info("saveOrOverwriteFile: File being saved = " + theFile.getAbsolutePath());
		theFile.getParentFile().mkdirs();

		try {
			fos = new FileOutputStream(fileName, false);
			fos.write(fileData);
		} catch (Exception e) {
			log.error("saveOrOverwriteFile: Error saving file: " + e.getMessage());
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e1) {
					log.error("saveOrOverwriteFile: Error closing file: " + e1.getMessage());
				}
			}
		}
		log.info("saveOrOverwriteFile: End (Success)");
		return theFile;
	}

	public static String getFullPath(String folderPath, String fileName) {
		String fullPath;

		if (folderPath.endsWith("\\")) {
			folderPath = folderPath.substring(0, folderPath.lastIndexOf("\\"));
		}
		fullPath = folderPath + "\\" + fileName;
		return fullPath;
	}

	public static String getMimeType(String fileName) {
		if ((fileName == null) || (fileName.equals(""))) {
			log.error("getMimeType: File is null or blank");
			return null;
		}
		File fileInfo = new File(fileName);

		String actualName = fileInfo.getName();
		MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
		mimetypesFileTypeMap.addMimeTypes("application/pdf pdf PDF");
		mimetypesFileTypeMap.addMimeTypes("application/zip zip ZIP");
		mimetypesFileTypeMap.addMimeTypes("image/bmp bmp BMP");
		mimetypesFileTypeMap.addMimeTypes("image/gif gif GIF");
		mimetypesFileTypeMap.addMimeTypes("image/jpg jpg JPG");
		mimetypesFileTypeMap.addMimeTypes("image/tif tif TIF");
		mimetypesFileTypeMap.addMimeTypes("image/png png PNG");
		mimetypesFileTypeMap.addMimeTypes("text/csv csv CSV");
		mimetypesFileTypeMap.addMimeTypes("application/vnd.ms-excel xls XLS");
		// log.info("getMimeType: MIME Type found = " +
		// mimetypesFileTypeMap.getContentType(actualName));
		return mimetypesFileTypeMap.getContentType(actualName);
	}

}
