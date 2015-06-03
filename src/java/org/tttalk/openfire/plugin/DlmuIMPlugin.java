package org.tttalk.openfire.plugin;

import java.io.File;
import java.sql.Connection;
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
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
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

	private static final Logger log = LoggerFactory.getLogger(DlmuIMPlugin.class);

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

	public JSONObject teacher(String pid) throws Exception {
		JSONObject result = new JSONObject();

		JSONArray orgs = new JSONArray();
		String sql = "select  CODE||'@" + domain + "' as CODE, DEPARTNAME from RS_OU_DEPARTMENT where PARENTCODE = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			rs = ps.executeQuery();
			while (rs.next()) {
				String CODE = rs.getString("CODE");
				String DEPARTNAME = rs.getString("DEPARTNAME");
				JSONObject row = new JSONObject();
				row.put("jid", CODE);
				row.put("name", StringUtils.escapeHTMLTags(new String(DEPARTNAME.getBytes(), "UTF-8")));
				log.debug(row.toString());

				orgs.put(row);
			}
			rs.close();
			ps.close();
		} finally {
			DbConnectionManager.closeConnection(rs, ps, conn);
		}

		JSONArray members = new JSONArray();

		try {
			String groupname = pid;
			Group group = groupManager.getGroup(groupname);
			for (JID jid : group.getMembers()) {
				JSONObject row = new JSONObject();
				try {
					String string = jid.toString();
					String username = string.substring(0, string.indexOf('@'));
					User user = userManager.getUser(username);
					row.put("name", user.getName());

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				row.put("jid", jid);
				members.put(row);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		result.put("orgs", orgs);
		result.put("members", members);
		return result;
	}

	public JSONObject studentClass(String pid) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray orgs = new JSONArray();
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String college = null, njdm = null, bjh = null;

		if (pid == null || pid.isEmpty()) {
			college = null;
		} else {
			String[] p = pid.split("_");
			if (p.length == 1)
				college = p[0];
			if (p.length == 2) {
				college = p[0];
				njdm = p[1];
			}
			if (p.length == 3) {
				college = p[0];
				njdm = p[1];
				bjh = p[2];
			}
		}

		try {
			conn = DbConnectionManager.getConnection();
			// 显示学院
			if (college == null) {
				sql = "select distinct depid||'@" + domain
						+ "' as CODE ,glz as DEPARTNAME from ecard.DATACT_GY_CLASSLIST_V t where glz is not null order by code ";
				ps = conn.prepareStatement(sql);
			} else {
				if (njdm == null) {
					// 显示年级
					sql = "select distinct depid||'_'||substr(bh,3,4)||'@" + domain
							+ "' as code,substr(bh,3,4)||'级' as  deptname from ecard.DATACT_GY_CLASSLIST_V t where t.depid=? order by code desc";
					ps = conn.prepareStatement(sql);
					ps.setString(1, college);
				} else {
					// 显示班级
					if (bjh == null) {
						sql = "select distinct depid||'_'||substr(bh,3,4)||'_'||bh||'@"
								+ domain
								+ "' as code, bjbm as deptname from ecard.DATACT_GY_CLASSLIST_V t where t.depid=? and substr(bh,3,4)=? order by code desc";
						ps = conn.prepareStatement(sql);
						ps.setString(1, college);
						ps.setString(2, njdm);
					} else {
						// 显示班级里面的学生
						sql = "select xh||'@" + domain + "' as code, xm as deptname from ecard.DATACT_JW_XS_XJB t where t.bjh=? order by code";
						ps = conn.prepareStatement(sql);
						ps.setString(1, bjh);
					}
				}

			}

			rs = ps.executeQuery();
			while (rs.next()) {
				String CODE = rs.getString("CODE");
				String DEPARTNAME = rs.getString("DEPARTNAME");
				JSONObject row = new JSONObject();
				row.put("jid", CODE);
				row.put("name", StringUtils.escapeHTMLTags(new String(DEPARTNAME.getBytes(), "UTF-8")));
				log.debug(row.toString());

				orgs.put(row);
			}
			rs.close();
			ps.close();
		} finally {
			DbConnectionManager.closeConnection(rs, ps, conn);
		}

		JSONArray members = new JSONArray();

		try {
			String groupname = bjh;
			Group group = groupManager.getGroup(groupname);
			for (JID jid : group.getMembers()) {
				JSONObject row = new JSONObject();
				try {
					String string = jid.toString();
					String username = string.substring(0, string.indexOf('@'));
					User user = userManager.getUser(username);
					row.put("name", user.getName());

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				row.put("jid", jid);
				members.put(row);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		result.put("orgs", orgs);
		result.put("members", members);
		return result;
	}

	public JSONObject studentCourse(String pid, String isStudent) throws Exception {
		JSONObject result = new JSONObject();

		JSONArray orgs = new JSONArray();
		String sql = "select  CODE||'@" + domain + "' as CODE, DEPARTNAME from RS_OU_DEPARTMENT where PARENTCODE = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			rs = ps.executeQuery();
			while (rs.next()) {
				String CODE = rs.getString("CODE");
				String DEPARTNAME = rs.getString("DEPARTNAME");
				JSONObject row = new JSONObject();
				row.put("jid", CODE);
				row.put("name", StringUtils.escapeHTMLTags(new String(DEPARTNAME.getBytes(), "UTF-8")));
				log.debug(row.toString());

				orgs.put(row);
			}
			rs.close();
			ps.close();
		} finally {
			DbConnectionManager.closeConnection(rs, ps, conn);
		}

		JSONArray members = new JSONArray();

		try {
			String groupname = pid;
			Group group = groupManager.getGroup(groupname);
			for (JID jid : group.getMembers()) {
				JSONObject row = new JSONObject();
				try {
					String string = jid.toString();
					String username = string.substring(0, string.indexOf('@'));
					User user = userManager.getUser(username);
					row.put("name", user.getName());

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				row.put("jid", jid);
				members.put(row);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		result.put("orgs", orgs);
		result.put("members", members);
		return result;
	}

	public JSONArray search(String s, String t) throws JSONException, SQLException {
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
		String sql = "select  XH code, XM name, 's' utype from VI_YKT_XSXX where XM =? union all select  GH code, XM, 't' utype from RS_HR_TEACHER_JZGJCXX where XM =?";
		log.info(sql);
		log.info(s);
		log.info(t);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, s);
			ps.setString(2, t);
			rs = ps.executeQuery();
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
		} finally {
			DbConnectionManager.closeConnection(rs, ps, conn);
		}

		return results;
	}

	public JSONArray send(String from_jid, String to_group, String subject, String body) throws Exception {
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

	public void changePassword(String username, String password) {

		try {
			User user = userManager.getUser(username);
			user.setPassword(password);
			log.info(String.format("changePassword:%s,%s", username, password));
		} catch (UserNotFoundException e) {
			log.info(username, e);
		}
	}

	public void createAccount(String username, String password) {
		try {
			User user = userManager.createUser(username, password, null, null);
			log.info(String.format("createAccount:%s,%s", user.getUID(), user.getUsername()));
		} catch (UserAlreadyExistsException e) {
			log.info(username + " UserAlreadyExists.");
			changePassword(username, password);
		}
	}

	public String getProperty(String username, String key, String def) {
		try {
			User user = userManager.getUser(username);
			if (key == null)
				return user.getProperties().toString();
			String value = user.getProperties().get(key);
			if (value == null) {
				value = def;
			}
			return value;
		} catch (UserNotFoundException e) {
			log.error(username, e);
			return def;
		}
	}

	public void updateProperty(String username, String key, String value) throws UserNotFoundException {
		User user = userManager.getUser(username);
		user.getProperties().put(key, value);
	}

}
