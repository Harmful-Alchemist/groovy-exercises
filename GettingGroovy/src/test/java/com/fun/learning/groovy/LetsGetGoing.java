package com.fun.learning.groovy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.apache.catalina.LifecycleException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LetsGetGoing {

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Hello friend and fellow developer, you are tasked with writing a document
	// management system in Groovy.
	// Documentation can be found here: http://docs.groovy-lang.org
	// We're going to be using Groovlets, maybe not the best choice, but them's the
	// requirements.
	// Learn more about Groovlets here:
	// http://docs.groovy-lang.org/latest/html/documentation/servlet-userguide.html
	// Pay special attention to the implicit variables, they will serve you well.
	//
	// The challenge is to modify src/test/resources/groovy/document.groovy until
	// all tests succeed.
	// The first test uses hello.groovy and you can use that as a pointer. the
	// herebehints subfolder contains spoilers, only look there if you're seriously
	// stuck.
	//
	// If you want to see you're creation in action run the
	// MainForBrowserAccesToTomcat class. You Groovlet will be accessible in your
	// browser.
	// Bonus points if you make it look beautiful!
	//
	// Now let's get groovy!
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	static TomcatRunnable tomcat;
	static int port = 5000;
	String startUrl = "http://localhost:" + port;

	@BeforeClass
	public static void getStarted() throws LifecycleException {
		tomcat = new TomcatRunnable();
		tomcat.setPort(port);
		new Thread(tomcat).start();
	}

	@AfterClass
	public static void tearDown() throws LifecycleException {
		tomcat.shutDown();
	}

	@Test
	public void firstSteps() throws ClientProtocolException, IOException, InterruptedException {
		String endpoint = "/groovy/hello.groovy";
		String response = connect(startUrl + endpoint);

		boolean gettingStarted = response.contains("Lets get started!");

		assertTrue(gettingStarted);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// The first requirement is very obvious, add a document! Exciting!
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void addADocument() {

		String docName = "MyFirstDocument";

		String endpoint = "/groovy/document.groovy";
		String arguments = "?newDoc=" + docName;
		String response = connect(startUrl + endpoint + arguments);

		boolean gotConfirmation = response.contains(docName);

		assertTrue(gotConfirmation);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Now we've added a document, it'd be nice if it's still there tomorrow ;)
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void persistence() {

		String docName = "MyFirstDocumentIWantToSave";
		String docName2 = "MySecondDocumentIWantToSave";

		String endpoint = "/groovy/document.groovy";
		String arguments = "?newDoc=" + docName;

		connect(startUrl + endpoint + arguments);

		arguments = "?newDoc=" + docName2;

		connect(startUrl + endpoint + arguments);
		
		arguments = "?alldocs=true";

		String response = connect(startUrl + endpoint + arguments);
		
		boolean gotConfirmation = response.contains(docName) && response.contains(docName2);

		assertTrue(gotConfirmation);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Hmm this document tho, we don't want to be there tomorrow.
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void fixUp() {
		String docName = "oops";

		String endpoint = "/groovy/document.groovy";
		String arguments = "?newDoc=" + docName;
		connect(startUrl + endpoint + arguments);

		arguments = "?clearall=true";

		String response = connect(startUrl + endpoint + arguments);

		String deleteRegex = "([Dd]eleted|[rR]emoved)[:\\s\"'/><=]* all documents"; // Edit this regex
		// if you want to send a confirmation in a way I
		// didn't consider. Basically I thought
		// of very basic xml, json or text.

		boolean deleteConfirmation = Pattern
				.compile(deleteRegex)
				.matcher(response)
				.find();
		
		arguments = "?alldocs=true";

		String responseAllDocs = connect(startUrl + endpoint + arguments);

		assertTrue(deleteConfirmation);
		
		assertFalse(responseAllDocs.contains(docName));
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Time to connect to the outside world.
	//
	// Maybe a nice walk outside.... Oh no! It means just another webservice (who
	// build this crappy webservice anyway, the boss's nephew?)
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void talkToTheWebService() {
		String docName = "archiveWorthyDocument";

		String endpoint = "/groovy/document.groovy";
		// Archive a document by sending a message to
		// startUrl+/herebehints/archive.groovy+?add=documentName
		String arguments = "?newDoc=" + docName + "&archive=true";
		connect(startUrl + endpoint + arguments);

		String archiveEndPoint = "/herebehints/archive.groovy";
		String archiveArguments = "?read=true";

		String response = connect(startUrl + archiveEndPoint + archiveArguments);
		archiveArguments = "?clear=true";
		connect(startUrl + archiveEndPoint + archiveArguments);

		assertTrue(response.contains(docName));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Just the document name, is not that much information... Let's add some more.
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void metadata() {
		String docName = "DocumentWithMetadata";

		String endpoint = "/groovy/document.groovy";
		String arguments = "?newDoc=" + docName + "&metadata=myFirstKey[MyFirstValue]mySecondKey[mySecondValue]";
		connect(startUrl + endpoint + arguments);

		arguments = "?readMeta=" + docName;

		String response = connect(startUrl + endpoint + arguments);

		String metaDataRegex = "myFirstKey[:\\s\"'/><=]*MyFirstValue[.\\s]*mySecondKey[:\\\\s\\\"'/><=]*MySecondValue"; // Again
																															// modify
																															// this
																															// based
																															// on
																															// your
																															// way
																															// of
																															// presentation

		boolean gotMetadata = Pattern.compile(metaDataRegex).matcher(response).find();

		assertTrue(gotMetadata);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Oh yeah, the actual document needs to be there too.
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void attachment() throws IOException {
		File file = new File("src/test/resources/example.txt");
		String content = "Hello!\n\rI am an example file.";
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(content);
		}

		String docName = "DocumentWithContent";

		String endpoint = "/groovy/document.groovy";
		String base64 = encode(file);

		String arguments = "?newDoc=" + docName + "&file=" + base64;
		connect(startUrl + endpoint + arguments);

		arguments = "?getFileContent=" + docName;

		String response = connect(startUrl + endpoint + arguments);

		assertTrue(response.contains(content));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// The End!
	// For now...... Next time: post requests! authentication! sql! analytics! more!
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public String connect(String url) {
		try {
			String response = Request
					.Get(url)
					.execute()
					.handleResponse(
							resp -> readResponse(resp));
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String readResponse(HttpResponse resp) {
		String response = "";
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(resp.getEntity().getContent(), Charset.defaultCharset()))) {
			String line;
			while ((line = br.readLine()) != null) {
				response = response + "\n" + line;
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private String encode(File file) throws IOException {
		String base64 = "";

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			int fileLength = (int) file.length();
			byte[] bytes = new byte[fileLength];
			bis.read(bytes, 0, fileLength);
			base64 = java.util.Base64.getEncoder().encodeToString(bytes);

		}
		return base64;
	}
}
