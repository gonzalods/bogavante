package org.gms.bogavante.startup;

import org.gms.bogavante.connector.http.HttpConnector;

public class Bootstrap {

	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		connector.start();

	}

}
