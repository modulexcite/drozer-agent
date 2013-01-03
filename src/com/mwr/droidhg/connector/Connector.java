package com.mwr.droidhg.connector;

import java.util.Collection;

import com.mwr.droidhg.api.ConnectorParameters;
import com.mwr.droidhg.api.Protobuf.Message;

public abstract class Connector extends Thread {

	public volatile boolean running = false;
	protected volatile Connection connection = null;
	protected ConnectorParameters parameters = null;
	private SessionCollection sessions = null;
	
	public Connector(ConnectorParameters parameters) {
		this.parameters = parameters;
		this.sessions = new SessionCollection(this);
	}
	
	public abstract void setStatus(ConnectorParameters.Status status);
	
	public boolean checkForLiveness() { return true; }
	public boolean dieWithLastSession() { return false; }
	public boolean mustBind() { return true; }
	
	protected void createConnection(Transport transport) {
		if(transport.isLive()) {
			this.connection = new Connection(this, transport);
			this.connection.start();
		}
	}
	
	public Session getSession(String session_id) {
		return this.sessions.get(session_id);
	}
	
	public Collection<Session> getSessions() {
		return this.sessions.all();
	}
	
	public boolean hasSessions() {
		return this.sessions.any();
	}
	
	public void lastSessionStopped() {
		if(this.dieWithLastSession())
			this.stopConnection();
	}
	
	public void send(Message message) {
		this.connection.send(message);
	}
	
	public Session startSession() {
		return this.sessions.create();
	}
	
	public void resetConnection() {
		this.connection = null;
	}
	
	protected void stopConnection() {
		if(this.connection != null)
			this.connection.stopConnection();
	}
	
	public void stopConnector() {
		this.running = false;
		
		this.stopConnection();
	}
	
	public Session stopSession(String session_id) {
		return this.sessions.stop(session_id);
	}
	
	public void stopSessions() {
		this.sessions.stopAll();
	}

}
