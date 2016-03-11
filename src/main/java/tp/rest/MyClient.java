package tp.rest;

import tp.model.City;
import tp.model.CityManager;
import tp.model.CityManagerService;
import tp.model.Position;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MyClient {

	private static final QName SERVICE_NAME = new QName("http://model.tp/", "CityManagerService");
	private static final QName PORT_NAME = new QName("http://model.tp/", "CityManagerPort");

	private Service service;
	private JAXBContext jc;

	private static final QName qname = new QName("", "");
	private static final String url = "http://127.0.0.1:8084";

	/**
	 *  Constructeur charge d'initialiser le parseur XML/Objet Java avec
	 * Les classes CityManager, City, Position
	 */
	public MyClient() {
		try {
			jc = JAXBContext.newInstance(CityManager.class, City.class,
					Position.class);
		} catch (JAXBException je) {
			System.out.println("Cannot create JAXBContext " + je);
		}
	}

	/**
	 * Methode permettant de faire le lien avec le serveur et permettant d'executer
	 * une requete de type GET retournant l'ensemble des City enregistrees.
	 * @throws JAXBException
     */
    public void getCities() throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);

        Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);

        Map<String, Object> requestContext = dispatcher.getRequestContext();


		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "GET");
        requestContext.put(MessageContext.PATH_INFO, "/all");

		Source result = dispatcher.invoke(new JAXBSource(jc, new CityManager()));

        printSource(result);
    }

	/**
	 * Methode permettant de faire le lien avec le serveur et permettant d'executer
	 * une requete de type GET retournant l'ensemble des City enregistrees qui ont le nom specifie.
	 * @param name Nom de la ville a rechercher
	 * @throws JAXBException
     */
    public void getCities(String name) throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);

        Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);
		Map<String, Object> requestContext = dispatcher.getRequestContext();

		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "GET");
        requestContext.put(MessageContext.PATH_INFO, "/" + name);

		Source result = dispatcher.invoke(new JAXBSource(jc, new CityManager()));

        printSource(result);
    }

	/**
	 * Methode effectuant une requete de type DELETE permettant de supprimer l'ensemble
	 * des City enregistrees.
	 * @throws JAXBException
     */
    public void removeCities() throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);
		Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);
		Map<String, Object> requestContext = dispatcher.getRequestContext();

		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "DELETE");
        requestContext.put(MessageContext.PATH_INFO, "/all");

        Source result = dispatcher.invoke(new JAXBSource(jc, new CityManager()));
        printSource(result);
    }

	/**
	 * Methode effectuant une requete de type PUT permettant d'ajouter une City.
	 * @param city City a ajouter.
	 * @throws JAXBException
     */
    public void addCity(City city) throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);
		Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);

        Map<String, Object> requestContext = dispatcher.getRequestContext();

        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "PUT");

		Source result = dispatcher.invoke(new JAXBSource(jc, city));

        printSource(result);
    }

	/**
	 * Methode effectuant une requete de type DELETE permettant de supprimer la City specifiee.
	 *
	 * WARNING: Cette methode ne fonctionne pas; il semble que l'objet Source n'est jamais
	 *          reçu du côte du serveur. Une des solutions serait de passer tous les parametres
	 *          necessaires a la suppression dans l'URL et les recuperer du côte du serveur.
	 *
	 * @param city City a supprimer
	 * @throws JAXBException
     */
    public void deleteCity(City city) throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);

		Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);

        Map<String, Object> requestContext = dispatcher.getRequestContext();

		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "DELETE");
        requestContext.put(MessageContext.PATH_INFO, "/city");

        Source s = new JAXBSource(jc, city);

        Source result = dispatcher.invoke(s);
        printSource(result);
    }

	/**
	 * Methode effectuant une requete de type POST permettant de retourner l'ensemble de
	 * City autour d'une position donnee.
	 * @param position Position autour de laquelle chercher
	 * @param radius rayon de recherche
	 * @throws JAXBException
     */
    public void searchForCities(Position position, int radius) throws JAXBException {

        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);
		Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);
		Map<String, Object> requestContext = dispatcher.getRequestContext();
		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
		requestContext.put(MessageContext.PATH_INFO, "/" + radius);
		Source result = dispatcher.invoke(new JAXBSource(jc, position));

        printSource(result);
    }

	/**
	 * Methode effectuant une requete de type POST permettant de retourner la City
	 * a une Position donnee.
	 * @param position Position de la ville a recuperer.
	 * @throws JAXBException
     */
	public void searchForCity(Position position) throws JAXBException {
		service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);

        Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);

        Map<String, Object> requestContext = dispatcher.getRequestContext();

		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");

        Source result = dispatcher.invoke(new JAXBSource(jc, position));
		printSource(result);
	}

	/**
	 * Affiche le flux xml retourne.
	 * @param s
     */
	public void printSource(Source s) {
		try {
			System.out.println("============================= Response Received =========================================");
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(s, new StreamResult(System.out));
			System.out.println("\n======================================================================");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Methode principale permettant de tester que les requetes via le service
	 * se passent correctement.
	 * @param args
	 * @throws Exception
     */
	public static void main(String args[]) throws MalformedURLException {

		URL wsdlURL = new URL("http://127.0.0.1:8084/citymanager?wsdl");
		Service service = Service.create(wsdlURL, SERVICE_NAME);

		CityManagerService cityManager = service.getPort(PORT_NAME, CityManagerService.class);
		System.out.println(cityManager.getCities());

		// == AJOUT

		City c = new City("Zanarkand", 16, 64, "Hyrule");
		cityManager.addCity(c);
		cityManager.removeCity(c);

		System.out.println(cityManager.getCities());

    }
}
