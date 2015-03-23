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
		// response.setContentType("text/json;charset=GBK");
		response.setCharacterEncoding("GBK");

		Log.info(request.toString());
		String pid = request.getParameter("pid");

		PrintWriter out = response.getWriter();
		try {
			JSONObject l = plugin.org(pid);
			out.println(l.toString());
		} catch (Exception e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}
	}

}
