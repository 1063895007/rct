package com.fp.rct.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fp.rct.server.impl.SshServerImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(tags = { "SSH" }, description = "执行工具")
@RestController
@Slf4j
@RequestMapping("/ssh")
public class SshApi {
	@Autowired
	private SshServerImpl sshServerImpl;
	
	@ApiOperation(value = "SSH执行", httpMethod = "GET", notes = "SSH")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ip", value = "ip", paramType = "query", required = true, dataType = "String"),
			@ApiImplicitParam(name = "user", value = "用户名", paramType = "query", required = true, dataType = "String"),
			@ApiImplicitParam(name = "pass", value = "密码", paramType = "query", required = true, dataType = "String"),
			@ApiImplicitParam(name = "command", value = "指令", paramType = "query", required = true, dataType = "String") })
	@GetMapping(value = "/tool")
	public String getSshApi(@ApiParam(hidden = true) @RequestParam Map<String, Object> map) {
		String ip = String.valueOf(map.get("ip"));
		log.info("ip:"+ip);
		String user = String.valueOf(map.get("user"));
		log.info("用户名:"+user);
		String pass = String.valueOf(map.get("pass"));
		String command = String.valueOf(map.get("command"));
		log.info("指令:"+command);
		
		String result = sshServerImpl.getSsh(ip, user, pass, command);
		return result;
	}
	@GetMapping(value = "/tooltest")
	public String getSshApi2() {
		log.info("tooltest");
		return "aaaaccbbdd";
	}
}
