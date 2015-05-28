package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UserServlet extends AbstractImServlet {
	private static final long serialVersionUID = -9126153402683026148L;
	private static final Logger Log = LoggerFactory
			.getLogger(UserServlet.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> params = getParameterMap(request);
		if (!Utils.checkSign(params)) {
			response.getWriter().println("{}");
			return;
		}
		Log.info(request.toString());

		String username = request.getParameter("username");
		PrintWriter out = response.getWriter();
		try {
			JSONObject jo = plugin.getUser(username);
			out.println(jo);
		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	@Override
	String getUri() {
		return "/user";
	}
}