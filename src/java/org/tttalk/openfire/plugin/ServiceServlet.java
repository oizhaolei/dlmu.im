package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 */
public class ServiceServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;

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
			JSONArray data = new JSONArray();
			JSONObject l;
			for (int i = 0; i < 10; i++) {
				l = new JSONObject();
				l.put("jid", "service_" + i);
				l.put("title", "title_" + i);
				l.put("url", "http://dlmuim:9090/session-summary.jsp" + i);
				data.put(l);
			}
			out.println(data.toString());
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

}
