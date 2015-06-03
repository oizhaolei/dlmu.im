package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 *
 */
public class AddStudentFromClassServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;

	@Override
	String getUri() {
		return "/addStudentFromClass";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> params = getParameterMap(request);
		if (!Utils.checkSign(params)) {
			response.getWriter().println("{}");
			return;
		}

		response.setCharacterEncoding("UTF-8");

		Log.info(request.toString());
		String jid = request.getParameter("jid");
		//String njdm = request.getParameter("njdm");
		//String bjh = request.getParameter("bjh");
		// 116020@im.dlmu.edu.cn = >116020

		// int start = 0;
		// int end = pid.indexOf("@");
		// if (start >= 0 && end >= start) {
		// pid = pid.substring(start, end);
		// }
		PrintWriter out = response.getWriter();
		try {
			JSONObject l = plugin.studentClass(jid);
			out.println(l.toString());
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}
}
