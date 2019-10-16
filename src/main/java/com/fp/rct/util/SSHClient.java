/**
 * File Name：SSHClient.java
 *
 * Version：
 * Date：2012-3-23
 * Copyright CloudWei Dev Team 2012 
 * All Rights Reserved.
 *
 */
package com.fp.rct.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 
 * Project Name：com.cloudwei.monitor.agent Class Name：SSHClient Class Desc：
 * Author：tigaly Create Date：2012-3-23 下午2:18:55 Last Modified By：tigaly Last
 * Modified：2012-3-23 下午2:18:55 Remarks：
 * 
 * @version
 * 
 */
@Service
public class SSHClient {
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Connection conn;
	private Session sess;
	private InputStream stdout;
	private InputStream stderr;
	private OutputStream stdin;
	private static final int TIME_OUT = 1000 * 2 * 60;

	// config ; snmp-server host 10.240.1.181 traps version 1 public ; end ;
	// show snmp host

	public boolean connect(String ip, String user, String password) {
		logging.info("ssh start:" + ip);
		conn = new Connection(ip);
		try {
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(user, password);
			logging.info("ssh login results:" + isAuthenticated);
			if (isAuthenticated == false) {
				conn.close();
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logging.error(e.getMessage());
			return false;
		}
	}

	public String processHandles(String command) {
		String result = "";
		String str = null;
		try {
			logging.info("Start command :" + command);
			sess = conn.openSession();
			sess.execCommand(command);
			// out stream
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			String response = null;
			while ((response = br.readLine()) != null) {
				result += response + "\n";
			}
			logging.info(command + " is over");
			br.close();

			str = new String(result.getBytes("gb2312"), "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
			logging.debug("Session exit " + sess.getExitStatus());
			sess.close();
		}
		logging.info("Command result:" + str);
		return str;
	}

	public BufferedReader processHandlesIO(String command) {
		BufferedReader br = null;
		try {
			logging.info("Start command :" + command);
			sess = conn.openSession();
			sess.execCommand(command);
			// out stream
			InputStream stdout = new StreamGobbler(sess.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
			logging.debug("Session exit " + sess.getExitStatus());
			sess.close();
		}
		return br;
	}

	public List<String> processHandle(String command) {
		List<String> result = result = new ArrayList<String>();
		try {
			logging.info(command);
			sess = conn.openSession();
			sess.execCommand(command);
			// out stream
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			String response = null;
			while ((response = br.readLine()) != null) {
				result.add(response);
			}
			// err stream
			br.close();
			stdout = new StreamGobbler(sess.getStderr());
			br = new BufferedReader(new InputStreamReader(stdout));
			while ((response = br.readLine()) != null) {
				if (response.trim().length() > 0)
					// throw new Exception(response);
					logging.error(response);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
			// 返回0正常
			logging.info("Session exit " + sess.getExitStatus() + ",数据：" + result.size());
			sess.close();
		}
		return result;
	}

	public List<String> processHandleSess(String command) {
		List<String> result = result = new ArrayList<String>();
		try {
			logging.info(command);
			if (sess == null) {
				logging.info("创建Session");
				sess = conn.openSession();
			}
			sess.execCommand(command);
			// out stream
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			String response = null;
			while ((response = br.readLine()) != null) {
				result.add(response);
			}
			// err stream
			br.close();
			stdout = new StreamGobbler(sess.getStderr());
			br = new BufferedReader(new InputStreamReader(stdout));
			while ((response = br.readLine()) != null) {
				if (response.trim().length() > 0)
					// throw new Exception(response);
					logging.error(response);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void processHandlessh(String command) {
		try {
			try {
				sess = conn.openSession();
				sess.requestDumbPTY();
				sess.startShell();
				stdout = sess.getStdout();
				stderr = sess.getStderr();
				stdin = sess.getStdin();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//io流操作
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stdin));
			bw.write(command + "\n\r");
			bw.flush();

			while (true) {
				if ((stdout.available() == 0) && (stderr.available() == 0)) {
					int conditions = sess.waitForCondition(
							ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA | ChannelCondition.EOF, 2000);
					if ((conditions & ChannelCondition.EOF) != 0) {
						if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0) {
							break;
						}
					}
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					String answer = br.readLine();
					if (answer != null) {
						logging.info("message call: " + answer);
						if (answer.contains("$")) {
							break;
						}
					} else {
						break;
					}
				}
				//				BufferedReader brErr = new BufferedReader(new InputStreamReader(stderr));
				//				while (true) {
				//					String answer = brErr.readLine();
				//					if (answer != null) {
				//						System.out.println("execute: answer error = " + answer);
				//					} else {
				//						break;
				//					}
				//				}
				br.close();
				bw.close();
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//			sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
			// 返回0正常
			logging.info("Session exit " + sess.getExitStatus());
			try {
				stdout.close();
				stderr.close();
				stdin.close();
			} catch (IOException e) {
			}
			sess.close();
		}

	}

	public void disconnect() {
		if (conn != null)
			conn.close();
	}

	public void disSession() {
		sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
		// 返回0正常
		logging.info("Session exit " + sess.getExitStatus() + "关闭Session");
		sess.close();
	}

	public static void main(String[] args) {
		SSHClient s = new SSHClient();
		s.connect("192.168.109.128", "root", "feng");

		s.processHandles("cat /opt/sp.storage.summary.readBytesRate.txt");

	}

}
