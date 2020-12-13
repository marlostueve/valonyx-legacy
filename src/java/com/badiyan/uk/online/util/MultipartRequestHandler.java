/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.CompanySettingsBean;
import com.badiyan.uk.beans.CourseBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
//import com.badiyan.uk.online.beans.AnokaCourseRequestBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.valonyx.fitness.beans.TrainingPlanMove;
import com.valonyx.fitness.beans.Workout;
import com.valonyx.fitness.beans.WorkoutSet;
import com.valonyx.fitness.servlets.FitnessServlet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.torque.TorqueException;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;



public class MultipartRequestHandler {

	
	
	
	public static List<FileMeta> uploadSetVideo(HttpServletRequest _request) throws IOException, ServletException, IllegalValueException, TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		UKOnlinePersonBean upload_person = null;
		CourseBean upload_course = null;
		
		List<FileMeta> files = new LinkedList<FileMeta>();

		// 1. Check request has multipart content
		boolean isMultipart = ServletFileUpload.isMultipartContent(_request);
		FileMeta temp = null;

		// 2. If yes (it has multipart "files")
		if (isMultipart) {

			// 2.1 instantiate Apache FileUpload classes
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			// 2.2 Parse the request
			try {

				// 2.3 Get all uploaded FileItem
				List<FileItem> items = upload.parseRequest(_request);
				System.out.println(" !! items.size() >" + items.size());
				
				String arg1 = "";
				String arg2 = "";
				String arg3 = "";
				
				for (FileItem item : items) {
					System.out.println("item(x) >" + item.getName());
					if (item.isFormField()) {
						System.out.println("item.getFieldName() >" + item.getFieldName() + ", item.getString() >" + item.getString());
						if (item.getFieldName().equals("arg1")) {
							arg1 = item.getString();
						} else if (item.getFieldName().equals("arg2")) {
							arg2 = item.getString();
						} else if (item.getFieldName().equals("arg3")) {
							arg3 = item.getString();
						}
					}
				}
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlanMove training_plan_move = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				Workout workout = Workout.getWorkout(Long.parseLong(arg3));
				
				WorkoutSet focus_set = null;
				
				ArrayList<WorkoutSet> sets = workout.getSets(training_plan_move);
				Iterator itr = sets.iterator();
				while (itr.hasNext()) {
					WorkoutSet set = (WorkoutSet)itr.next();
					if ( (focus_set == null) || 
							set.getVideoURLString().isEmpty()) {
						focus_set = set;
					}
				}
				
				if (focus_set == null) {
					throw new IllegalValueException("Unable to find set for video upload.");
				}
				
				String file_name = null;
				String url_str = "";
				String thumb_url_str = "";
				
				for (FileItem item : items) {
					
					file_name = focus_set.getValue() + "-" + item.getName();

					System.out.println("item(y) >" + file_name);

					if (!item.isFormField()) {

						// 2.7 Create FileMeta object
						temp = new FileMeta();
						temp.setFileName(file_name);
						temp.setContent(item.getInputStream());
						temp.setFileType(item.getContentType());
						temp.setFileSize(item.getSize() / 1024 + "Kb");
						
						// 2.7 Add created FileMeta object to List<FileMeta> files
						files.add(temp);

						try {
							//File aFile = new File("C:\\Tomcat7.0.33\\webapps\\Ecolab\\resources\\signatures\\" + item.getName());
							
							String filePath = "";
							String item_name_str = "";
							CUBean.createDirectory(CUBean.getProperty("cu.realPath") + File.separator + CUBean.getProperty("cu.resourcesFolder"), "set-videos");
							String baseDirectory = CUBean.getProperty("cu.realPath") + File.separator + CUBean.getProperty("cu.resourcesFolder") + File.separator + "set-videos";
							//CUBean.createDirectory(baseDirectory, companyId);
							filePath = baseDirectory + File.separator + file_name;
							
							//fileName = item.getName();
							
							File aFile = new File(filePath);
							item.write(aFile);

							if (aFile.createNewFile()) {
								System.out.println("File is created!");
							} else {
								System.out.println("File already exists. (9)");
							}
							
							//CompanySettingsBean settings_obj = company_obj.getSettings();
							url_str = CUBean.getProperty("cu.webDomain") + "/resources/set-videos/" + file_name;
							focus_set.setVideoURL(url_str);
							
							
							
								
							String realPath = CUBean.getProperty("cu.realPath");
							String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
							resourcesFolder = realPath + resourcesFolder;

							String file = "thumb-" + focus_set.getValue() + ".png";
							String png_file = resourcesFolder + "set-videos" + File.separator + file;

							//String pdf_file_source = resourcesFolder + "pdf/blank.pdf";

							try {
								int frameNumber = 1;
								Picture picture = FrameGrab.getFrameFromFile(new File(filePath), frameNumber);

								//for JDK (jcodec-javase)
								BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
								ImageIO.write(bufferedImage, "png", new File(png_file));

								thumb_url_str = CUBean.getProperty("cu.webDomain") + "/resources/set-videos/" + file;
								focus_set.setThumbnailURL(thumb_url_str);
							} catch (Exception x) {
								x.printStackTrace();
							}
							
							focus_set.save();
							
						} catch (Exception x) {
							x.printStackTrace();
						}
					}

				}
				
				

				// 2.8 Set "personId" parameter 
				for (FileMeta fm : files) {
					fm.setFileName(file_name);
					fm.setVideoURL(url_str);
					fm.setThumbURL(thumb_url_str);
					
					/*
					try {
						upload_course.save();
						fm.setCourseId(upload_course.getValue());
					} catch (Exception x) {
						x.printStackTrace();
					}
					*/
					
				}
				

			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
		return files;
	}
	
	
	
}
