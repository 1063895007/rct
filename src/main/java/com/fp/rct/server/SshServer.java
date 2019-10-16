package com.fp.rct.server;

public interface SshServer {
	
	String getSsh(String ip,String user,String pass,String command);

}
