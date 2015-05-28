package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UserServlet extends AbstractImServlet {
	private static final long serialVersionUID = -9126153402683026148L;
	private static final String DLMU_USER_ENTRY_POINT = "dlmu.user.entry.point";
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
		response.setCharacterEncoding("UTF-8");
		Log.info(request.toString());

		String userid = request.getParameter("userid");
		PrintWriter out = response.getWriter();
		try {
			final Map<String, String> map = new HashMap<String, String>();
			map.put("userid", userid);

			String res = Utils.get(JiveGlobals.getProperty(
					DLMU_USER_ENTRY_POINT,
					"http://202.118.89.129/dlmu_rest_webservice/000102"), map);

			out.println(res);
		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	@Override
	String getUri() {
		return "/user";
	}
}