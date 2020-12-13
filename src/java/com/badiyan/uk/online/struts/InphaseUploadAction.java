/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.valonyx.fitness.beans.Food;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.http.*;

import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class InphaseUploadAction
		extends Action {

	public ActionForward execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
			throws Exception {
		System.out.println("execute() invoked in InphaseUploadAction");


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlineCompanyBean adminCompany = null;
		HttpSession session = _request.getSession(false);

		try {
			// Check the session to see if there's a course in progress...

			if (session != null) {
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				if (loginBean == null) {
					return (_mapping.findForward("session_expired"));
				} else {
					try {
						UKOnlinePersonBean person = (UKOnlinePersonBean) loginBean.getPerson();
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
							return (_mapping.findForward("session_expired"));
						}
					} catch (IllegalValueException x) {
						return (_mapping.findForward("session_expired"));
					}
				}
			} else {
				return (_mapping.findForward("session_expired"));
			}

			String destination_file_path = null;
			String filename = null;

			String realPath = CUBean.getProperty("cu.realPath");
			String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
			if (resourcesFolder == null) {
				throw new IllegalValueException("Property not defined: cu.resourcesFolder");
			}

			CUBean.createDirectory(realPath, resourcesFolder + "user-upload-data");
			// ensure that an upload directory exists for this course

			String destination_folder = realPath + resourcesFolder + "user-upload-data/";


			UserUploadForm fileForm = (UserUploadForm) _form;
			FormFile uploadedFile = fileForm.getUploadInput();

			System.out.println("++++++++++++++ - " + uploadedFile.getFileName());

			filename = uploadedFile.getFileName();
			InputStream uploadInStream = uploadedFile.getInputStream();

			int c = 0;
			c = uploadInStream.read();
			if (c != -1) {
				destination_file_path = destination_folder + filename;
				System.out.println("destination_file_path >" + destination_file_path);
				FileOutputStream fOut = new FileOutputStream(destination_file_path);
				while (c != -1) {
					fOut.write(c);
					c = uploadInStream.read();
				}
				fOut.flush();
				fOut.close();
			} else {
				throw new IllegalValueException("Cannot find specified file.");
			}

			UKOnlineAccessManager am = (UKOnlineAccessManager) UKOnlineAccessManager.getInstance();

			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(destination_file_path));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			int sheet_index = 1;

			StringBuffer results = new StringBuffer();

			HSSFSheet sheet = wb.getSheetAt(sheet_index);
			int rows_modified = 0;
			int row_index;

			int num_matched = 0;
			int num_no_match = 0;
			int num_no_unique = 0;

			int num_found_inactive = 0;

			for (row_index = 1; row_index < 99999; row_index++) {

				HSSFRow obj_row = sheet.getRow(row_index);

				if (obj_row == null) {
					break;
				} else {
					try {

						boolean modified = false;

						HSSFCell dbNumber_cell = obj_row.getCell((short) 0);
						HSSFCell group_cell = obj_row.getCell((short) 1);
						HSSFCell longDesc_cell = obj_row.getCell((short) 2);
						HSSFCell shortDesc_cell = obj_row.getCell((short) 3);
						HSSFCell comName_cell = obj_row.getCell((short) 4);
						HSSFCell manufacturerName_cell = obj_row.getCell((short) 4);
						
						HSSFCell protein_cell = obj_row.getCell((short) 17);
						HSSFCell fat_cell = obj_row.getCell((short) 20);
						HSSFCell carb_cell = obj_row.getCell((short) 23);

						String dbNumber = this.getStringValueFromCell(dbNumber_cell).trim();
						String group = this.getStringValueFromCell(group_cell).trim();
						String longDesc = this.getStringValueFromCell(longDesc_cell).trim();
						String shortDesc = this.getStringValueFromCell(shortDesc_cell).trim();
						String comName = this.getStringValueFromCell(comName_cell).trim();
						String manufacturerName = this.getStringValueFromCell(manufacturerName_cell).trim();
						
						BigDecimal protein = this.getBigDecimalValueFromCell(protein_cell);
						BigDecimal fat = this.getBigDecimalValueFromCell(fat_cell);
						BigDecimal carb = this.getBigDecimalValueFromCell(carb_cell);

						System.out.println("");
						System.out.println("row >" + row_index + "<");
						System.out.println("dbNumber >" + dbNumber + "<");
						System.out.println("group >" + group + "<");
						System.out.println("longDesc >" + longDesc + "<");
						System.out.println("shortDesc >" + shortDesc + "<");
						System.out.println("comName >" + comName + "<");
						System.out.println("manufacturerName >" + manufacturerName + "<");
						
						System.out.println("protein >" + protein + "<");
						System.out.println("fat >" + fat + "<");
						System.out.println("carb >" + carb + "<");
						
						
						
						Food fud = null;
						try {
							fud = Food.getFood(longDesc, CUBean.one_hundred, Food.GRAMS_SERVING_SIZE_TYPE, fat, carb, protein);
						} catch (ObjectNotFoundException x) {
							fud = new Food();
							fud.setCreateOrModifyPerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4424));
							fud.setName(longDesc);
							fud.setDescription(shortDesc);
							fud.setManufacturer(manufacturerName);
							fud.setCategory(group);
							fud.setFat(fat);
							fud.setCarbs(carb);
							fud.setProtein(protein);

							fud.setServingSize(CUBean.one_hundred);
							fud.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
							fud.setActive(true);
							fud.setPublic(true);
							fud.save();
						}
						

						
						/*
						UKOnlineCompanyBean sano = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
						VendorRet sp = VendorRet.getVendor(595);
						
						// first see if there's a 
						CheckoutCodeBean code = null;
						try {
							code = CheckoutCodeBean.getCheckoutCodeForVendorAndProductId(sp, Integer.parseInt(productNumber));
							BigDecimal order_cost = new BigDecimal(unitCost.substring(1));
							if (order_cost.compareTo(code.getOrderCost()) != 0) {
								System.out.println("unit cost change!! ><");
								code.setOrderCost(order_cost);
								code.save();
							}
						} catch (ObjectNotFoundException x) {
							// no matching product for product number
							String desc_substr = productDesc.substring(0, productDesc.length());
							System.out.println("desc_substr >" + desc_substr + "<");
							String contentStr = content.substring(0, content.indexOf(' '));
							System.out.println("contentStr >" + contentStr + "<");
							Vector search_results = CheckoutCodeBean.getCheckoutCodesStartingWith(sano, desc_substr, 0);
							Iterator itr = search_results.iterator();
							CheckoutCodeBean match_code = null;
							int num_matches = 0;
							while (itr.hasNext()) {
								CheckoutCodeBean found_code = (CheckoutCodeBean)itr.next();
								if (search_results.size() == 1) {
									match_code = found_code;
									System.out.println("       ***found_code(1) >" + found_code.getLabel() + "<");
									num_matches++;
								} else {
									if (found_code.getDescriptionString().indexOf(contentStr) > -1) {
										match_code = found_code;
										System.out.println("       ***found_code(2) >" + found_code.getLabel() + "<");
										num_matches++;
									}
								}
							}
							if (num_matches == 1) {
								System.out.println("       !!!saving ><");
								match_code.setItemNumber(Integer.parseInt(productNumber));
								match_code.save();
							}
						}
						*/
						

					} catch (Exception x) {
						results.append("Error processing user :" + x.getMessage() + " <br />");
						x.printStackTrace();
					}
				}


			}

			System.out.println("num_matched >" + num_matched);
			System.out.println("num_no_match >" + num_no_match);
			System.out.println("num_no_unique >" + num_no_unique);
			System.out.println("num_found_inactive >" + num_found_inactive);


			results.append(rows_modified + " user" + ((rows_modified == 1) ? "" : "s") + " affected");

			session.setAttribute("upload_results", results);
		} catch (IllegalValueException x) {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		} catch (Exception x) {
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null) {
			if ("request".equals(_mapping.getScope())) {
				_request.removeAttribute(_mapping.getAttribute());
			} else {
				session.removeAttribute(_mapping.getAttribute());
			}
		}

		// Forward control to the specified success URI
		return (_mapping.findForward("success"));
	}

	private String getStringValueFromCell(HSSFCell _cell) {
		if (_cell == null) {
			return "";
		}
		String str = null;
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			str = (int) _cell.getNumericCellValue() + "";
		} else {
			str = _cell.getStringCellValue();
		}

		return str;
	}
	

	private BigDecimal getBigDecimalValueFromCell(HSSFCell _cell) throws IllegalValueException {
		if (_cell == null) {
			return BigDecimal.ZERO;
		}
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return new BigDecimal(_cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
		} else {
			throw new IllegalValueException("NaN");
		}
	}
}