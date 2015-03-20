package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class VersionServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;

	@Override
	String getUri() {
		return "/ver";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			JSONObject jo = new JSONObject();
			jo.put("appname", "dlmu.im");
			jo.put("verCode", "2015021116");
			jo.put("verName", "2.5.0");
			out.println(jo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}


	}

}
