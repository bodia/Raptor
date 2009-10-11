/**
 * New BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 * Copyright (c) 2009, RaptorProject (http://code.google.com/p/raptor-chess-interface/)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the RaptorProject nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package raptor.script;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jface.preference.PreferenceStore;

import raptor.Raptor;
import raptor.connector.Connector;
import raptor.swt.chess.ChessBoard;
import raptor.util.RaptorStringUtils;

public class GameScript implements Comparable<GameScript> {
	public static final String DESCRIPTION = "description";

	public static final String IS_AVAILABLE_IN_EXAMINE_STATE = "isAvailableInExamineState";

	public static final String IS_AVAILABLE_IN_FREEFORM_STATE = "isAvailableInFreeformState";
	public static final String IS_AVAILABLE_IN_OBSERVE_STATE = "isAvailableInObserveState";
	public static final String IS_AVAILABLE_IN_PLAYING_STATE = "isAvailableInPlayingState";
	public static final String IS_AVAILABLE_IN_SETUP_STATE = "isAvailableInSetupState";
	static final Log LOG = LogFactory.getLog(GameScript.class);
	public static final String SCRIPT = "script";
	public static final String SCRIPT_DIR = Raptor.USER_RAPTOR_DIR
			.getAbsolutePath()
			+ "/scripts/";

	public static GameScript[] getGameScripts(Connector connector) {
		String scriptDirectory = SCRIPT_DIR + connector.getShortName()
				+ "/game/";

		List<GameScript> result = new ArrayList<GameScript>(20);

		File[] files = new File(scriptDirectory).listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".properties");
			}
		});
		if (files != null) {
			for (File file : files) {
				String name = RaptorStringUtils.replaceAll(file.getName(),
						".properties", "");
				result.add(new GameScript(connector, name));
			}

			Collections.sort(result);

			return result.toArray(new GameScript[0]);
		} else {
			return new GameScript[0];
		}
	}

	protected Connector connector;
	protected String name;

	protected PreferenceStore store;

	public GameScript() {
		store = new PreferenceStore();
	}

	public GameScript(Connector connector, String name) {
		store = new PreferenceStore(SCRIPT_DIR + connector.getShortName()
				+ "/game/" + name + ".properties");
		try {
			store.load();
		} catch (IOException ioe) {
			LOG.error("Error occured loading game script " + SCRIPT_DIR
					+ connector.getShortName() + "/game/" + name
					+ ".properties", ioe);
			throw new RuntimeException(ioe);
		}
		this.name = name;
		this.connector = connector;
	}

	public int compareTo(GameScript o) {
		return getName().compareTo(o.getName());
	}

	public void delete() {
		new File(SCRIPT_DIR + connector.getShortName() + "/game/" + name
				+ ".properties").delete();
	}

	public boolean equals(GameScript script) {
		return connector == script.connector && name.equals(script.name);
	}

	public void execute(ChessBoard board) {

		String script = getScript();
		try {
			VelocityContext context = new VelocityContext();
			context.put("board", board);

			StringWriter writer = new StringWriter();
			Velocity.evaluate(context, writer, "evaluating game script "
					+ getName(), getScript());
			/* show the World */
			connector.sendMessage(writer.toString());
			writer.close();
		} catch (IOException ioe) {
			LOG.error("Error occured executing game script " + name + " "
					+ script, ioe);
			throw new RuntimeException(ioe);
		}
	}

	public Connector getConnector() {
		return connector;
	}

	public String getDescription() {
		return store.getString(DESCRIPTION);
	}

	public String getName() {
		return name;
	}

	public String getScript() {
		return store.getString(SCRIPT);
	}

	public boolean isAvailableInExamineState() {
		return store.getBoolean(IS_AVAILABLE_IN_EXAMINE_STATE);
	}

	public boolean isAvailableInFreeformState() {
		return store.getBoolean(IS_AVAILABLE_IN_FREEFORM_STATE);
	}

	public boolean isAvailableInObserveState() {
		return store.getBoolean(IS_AVAILABLE_IN_OBSERVE_STATE);
	}

	public boolean isAvailableInPlayingState() {
		return store.getBoolean(IS_AVAILABLE_IN_PLAYING_STATE);
	}

	public boolean isAvailableInSetupState() {
		return store.getBoolean(IS_AVAILABLE_IN_SETUP_STATE);
	}

	public void save() {
		store.setFilename(SCRIPT_DIR + connector.getShortName() + "/game/"
				+ name + ".properties");
		try {
			store.save();
		} catch (IOException ioe) {
			LOG.error("Error occured saving game script " + SCRIPT_DIR
					+ connector.getShortName() + "/game/" + name
					+ ".properties", ioe);
			throw new RuntimeException(ioe);
		}
	}

	public void setAvailableInExamineState(boolean isAvailableInExamineState) {
		store
				.setValue(IS_AVAILABLE_IN_EXAMINE_STATE,
						isAvailableInExamineState);
	}

	public void setAvailableInFreeformState(boolean isAvailableInFreeformState) {
		store.setValue(IS_AVAILABLE_IN_FREEFORM_STATE,
				isAvailableInFreeformState);
	}

	public void setAvailableInObserveState(boolean isAvailableInObserveState) {
		store
				.setValue(IS_AVAILABLE_IN_OBSERVE_STATE,
						isAvailableInObserveState);
	}

	public void setAvailableInPlayingState(boolean isAvailableInPlayingState) {
		store
				.setValue(IS_AVAILABLE_IN_PLAYING_STATE,
						isAvailableInPlayingState);
	}

	public void setAvailableInSetupState(boolean isAvailableInSetupState) {
		store.setValue(IS_AVAILABLE_IN_SETUP_STATE, isAvailableInSetupState);
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public void setDescription(String description) {
		store.setValue(DESCRIPTION, description);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScript(String script) {
		store.setValue(SCRIPT, script);
	}

}
