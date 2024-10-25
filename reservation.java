package online_reservation_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.Scanner;

class Login {
	private String username;
	private String password;
	
	Scanner sc = new Scanner(System.in);
	
	public String getUser_name() {
		System.out.println("Enter your username: ");
		username = sc.nextLine();
		return username;
	}
	public String getPassword() {
		System.out.println("Enter your Password: ");
		password  = sc.nextLine();
		return password;
	}
}

class Reservation_info{
	private int pnr_Number;
	private String passengerName;
	private int train_No;
	private String classType;
	private String journeyDate;
	private String fromPlace;
	private String toPlace;
	
	Scanner sc = new Scanner(System.in);
	
	public int getPnr_Number() {
		Random rand = new Random();
		pnr_Number = rand.nextInt(9999) + 1000;
		return pnr_Number;
	}
	public String getPassenger_Name() {
		System.out.print("Enter your name: ");
		passengerName = sc.nextLine();
		return passengerName;
	}
	public int getTrain_No() {
		System.out.print("Enter train number: ");
		train_No = sc.nextInt();
		return train_No;
	}
	
	public String getClassType() {
		System.out.print("***Don't use space!!***\nEnter your class type: ");
		classType = sc.next();
		return classType;
	}
	
	public String getJourneyDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		System.out.print("Enter your Journey date in yyyy-MM-dd format: ");
		journeyDate = sc.next();
		 try {
	            LocalDate date = LocalDate.parse(journeyDate, formatter);
	            LocalDate today = LocalDate.now();

	            if (!date.isBefore(today)) {
	            	return journeyDate;
	            } 
	            else {
	            	System.out.println("\nThe date is in the past. Please enter a present or future date.");
	            }
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
	        }
		 return "";
	}
	public String getFrom() {
		System.out.print("***Don't use space!!***\nEnter your on-boarding station: ");
		fromPlace = sc.next();
		return fromPlace;
	}
	public String getTo() {
		System.out.print("***Don't use space!!***\nEnter your destination station: ");
		toPlace = sc.next();
		return toPlace;
	}
	
}


public class reservation {
	static boolean isLoggedIn= false;
	
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		String uname = "root";
		String pass= "12345";
		String url = "jdbc:mysql://localhost:3306/reservation";
		
		try {
			Connection con = DriverManager.getConnection(url,uname,pass);
			
			while(true){
				System.out.println("1.Register");
				System.out.println("2.Login");
				int ch = sc.nextInt();
				String registerQuery = "insert into login values(?,?)";
				String loginQuery = "Select * from login where username = ? and password = ?";
				String insertQuery = "insert into reserve values(?,?,?,?,?,?,?)";
				String deleteQuery = "Delete from reserve where pnr_number = ?";
				String showDataQuery = "Select * from reserve where pnr_number = ?";
				String showTrains = "Select * from train";
				String TrainNameQuery = "Select trainName from train where train_No = ?";
				Login lg = new Login();	
				
				switch(ch){
				    
					case 1: 
					String username = lg.getUser_name();
					String password = lg.getPassword();
					PreparedStatement ps = con.prepareStatement(registerQuery);
					ps.setString(1,username);
					ps.setString(2,password);
					
					int rowsAffected = ps.executeUpdate();
					if(rowsAffected > 0) {
						System.out.println("Registered Successfully!");
					}
					else {
						System.out.println("Registeration failed!");
					}
					break;
					
					case 2:
					String usrname = lg.getUser_name();
					String pswrd = lg.getPassword();
					ps = con.prepareStatement(loginQuery);
					ps.setString(1,usrname);
					ps.setString(2, pswrd);
					
					ResultSet rs = ps.executeQuery();	
					if(rs.next()) {
						isLoggedIn = true;
						System.out.println("Login Successfull!");
					}
					else {
						System.out.println("Login Failed!");
					}
					break;
					
					default:
						System.exit(0);
				}
				if(isLoggedIn) {
					
					while(true) {
						System.out.println("\n1.Book Train Reservation");
						System.out.println("2.Cancel Ticket");
						System.out.println("3.Show All Trains");
						System.out.println("Press any key to logout");
						
						int c = sc.nextInt();
						
						switch(c) {
						case 1:
							Reservation_info r = new Reservation_info();
							int pnr_number = r.getPnr_Number();
							String passengerName =  r.getPassenger_Name();
							int train_No = r.getTrain_No();
							String classType = r.getClassType();
							String journeyDate = r.getJourneyDate();
							if(journeyDate == "") break;
							String fromPlace = r.getFrom();
							String toPlace = r.getTo();
							
							PreparedStatement ps = con.prepareStatement(insertQuery);
							ps.setInt(1, pnr_number);
							ps.setString(2,passengerName );
							ps.setInt(3, train_No);
							ps.setString(4, classType);
							ps.setString(5, journeyDate);
							ps.setString(6, fromPlace);
							ps.setString(7, toPlace);
							
							int rowsAffected = ps.executeUpdate();
							if(rowsAffected > 0) {
								ps = con.prepareStatement(TrainNameQuery);
								ps.setInt(1, train_No);
								ResultSet rs = ps.executeQuery();
								if(rs.next()) {
									String train_name =  rs.getString("trainName");
									System.out.println("Reservation Successfull!");
									System.out.println("\nYour PNR Number: "+ pnr_number);
									System.out.println("Train Name: "+ train_name);
								}
							}
							else {
								System.out.println("Reservation failed!");
							}
							break;
							
							case 2: 
								System.out.println("Enter your PNR number to cancel the ticket");
								int new_pnr = sc.nextInt();
								ps = con.prepareStatement(showDataQuery);
								ps.setInt(1, new_pnr);
								ResultSet rs = ps.executeQuery();
								if(rs.next()) {
									String passenger_Name =  rs.getString("passengerName");
									int trainNo =  rs.getInt("train_No");
									String class_Type = rs.getString("classType");
									String journey_Date = rs.getString("journeyDate");
									String from_Place = rs.getString("fromPlace");
									String to_Place = rs.getString("toPlace");
									
									ps = con.prepareStatement(TrainNameQuery);
									ps.setInt(1, trainNo);
									rs = ps.executeQuery();
									String train_name="" ;
									if(rs.next()) {
										train_name =  rs.getString("trainName");
									}
									
									System.out.println("\nPNR Number: "+ new_pnr);
									System.out.println("Passenger Name: "+ passenger_Name);
									System.out.println("Train Number: "+ trainNo);
									System.out.println("Train Name: "+ train_name);
									System.out.println("Class Type: "+ class_Type);
									System.out.println("Journey Date: "+ journey_Date);
									System.out.print( from_Place + "  --->  ");
									System.out.println(to_Place+"\n");
									
								}
								else {
									System.out.println("***No Records Found!***");
									break;
								}
								System.out.print("\n***Use Capital letters Only!***\nTo cancel the reservation press(Y/N): ");
								String choice = sc.next();
								switch(choice) {
								case "Y":
									ps = con.prepareStatement(deleteQuery);
									ps.setInt(1, new_pnr);
									int rows_affected = ps.executeUpdate();
									if(rows_affected > 0) {
										System.out.println("***Reservation Cancelled Successfully!***");
									}
									else {
										System.out.println("***Reservation Cancellation failed!***");
									}
									break;
									
								case "N":
									System.out.println("\n***Back to Menu***");
									break;
									
								default:
									break;
								}
							break;
								
							case 3:
								ps = con.prepareStatement(showTrains);
							    rs = ps.executeQuery();
							    System.out.println("Train No.  |  Train Name\n");
							    
							    while(rs.next()) {
							    	int trainNo = rs.getInt("train_No");
							    	String trainName = rs.getString("trainName");
							    	
							    	System.out.println( "  " +trainNo + "    |  "+ trainName);
							    }
							    break;
								
							default:
								System.exit(0);
						}
					}
					
					
					
				
					
				}
			}
		}
		catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("\nNo Such Train exists with such Train number\n "+"Session Faiiled! Login Again");
		}
		catch(Exception e){
			System.out.print(e.getMessage());
		}
		
		}

	
}
