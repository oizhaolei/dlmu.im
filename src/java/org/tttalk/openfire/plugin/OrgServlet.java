package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 *
 */
public class OrgServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;

	@Override
	String getUri() {
		return "/org";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");

		Log.info(request.toString());
		String pid = request.getParameter("jid");
		// org_116020@im.dlmu.edu.cn = >116020

		int start = pid.indexOf("org_") + 4;
		int end = pid.indexOf("@");
		if (start >= 0 && end >= start) {
			pid = pid.substring(start, end);
		}
		PrintWriter out = response.getWriter();
		try {
			JSONObject l = plugin.org(pid);
			out.println(l.toString());
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

}
