package tp.rest;

import tp.model.City;
import tp.model.CityManager;

import javax.xml.ws.Endpoint;

public class MyServiceTP {
	
	/**
	 * Gere les villes
	 */
	private CityManager cityManager;

	public MyServiceTP(){

		cityManager = new CityManager();

        cityManager.addCity(new City("Rouen",49.437994,1.132965,"FR"));
        cityManager.addCity(new City("Neuor",12,42,"RF"));

		Endpoint.publish("http://127.0.0.1:8084/", cityManager);
	}

	/**
	 *
	 * @param args
     */
	public static void main(String args[]) throws InterruptedException {
		new MyServiceTP();
		Thread.sleep(15 * 60 * 1000);
		System.exit(0);
	 }
}
