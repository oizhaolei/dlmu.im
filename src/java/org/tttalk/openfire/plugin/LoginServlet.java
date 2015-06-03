package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.JiveGlobals;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class LoginServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;
	private static final String DLMU_LOGIN_ENTRY_POINT = "dlmu.login.entry.point";

	@Override
	String getUri() {
		return "/login";
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

		Log.info(request.toString());
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");

		PrintWriter out = response.getWriter();
		try {
			JSONObject jo = new JSONObject();
			jo.put("userid", userid);
			jo.put("password", password);

			final Map<String, String> map = new HashMap<String, String>();
			map.put("userid", userid);
			map.put("passwd", password);
			String res = Utils.get(JiveGlobals.getProperty(
					DLMU_LOGIN_ENTRY_POINT,
					"http://202.118.89.129/dlmu_rest_webservice/002001"), map);

			try {
				// if exists， try create user on openfire
				JSONObject d = new JSONObject(res);
				if ("99".equals(d.getString("code"))) {
					password = userid.substring(userid.length() - 4);
					plugin.createAccount(userid, password);
				}
			} catch (Exception e) {
				Log.error(e.getMessage(), e);
			}

			out.println(res);
		} catch (JSONException e) {
			Log.error(e.getMessage(), e);
		}
	}

}
