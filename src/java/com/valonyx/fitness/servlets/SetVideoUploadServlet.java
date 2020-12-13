/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.fitness.servlets;

import com.badiyan.uk.online.util.FileMeta;
import com.badiyan.uk.online.util.MultipartRequestHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;


/**
 *
 * @author
 * marlo
 */
//this to be used with Java Servlet 3.0 API
@MultipartConfig
public class SetVideoUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	// this will store uploaded files
	
	private static HashMap<String,List<FileMeta>> file_hash = new HashMap<String,List<FileMeta>>();
	//private static List<FileMeta> files = new LinkedList<FileMeta>();

	/**
	 **************************************************
	 * URL: /upload
	 * doPost(): upload the files and other parameters
	 ****************************************************
	 */
	protected void doPost(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		System.out.println("doPost() invoked in SetVideoUploadServlet");
		
		_response.setContentType("application/json;charset=UTF-8");
		
		_response.setHeader("Access-Control-Allow-Origin", "*");
		_response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		_response.setHeader("Access-Control-Max-Age", "1000");
		_response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

		HttpSession session = _request.getSession(true);

		PrintWriter writer = null;
		
		try {
			
			file_hash.remove(session.getId()); // I don't need to maintain a list....
			
			List<FileMeta> files = (List<FileMeta>)file_hash.get(session.getId());
			if (files == null) {
				files = new LinkedList<FileMeta>();
				file_hash.put(session.getId(), files);
			}
			
			// 1. Upload File Using Java Servlet API
			//files.addAll(MultipartRequestHandler.uploadByJavaServletAPI(request));

			// 1. Upload File Using Apache FileUpload
			
			files.addAll(MultipartRequestHandler.uploadSetVideo(_request));

			System.out.println("files.size() >" + files.size());

			// Remove some files
			while (files.size() > 20) {
				files.remove(0);
			}

			// 2. Set response type to json
			_response.setContentType("application/json");

			// 3. Convert List<FileMeta> into JSON format
			ObjectMapper mapper = new ObjectMapper();

			// 4. Send resutl to client
			mapper.writeValue(_response.getOutputStream(), files);
			
		} catch (Exception x) {
			x.printStackTrace();
			
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[");
			b.append("{\"type\":\"ERROR\"," +
					"\"heading\":\"Oh Snap!\"," +
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"" +
					"}");
			b.append("]}");
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer = _response.getWriter();
			writer.println(b.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}
	
	protected void doOptions(HttpServletRequest request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		System.out.println("doOptions() invoked in SetVideoUploadServlet");
		
		_response.setContentType("application/json;charset=UTF-8");
		
		_response.setHeader("Access-Control-Allow-Origin", "*");
		_response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		_response.setHeader("Access-Control-Max-Age", "1000");
		_response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
		
	}

	/**
	 **************************************************
	 * URL: /upload?f=value
	 * doGet(): get file of index "f" from List<FileMeta> as an attachment
	 ****************************************************
	 */
	protected void doGet(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		
		System.out.println("doGet() invoked in SetVideoUploadServlet");

		// 1. Get f from URL upload?f="?"
		String value = _request.getParameter("f");
		
		HttpSession session = _request.getSession(true);

		try {
			
			List<FileMeta> files = (List<FileMeta>)file_hash.get(session.getId());
			if (files == null) {
				files = new LinkedList<FileMeta>();
				file_hash.put(session.getId(), files);
			}
			
			// 2. Get the file of index "f" from the list "files"
			FileMeta getFile = files.get(Integer.parseInt(value));

			// 3. Set the response content type = file content type 
			_response.setContentType(getFile.getFileType());

			// 4. Set header Content-disposition
			_response.setHeader("Content-disposition", "attachment; filename=\"" + getFile.getFileName() + "\"");

			// 5. Copy file inputstream to response outputstream
			InputStream input = getFile.getContent();
			OutputStream output = _response.getOutputStream();
			byte[] buffer = new byte[1024 * 10];

			for (int length = 0; (length = input.read(buffer)) > 0;) {
				output.write(buffer, 0, length);
			}

			output.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
