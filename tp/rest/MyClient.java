package tp.rest;

import tp.model.City;
import tp.model.CityManager;
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
import java.util.Map;

public class MyClient {
	private Service service;
	private JAXBContext jc;

	private static final QName qname = new QName("", "");
	private static final String url = "http://127.0.0.1:8084";

	public MyClient() {
		try {
			jc = JAXBContext.newInstance(CityManager.class, City.class,
					Position.class);
		} catch (JAXBException je) {
			System.out.println("Cannot create JAXBContext " + je);
		}
	}


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

    public void searchForCities(Position position, int radius) throws JAXBException {
/*
        service = Service.create(qname);
		service.addPort(qname, HTTPBinding.HTTP_BINDING, url);
		Dispatch<Source> dispatcher = service.createDispatch(qname,
				Source.class, Service.Mode.MESSAGE);
		Map<String, Object> requestContext = dispatcher.getRequestContext();
		requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
		Source result = dispatcher.invoke(new JAXBSource(jc, position));

        printSource(result);
        */
    }

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

	public static void main(String args[]) throws Exception {

		MyClient client = new MyClient();

		client.getCities();

		client.removeCities();

        client.getCities();

		client.addCity(new City("Rouen",      49.443889,  1.103333, "France"    ));
		client.addCity(new City("Mogadiscio",  2.333333,     48.85, "Somalie"   ));
		client.addCity(new City("Rouen",      49.443889,  1.103333, "France"    ));
		client.addCity(new City("Bihorel",    49.455278,  1.116944, "France"    ));
		client.addCity(new City("Londres",    51.504872,  -0.07857, "Angleterre"));
		client.addCity(new City("Paris",      48.856578,  2.351828, "France"    ));
		client.addCity(new City("Paris",           43.2, -80.38333, "Canada"    ));

        client.addCity(new City("Villers-Bocage", 49.083333,    -0.65, "France" ));
        client.addCity(new City("Villers-Bocage", 50.021858, 2.326126, "France" ));

        client.getCities();

        // client.deleteCity(new City("Londres",    51.504872,  -0.07857, "Angleterre"));
        // client.deleteCity(new City("Londres",    51.504872,  -0.07857, "Angleterre"));

        client.searchForCity(new Position(49.443889, 1.103333));
        client.searchForCity(new Position(49.083333,    -0.65));
        client.searchForCity(new Position(     43.2,   -80.38));

        client.searchForCities(new Position(48.85, 2.34), 10);
        client.searchForCities(new Position(   42,   64), 10);
        client.searchForCities(new Position(49.45, 1.11), 10);

        client.getCities("Mogadiscio");
        client.getCities("Paris");
        client.getCities("Hyrule");

        client.removeCities();

        client.getCities();


    }
}
