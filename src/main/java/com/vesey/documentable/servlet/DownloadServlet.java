package com.vesey.documentable.servlet;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.session.DocumentBean;
import com.vesey.documentable.utils.FileUtils;
import com.vesey.documentable.utils.Utils;

/**
 *
 * @author Richard Clarke
 */
@WebServlet(name = "download", urlPatterns = { "/download" })
public class DownloadServlet extends HttpServlet {
	@Inject
	DBFacade dbFacade;

	@Inject
	DocumentBean documentBean;

	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient static final Logger log = Logger.getLogger(DownloadServlet.class);

	// private static final int BYTES_DOWNLOAD = 1024;
	private static final String errMsg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + "<HTML>\n" + "<HEAD><TITLE>Invalid Request</TITLE></HEAD>\n"
			+ "<BODY>\n" + "<h1>Invalid Request</h1>\n" + "<p>Unable to process request - invalid parameters.</p>\n" + "</BODY></HTML>";
	private static final String unauthorisedMsg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + "<HTML>\n"
			+ "<HEAD><TITLE>Unauthorised Request</TITLE></HEAD>\n" + "<BODY>\n" + "<h1>Unauthorised Request</h1>\n"
			+ "<p>Unable to process request - Unauthorised (not logged in?).</p>\n" + "</BODY></HTML>";

	public DownloadServlet() {
		super();
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("processRequest: Start");

		String fileName = null;
		String fullPath = null;
		String mimeType = null;

		String type = request.getParameter("type");
		if (Utils.isNotEmpty(type)) {
			switch (type) {
			case "document":
				String documentUuid = request.getParameter("d");
				log.info("processRequest: documentUuid = " + documentUuid);

				String html = documentBean.generateHTMLPreview(documentUuid);
				response.setContentType("text/html");
				response.getWriter().println(html);
				return;

			default:
				log.warn("processRequest: Unknown Type : " + type);
				response.setContentType("text/html");
				response.getWriter().println(errMsg);
				return;

			}
		}
		log.info("processRequest: File full path = " + fullPath);

		Boolean forceDownload = false;
		String forceDownloadStr = request.getParameter("fd");
		log.info("processRequest: Force Download = " + forceDownloadStr);
		if (!Utils.isEmpty(forceDownloadStr)) {
			forceDownload = true;
		}

		if (!Utils.isEmpty(fullPath)) {
			// log.info("processRequest: Full Path = " + fullPath);
			long fileLength = 0;
			try {
				File theFile = new File(fullPath);
				fileLength = theFile.length();
				fileName = theFile.getName();
			} catch (Exception e) {
				log.warn("processRequest: File Size cannot be determined. Cannot Continue");
				response.setContentType("text/html");
				response.getWriter().println(errMsg);
			}
			// log.info("processRequest: File Length = " + fileLength);
			if ((fileLength > 0) && !Utils.isEmpty(fileName)) {
				final ServletOutputStream out = response.getOutputStream();
				mimeType = FileUtils.getMimeType(fullPath);
				// log.info("processRequest: Mime Type = " + mimeType);
				response.setHeader("Content-Length", String.valueOf(fileLength));

				if (forceDownload) {
					response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "");
				} else {
					response.setHeader("Content-Disposition", "inline; filename=" + fileName + "");
				}
				// log.info("processRequest: Valid Parameters Passed - Attempting to return
				// file...");
				response.setContentType(mimeType + "; name=" + fileName + "");
				Boolean success = Utils.writeFileToOutputStream(out, fullPath);
				if (success) {
					log.info("processRequest: File successfully served");
				} else {
					log.warn("processRequest: Unable to serve file.");
					response.setContentType("text/html");
					response.getWriter().println(errMsg);
				}
				out.close();
			} else {
				log.warn("processRequest: File Length is zero.");
				response.setContentType("text/html");
				response.getWriter().println(errMsg);
			}
		} else {
			log.warn("processRequest: Invalid Parameters Passed - Cannot continue.");
			response.setContentType("text/html");
			response.getWriter().println(errMsg);
		}

		// log.info("processRequest: End");
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return "Download Servlet";
	}

}
