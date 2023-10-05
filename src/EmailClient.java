// 200516P

import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.imageio.plugins.tiff.ExifGPSTagSet;
import javax.mail.search.ReceivedDateTerm;
import javax.swing.text.DateFormatter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.*;

public class EmailClient {
	// initializing global variables
	public static ArrayList<Recipient> recipient_objects = new ArrayList<Recipient>();
	static ArrayList<EmailObject> all_sent_mails = new ArrayList<EmailObject>();
	static int recipient_count = 0;
	static RecipientFactory recipient_factory = new RecipientFactory();
	
	// create Recipient objects using RecipientFactory and update recipient_objects
	static void createRecipientObjects(String str) {
		recipient_count ++;
		Recipient recipient = recipient_factory.getRecipientObject(str);
		recipient_objects.add(recipient);
	}
	
	// Read all the records in ClientList to store in recipient_objects
	static void clientListRead() {
		try {
    		FileReader file = new FileReader("D:/Eclipse Workspace/Email Client/src/clientlist.txt");
    		BufferedReader file_ = new BufferedReader(file);
    		String line = null;	
    		while ((line = file_.readLine())!=null) { 
    			createRecipientObjects(line);
    		}
    		file_.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	// Write new records to the ClientList and create new Recipient Objects
	static void clientListWrite(String record) {
		try {
			FileWriter client_list = new FileWriter("D:/Eclipse Workspace/Email Client/src/clientlist.txt",true);////
			client_list.write(record.strip()+"\n");
			createRecipientObjects(record);
			client_list.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  
	// Find and return the list of birthday recipients
	static ArrayList<Recipient> find_birthday_recipients(String date) {
		ArrayList<Recipient> birthday_recipient_list = new ArrayList<Recipient>();
		for(int i=0; i<recipient_count; i++) {
			Recipient recipient = recipient_objects.get(i);
			if(!recipient.getClass().isAssignableFrom(Official.class)) {
				if(recipient.getClass().isAssignableFrom(OfficialFriend.class)) {
					OfficialFriend recepient = (OfficialFriend) recipient;
					if(recepient.birthday.substring(5).equalsIgnoreCase(date)) 
						birthday_recipient_list.add(recipient);}
				
				else {
					PersonalFriend recepient = (PersonalFriend) recipient;
					if(recepient.birthday.substring(5).equalsIgnoreCase(date)) 
						birthday_recipient_list.add(recipient);
				}
			}
		}
		return birthday_recipient_list;
	}

	// Checking whether birthday wishes are already sent on the current date
	static boolean birthdayWishesSent(String date) {
		boolean has_sent = false;
		String first_line = null;
		
		FileReader file;
		try {
			file = new FileReader("D:/Eclipse Workspace/Email Client/src/wishSentDate.txt");
			BufferedReader file_ = new BufferedReader(file);
			first_line = file_.readLine();
			if((first_line != null) && (first_line.equals(date)))
				has_sent = true;
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return has_sent;
	}
	
	// Update the file of wishSendDate after sending birthday messages
	static void updateWishSentDate(String date) {
		try {
			FileWriter file = new FileWriter("D:/Eclipse Workspace/Email Client/src/wishSentDate.txt");
			file.write(date);
			file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	// Store a serialized list of all_sent_mails list in serializedEmailObjects.ser file
	static void writeSerializedEmailObjects(ArrayList<EmailObject> emails) {
		try {
			FileOutputStream file_stream = new FileOutputStream("D:/Eclipse Workspace/Email Client/src/serializedEmailObjects.ser");
			ObjectOutputStream object_stream = new ObjectOutputStream(file_stream);
			object_stream.writeObject(emails);
			object_stream.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// deserialize the serialized list object in erializedEmailObjects.ser and assign it to all_sent_mails
	static  void deserializeEmailObjects() {
		try {
			FileInputStream file_stream = new FileInputStream("D:/Eclipse Workspace/Email Client/src/serializedEmailObjects.ser");
			ObjectInputStream object_stream = new ObjectInputStream(file_stream);
			ArrayList<EmailObject> deserialized_object = (ArrayList<EmailObject>) object_stream.readObject();
			object_stream.close();
			
			if(deserialized_object == null) {
				all_sent_mails = new ArrayList<EmailObject>();
			}
			all_sent_mails = deserialized_object;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
	}
	
	// send birthday wishes to the birthday recipients and update all_sent_mails
	static void sendAndStoreBirthdayWishes(ArrayList<Recipient> recipients, String date) {
		String message;
		Recipient recipient;
		
		deserializeEmailObjects();
		
		for(int i=0; i<recipients.size(); i++) {
			recipient = recipients.get(i);
			if(recipient.getClass().isAssignableFrom(PersonalFriend.class))
				message = "Sending you so much love for your birthday.\n" + "Mudditha";
			else 
				message = "Wish you a happy birthday.\n" + "Mudditha";
		
			EmailObject email = new EmailObject(recipient.email, "Birthday Wish", message, date);
	    	email.mailSend();
	    	all_sent_mails.add(email);
		}
		
		writeSerializedEmailObjects(all_sent_mails);
	}

	// print details of all the mails send on a specific date
	static void printEmailDetails(String date) {
		int count = 0;
		String last = "";
		
		deserializeEmailObjects();
		
		int size = all_sent_mails.size();
		
		for(int i=0; i < size; i++) {
			EmailObject email = all_sent_mails.get(i);
			if(email.date.equalsIgnoreCase(date)) {
				count++;
				System.out.println("Recipient Email: " + email.recipient_email + ",  Subject: " + email.subject);
				last = email.date;
			}
			else if (!last.equalsIgnoreCase(email.date)) {
				break;
			}
		}
		
		if(count == 0)
			System.out.println("No emails sent on specific day");
	}
	
    public static void main(String[] args) {
     	Scanner scanner = new Scanner(System.in);
        String exit = "1";
        
        // read data in ClientList file
        clientListRead();
        
        // get today date
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime today = LocalDateTime.now();
        String date = dateformat.format(today).toString();
        
        // send birthday wishes if not sent earlier
        if(!birthdayWishesSent(date)) {
        	updateWishSentDate(date);
        	ArrayList<Recipient> birthday_recipients =  find_birthday_recipients(date.substring(5));
            sendAndStoreBirthdayWishes(birthday_recipients, date);
        }

        // run loop until user want to quit
        while(exit != "-1") {
	    	System.out.println("Enter option type: \n"
	                  + "1 - Adding a new recipient\n"
	                  + "2 - Sending an email\n"
	                  + "3 - Printing out all the recipients who have birthdays on a given date\n"
	                  + "4 - Printing out details of all the emails sent on a given date\n"
	                  + "5 - Printing out the number of recipient objects in the application");
	    	
	        int option = scanner.nextInt();
	        scanner.nextLine();
	
	        switch(option){
	        	// Adding new recipients
	            case 1:
	                System.out.println("Enter recepient details in following format\n"
	                	  		+ "if official recepient:-	Official: <name>,<email>,<designation>\n"
	                	  		+ "if office friend:- Officie_friend: <name>,<email>,<designation>,<birthday>\n"
	                	  		+ "if personal friend:- Personal: <name>,<nick name>,<email>,<birthday>");
	                
	                
	                //Get input
			        String record = scanner.nextLine();
			                
			        //Updates ClientList file and program
			        clientListWrite(record);
				
	                //If the new recipient have birthday on same day, send birthday wish
			        if(!recipient_objects.get(recipient_count-1).getClass().isAssignableFrom(Official.class)) {
			        	 ArrayList<Recipient> new_arr = new ArrayList<Recipient>();
			        	 new_arr.add(recipient_objects.get(recipient_count-1));
			        	 sendAndStoreBirthdayWishes(new_arr, date);
			        }
			        
			        /*
	                if(!recipient_objects.get(recipient_count-1).getClass().isAssignableFrom(Official.class)) {
	                	if(recipient_objects.get(recipient_count-1).getClass().isAssignableFrom(OfficialFriend.class)) {
	                		OfficialFriend rece = (OfficialFriend) recipient_objects.get(recipient_count-1);
	                		if(rece.birthday.substring(5).equalsIgnoreCase(date.substring(5))) {
	                			ArrayList<Recipient> new_arr = new ArrayList<Recipient>();
		                		new_arr.add(recipient_objects.get(recipient_count-1));
		                		sendAndStoreBirthdayWishes(new_arr, date);
		                	}
	                	}
	                	else {
	                		PersonalFriend rece = (PersonalFriend) recipient_objects.get(recipient_count-1);
	                		if(rece.birthday.substring(5).equalsIgnoreCase(date.substring(5))) {
	                			ArrayList<Recipient> new_arr = new ArrayList<Recipient>();
		                		new_arr.add(recipient_objects.get(recipient_count-1));
		                		sendAndStoreBirthdayWishes(new_arr, date);
		                	}
	                	}   	
	                } */
			        
	                break; 
	                
	            // Sending new mails
	            case 2:
	            	System.out.println("Input details in this format:- email,subject,content");
	            	
	            	// Get input and split details
	            	String[] input = scanner.nextLine().strip().split(",", 3);
	            	String email_address = input[0].strip();
	            	String subject = input[1].strip();
	            	String content = input[2].strip();
	            
	            	// Send the mail
	            	EmailObject email = new EmailObject(email_address, subject, content, date);
	            	email.mailSend();
	            	
	            	// Update serializedEmailObject.ser file 
	            	deserializeEmailObjects();
	            	all_sent_mails.add(email);
	            	writeSerializedEmailObjects(all_sent_mails);
	            	
	                break;
	                
	            // To get the birthday recipient names on a specific date   
	            case 3:
	            	System.out.println("Enter day to find birthday recipients with birthday on that day.\n"
	            			+ "format: yyyy/MM/dd");
	            	
	            	// get input
	            	String birthday = scanner.nextLine().strip();
	            	
	            	//  get the list of persons having birthday on input date and print their names
	            	ArrayList<Recipient> birthday_recipient_list =  find_birthday_recipients(birthday.substring(5));
	            	int size = birthday_recipient_list.size();
	            	
	            	if(size!=0) {
		            	System.out.println("Persons having birthday on "+birthday+" are :");
		            	for(int i=0; i<size; i++) {
		            		System.out.println(birthday_recipient_list.get(i).name);
		            	}
	            	}
	            	else {
	            		System.out.println("No recipients have birthdays on "+birthday);
	            	}
	            	
	                break;
	                
	            // Get all email address and subjects sent on a specific date
	            case 4:
	            	System.out.println("Enter day to get all the emails details sent on that day.\n"
	            			+ "format: yyyy/MM/dd");
	            	
	            	// Get input
	            	String day = scanner.nextLine().strip();
	            	
	            	// Print details
	            	printEmailDetails(day);
	                
	                break;
	                
	            // Output recipient count
	            case 5:
	                System.out.println("No. of recepient objects = " + recipient_count);
	                
	                break;
	
	        }
	        
	        // input loop termination condition
	        System.out.println("\nPress -1 if you want to exit the program. Otherwise press any character key.");
	        exit = scanner.next();
	        scanner.nextLine();
        }

        scanner.close();
        }
}
