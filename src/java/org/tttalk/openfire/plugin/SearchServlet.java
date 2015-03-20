package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

/**
 *
 */
public class SearchServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;

	@Override
	String getUri() {
		return "/search";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// response.setContentType("text/json;charset=GBK");
		response.setCharacterEncoding("GBK");

		Log.info(request.toString());
		String s = request.getParameter("s");
		String t = request.getParameter("t");

		PrintWriter out = response.getWriter();
		try {
			JSONArray l = plugin.search(s, t);

			out.println(l);
		} catch (Exception e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}
	}

}
