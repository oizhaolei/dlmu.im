package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UserProperyServlet extends AbstractImServlet {
	private static final long serialVersionUID = -961330208339141998L;
	private static final Logger Log = LoggerFactory
			.getLogger(UserProperyServlet.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> params = getParameterMap(request);
		if (!Utils.checkSign(params)) {
			response.getWriter().println("{}");
			return;
		}
		Log.info(request.toString());

		String username = request.getParameter("user");
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		PrintWriter out = response.getWriter();
		try {
			if (value == null) {
				value = plugin.getProperty(username, key, null);
				out.println(value);
			} else {
				plugin.updateProperty(username, key, value);
				out.println("success");
			}
		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	@Override
	String getUri() {
		return "/userproperties";
	}
}