package org.tttalk.openfire.plugin;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.StringUtils;
import org.jivesoftware.util.WebManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class DlmuIMPlugin implements Plugin {
	public static final String PLUGIN_NAME = "dlmu.im";

	private static final Logger log = LoggerFactory
			.getLogger(DlmuIMPlugin.class);

	private UserManager userManager;
	private GroupManager groupManager;
	private String domain;

	private MessageRouter router;

	public DlmuIMPlugin() {
		userManager = XMPPServer.getInstance().getUserManager();

		WebManager wm = new WebManager();
		wm.getGroupManager();
		groupManager = GroupManager.getInstance();
		UserManager.getUserProvider();
		domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
		router = XMPPServer.getInstance().getMessageRouter();
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
	}

	@Override
	public void destroyPlugin() {
		userManager = null;
	}

	public JSONArray org(String pid) throws Exception {
		JSONArray orgs = new JSONArray();
		// String sql =
		// "select  CODE, DEPARTNAME from RS_OU_DEPARTMENT where PARENTCODE = ?";
		// PreparedStatement ps = DbConnectionManager.getConnection()
		// .prepareStatement(sql);
		// ps.setString(1, pid);
		// ResultSet rs = ps.executeQuery();
		// while (rs.next()) {
		// String CODE = rs.getString("CODE");
		// String DEPARTNAME = rs.getString("DEPARTNAME");
		// JSONObject row = new JSONObject();
		// row.put("CODE", CODE);
		// row.put("DEPARTNAME", StringUtils.escapeHTMLTags(DEPARTNAME));
		// log.debug(row.toString());
		//
		// orgs.put(row);
		// }
		// rs.close();
		// ps.close();
		Collection<Group> groups = groupManager.getGroups();
		for (Group group : groups) {
			JSONObject row = new JSONObject();
			row.put("CODE", group.getName());
			row.put("DEPARTNAME",
					StringUtils.escapeHTMLTags(group.getDescription()));
			log.info(row.toString());

			orgs.put(row);

		}
		return orgs;
	}

	public JSONArray search(String u, String t) throws JSONException,
			SQLException {
		JSONArray results = new JSONArray();
		// Collection<User> users = userManager.getUsers();
		// for (User user : users) {
		// JSONObject row = new JSONObject();
		// row.put("name", user.getName());
		// row.put("username", user.getUsername());
		// log.debug(row.toString());
		//
		// results.put(row);
		// }
		String sql = "select  XH code, XM name, 's' utype from VI_YKT_XSXX where XM =? union select  GH code, XM, 't' utype from RS_HR_TEACHER_JZGJCXX where XM =?";
		log.info(sql);
		PreparedStatement ps = DbConnectionManager.getConnection()
				.prepareStatement(sql);
		ps.setString(1, u);
		ps.setString(1, t);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String code = rs.getString("code");
			String name = rs.getString("name");
			String utype = rs.getString("utype");
			JSONObject row = new JSONObject();
			row.put("code", code);
			row.put("name", StringUtils.escapeHTMLTags(name));
			row.put("utype", utype);
			log.info(row.toString());

			results.put(row);
		}
		rs.close();
		ps.close();

		return results;
	}

	public JSONArray send(String from_jid, String to_group, String subject,
			String body) throws Exception {
		JSONArray users = new JSONArray();

		Group group = groupManager.getGroup(to_group);
		Collection<JID> members = group.getMembers();
		Message message = new Message();
		message.setFrom(from_jid);
		message.setSubject(subject);
		message.setBody(body);
		for (JID jid : members) {
			message.setTo(jid);
			users.put(jid.toString());
			router.route(message);

			log.info(message.toXML());
		}
		return users;
	}

}
