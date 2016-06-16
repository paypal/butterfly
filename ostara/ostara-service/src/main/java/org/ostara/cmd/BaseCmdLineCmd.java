/*******************************************************************************
 * Copyright (c) 2014 eBay Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.ostara.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.ostara.cmd.util.ProcessCall;
import org.ostara.cmd.util.ProcessCallOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCmdLineCmd extends BaseCommand {
	private static Logger logger = LoggerFactory
			.getLogger(BaseCmdLineCmd.class);

	public BaseCmdLineCmd(String name) {
		super(name);
	}

	@Override
	public ICmdResult execute() {
		ICmdResult result = new CmdResult();
		try {
			List<String> cmds = getCmdStrs();
			String cmdExecDir = getCmdDir();

			File file = new File(cmdExecDir);
			if (!file.exists()) {
				file.mkdirs();
			}

			executeCommand(result, cmds, cmdExecDir, isWait());
		} catch (Exception e) {
			result.setException(e);
		}
		return result;
	}

	protected void executeCommand(ICmdResult result, List<String> cmd,
			String cmdExecDir, boolean wait) {
		Process p = null;
		ProcessCall processCall = null;
		ProcessCallOutput output = null;

		List<String> cmdLine = new ArrayList<String>();

		// For windows system
		if (SystemUtils.IS_OS_WINDOWS) {
			cmdLine.add("cmd.exe");
			cmdLine.add("/C");
		}

		cmdLine.addAll(cmd);

		int retry = 0;
		Exception error = null;
		while (retry < 3) {
			try {

				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.redirectErrorStream(true);
				processBuilder.directory(new File(cmdExecDir));

				StringBuilder cmdToLog = new StringBuilder();

				for (String s : cmdLine) {
					s = s.replaceFirst("https://.*[@]", "https://");
					cmdToLog.append(s).append(" ");
				}

				logger.info("Executing process: " + cmdToLog);

				p = processBuilder.command(cmdLine).start();
				if (wait) {
					processCall = new ProcessCall(p);
					output = processCall.call();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				error = e;
				retry++;
				continue;
			}
			break;
		}

		if (output == null) {
			result.setException(error);
		} else if (output.exitValue != 0) {
			result.setException(new RuntimeException(
					"Failed to execute command(%" + getClass().getName()
							+ "%), rtn value:" + output.exitValue));
		} else {
			result.setMessage(output.output);
		}
	}

	protected abstract String getCmdDir();

	protected abstract List<String> getCmdStrs();

	protected boolean isWait() {
		return true;
	}
}
