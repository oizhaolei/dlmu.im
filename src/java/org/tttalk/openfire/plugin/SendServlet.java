package org.tttalk.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SendServlet extends AbstractImServlet {
	private static final long serialVersionUID = 1159875340630997082L;
	private static final Logger Log = LoggerFactory
			.getLogger(SendServlet.class);

	@Override
	String getUri() {
		return "/send";
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Log.info(request.toString());

		String from_jid = request.getParameter("from_jid");
		String to_group = request.getParameter("to_group");
		to_group = to_group.substring(0, to_group.indexOf('@'));
		String subject = request.getParameter("subject");
		String body = request.getParameter("body");

		// plugin.send(translators, qa_id, answer);

		PrintWriter out = response.getWriter();
		try {
			JSONArray l = plugin.send(from_jid, to_group, subject, body);

			out.println(l.toString());
		} catch (Exception e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}
	}

}
