package org.tttalk.openfire.plugin;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractImServlet extends HttpServlet {

	private static final long serialVersionUID = 7171852473707442671L;

	static final Logger Log = LoggerFactory.getLogger(AbstractImServlet.class);

	DlmuIMPlugin plugin;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		plugin = (DlmuIMPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin(DlmuIMPlugin.PLUGIN_NAME);
		AuthCheckFilter.addExclude(DlmuIMPlugin.PLUGIN_NAME + getUri());
	}

	// TODO: sign check
	abstract String getUri();

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");

		doGet(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
