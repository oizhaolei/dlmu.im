package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.JiveGlobals;
import org.json.JSONObject;

/**
 *
 */
public class VersionServlet extends AbstractImServlet {
	private static final long serialVersionUID = 9008949607840140354L;
	private static final String APK_URL = "dlmu.apkUrl";
	private static final String VER_CODE = "dlmu.verCode";
	private static final String VER_NAME = "dlmu.verName";

	private static final String IM_HOST = "dlmu.im.host";
	private static final String IM_PORT = "dlmu.im.port";

	@Override
	String getUri() {
		return "/ver";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			JSONObject imjo = new JSONObject();
			imjo.put("host", JiveGlobals.getProperty(IM_HOST, "202.118.89.187"));
			imjo.put("port", JiveGlobals.getIntProperty(IM_PORT, 5222));

			JSONObject jo = new JSONObject();
			jo.put("im", imjo);

			jo.put("apkUrl", JiveGlobals.getProperty(APK_URL,
					"http://www.dlmu.edu.cn/dlmu.im.apk"));
			jo.put("verCode", JiveGlobals.getProperty(VER_CODE, "2015021116"));
			jo.put("verName", JiveGlobals.getProperty(VER_NAME, "2.5.0"));
			out.println(jo.toString());
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}

	}
}
