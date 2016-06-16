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

import java.util.List;

public class CmdMeta implements ICmdMeta {
	private String m_description;

   private List<IParameter> m_inputParameters;
	private List<IParameter> m_outputParameters;
	private Class<? extends ICommand> m_clazz;
	public CmdMeta(String description, List<IParameter> input,
			List<IParameter> output, Class<? extends ICommand> clazz) {
		m_description = description;
		m_inputParameters = input;
		m_outputParameters = output;
		m_clazz = clazz;
	}

	@Override
	public Class<? extends ICommand> getCommandClass() {
		return m_clazz;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public List<IParameter> getInputParameters() {
		return m_inputParameters;
	}

	@Override
	public List<IParameter> getOutputParameters() {
		return m_outputParameters;
	}

	@Override
   public String toString() {
      return "Command [m_description=" + m_description + ", m_inputParameters=" + m_inputParameters
            + ", m_outputParameters=" + m_outputParameters + ", m_clazz=" + m_clazz + "]";
   }
}
