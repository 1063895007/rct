package com.fp.rct.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fp.rct.server.SshServer;
import com.fp.rct.util.SSHClient;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class SshServerImpl implements SshServer {
	@Autowired
	private SSHClient sSHClient;
	@Override
	public String getSsh(String ip, String user, String pass,String command) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			boolean bool = sSHClient.connect(ip, user, pass);
			if(bool) {
				log.info("SSH登录成功");
				result = sSHClient.processHandles(command);
			}else {
				log.info("SSH登录失败");
				return "SSH登录失败";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			log.info("关闭SSH链接");
			sSHClient.disconnect();
		}
		return result;
	}

}
