package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.JiveGlobals;

/**
 *
 */
public class ServiceServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;
	private static final String DLMU_SERVICE_ENTRY_POINT = "dlmu.service.entry.point";

	@Override
	String getUri() {
		return "/service";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> params = getParameterMap(request);
		if (!Utils.checkSign(params)) {
			response.getWriter().println("{}");
			return;
		}
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		try {
			params = new HashMap<String, String>();
			params.put("token", "11");
			String res = Utils.get(JiveGlobals.getProperty(
					DLMU_SERVICE_ENTRY_POINT,
					"http://202.118.89.129/dlmu_rest_webservice/001001"),
					params);

			out.println(res);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

}
