package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

		Log.info(request.toString());
		String u = request.getParameter("u");
		String t = request.getParameter("t");

		PrintWriter out = response.getWriter();
		try {
			JSONArray l = plugin.search(u, t);

			JSONObject jo = new JSONObject();
			jo.put("u", u);
			jo.put("t", t);
			out.println(jo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}
	}

}
