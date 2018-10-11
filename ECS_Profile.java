import java.io.*;
import java.util.regex.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.util.*;
import java.net.*;

// A class to store the information about a particular profile on the ECS website/intranet.
public class ECS_Profile 
{
	// Method that attempts to retrieve the name associated to the user-input email ID from the ECS website.
	private static String retrieveName(String emailID, Scanner input)
	{		
		String address = "https://www.ecs.soton.ac.uk/people/";
		
		try
		{	
			// Open a connection to the page and attempt to read from it.
			URL profileAddress = new URL(address + emailID);
			URLConnection stream = profileAddress.openConnection();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream.getInputStream()));
			
			String line;
			String name = "";
			
			Pattern regex =  Pattern.compile("property=\"name\"|People</h1>");
			
			while ((line = reader.readLine()) != null)
			{	
				Matcher match = regex.matcher(line);
				
				// If the regex pattern is matched, then read the data from it.
				if (match.find())
				{
					// If "People</h1>" has been found, it means that the user does not exist on the website.
					// Therefore, prompt the user for a username and password, and search the intranet instead.
					if (line.contains("People</h1>"))
					{
						System.out.println("Failed to find user on the public ECS website.");
						System.out.println("Attempting to find profile on the ECS intranet.");
						
						LoginDetails login = new LoginDetails();
						
						Console console = System.console();
						
						System.out.println("Please enter your username: ");
						String username = input.nextLine();
						login.setUsername(username);
						
						System.out.println("Please enter your password: ");
						char[] password = console.readPassword();
						login.setPassword(new String(password));
						
						name = retrieveName(emailID, login);
					}
					// Otherwise, if a match has been found, then the name can be found and extracted.
					else
					{
						name = line.substring(line.indexOf("property=\"name\">") + 16);
						name = name.substring(0, name.indexOf("<"));
						line = null;
					}
				}
			}
			
			reader.close();
			
			return name;
		}
		
		// Catch any exceptions and output them to the console.
		catch (IOException ex)
		{
			System.err.println(ex.getCause() + ": " + ex.getMessage());
		}

		return "ERROR: Name could not be found.";
	}
	
	// Method that attempts to retrieve the name associated to the user-input email ID from the ECS intranet.
	// Note: This method uses the HtmlUnit library as opposed to a URLConnection due to the required authentication.
	private static String retrieveName(String emailID, LoginDetails login)
	{
		String address = "https://secure.ecs.soton.ac.uk/people/";
		
		try
		{	
			// Attempt to sign in using the user input values for the username and password.
			WebClient webClient = new WebClient();
			
			HtmlPage loginPage = (HtmlPage) webClient.getPage("https://secure.ecs.soton.ac.uk/login");
			HtmlForm loginForm = (HtmlForm) loginPage.getForms().get(1);
			loginForm.getInputByName("ecslogin_username").setValueAttribute(login.getUsername());
			loginForm.getInputByName("ecslogin_password").setValueAttribute(login.getPassword());
			loginForm.getInputByValue("Log in....").click();
			
			// Load the new page if the login attempt was successful.
			HtmlPage page = (HtmlPage) webClient.getPage(address + emailID);
			
			// If the page still has "ECS Intranet Login" in its title, then the login failed.
			if (page.getHead().getChildNodes().get(1).asText().contains("ECS Intranet Login"))
			{
				webClient.close();
				return "ERROR: Failed to login.";
			}
			
			// Otherwise the login was successful, and the name can be extracted and returned for output.
			String name = page.getElementById("name").getChildNodes().get(0).asText();
			
			webClient.close();
			
			if (name == "")
			{
				return "ERROR: Name could not be found.";
			}
			else
			{
				return name;
			}
		}
		
		// Catch any exceptions and output them to the console.
		catch (IOException ex)
		{
			System.err.println(ex.getCause() + ": " + ex.getMessage());
		}
		
		return "ERROR: Name could not be found.";
	}
	
	public static void main(String[] args) 
	{
		// Attempt to bypass the university firewall.
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", "152.78.128.51");
		System.getProperties().put("proxyPort", "3128");
		
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the email ID for the user you would like to search for: ");
		
		String id = input.nextLine();
		
		String name = retrieveName(id, input);
		
		input.close();
		
		System.out.println("*********************************************************************");
		System.out.println(name);
	}
}
