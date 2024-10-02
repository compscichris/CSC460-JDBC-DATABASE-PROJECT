/**
/**TODO: VERIFY COMPLETION OF FILE, TEST FOR ERRORS, FINISH DOCUMENTATION
 * Filename: Prog4.java
 * Authors: Chris Chen, ...
 * Course: CSC 460 Fall 2022
 * Assignment: Program #4
 * Instructor: Lester I. McCann, Ph.D.
 * TAs: Priya Kaushik, Aayush Pinto
 * LoadData.java is a program that creates the main tables of the database from the csv files.
 * It uses JAVA JDBC to connect to the SQL server
 */

import java.io.*;
import java.sql.*;                 // For access to the SQL interaction methods
import java.util.ArrayList;
import java.util.Scanner;

public class LoadData
{
	public static final String table_names[] = 
		{"passenger", "passenger_history", "flight", "staff", "flight_staff"}; 
	/**
	 * This main method handles all the code that is needed to create the connection to the oracle dbms.
	 * It creates the tables for us by reading the csv files table data.
	 * 
	 */
    public static void main (String [] args)
    {
        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        String file1name = "";
        String file2name = "";
        String file3name = "";
        String file4name = "";
        String file5name = "";
        String username = null,    // Oracle DBMS username
               password = null;    // Oracle DBMS password

        if (args.length == 7) {    // get username/password from cmd line args
            username = args[0];
            password = args[1];
            file1name = args[2];
            file2name = args[3];
            file3name = args[4];
            file4name = args[5];
            file5name = args[6];
        }
        else if (args.length == 5) {    //
        	username = "chrismaschen";
        	password = "a8889";
        	file1name = args[1];
            file2name = args[2];
            file3name = args[3];
            file4name = args[4];
            file5name = args[5];
        }
        else {
            System.out.println("\nUsage:  java JDBC <username> <password> "
            		+ "<file1name> <file1name> <file2name> <file3name> <file4name> <file5name>\n"
                             + "    where <username> is your Oracle DBMS"
                             + " username,\n    and <password> is your Oracle"
                             + " password (not your system password)."
                             + " file1name is csv for passenger, file2name is csv for passenger_history,"
                             + " file3name is csv for flight, file4name is csv for staff,"
                             + " file5name is csv for flight_staff,");
            System.exit(-1);
        }
            // load the (Oracle) JDBC driver by initializing its base
            // class, 'oracle.jdbc.OracleDriver'.
        try {
                Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {

                System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
                System.exit(-1);
        }
            // make and return a database connection to the user's
            // Oracle database
        //CONNECTING
        Connection dbconn = null;
        try {
                dbconn = DriverManager.getConnection
                         (oracleURL,username,password);
        } catch (SQLException e) {
                System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
        }
        
        Scanner input = new Scanner(System.in);
        String user_in = "";
        //ACCESS MENU
        while(user_in != "quit")
        {
        	System.out.println("Option DEBUG TABLES: Enter 'a'");
            System.out.println("Option CREATE TABLES: Enter 'b'");
            System.out.println("Option INSERT INTO TABLES: Enter 'c'");
            System.out.println("EXIT: Enter 'quit'");
        	user_in = input.nextLine();
        	//CHOSE DEBUG
        	if(user_in.equals("a"))
        	{
        		System.out.println("DELETING TABLES.");
        		deleteTables(dbconn);
        	}
        	//INITIALIZE TABLES
        	else if(user_in.equals("b"))
        	{
                // Send the query to the DBMS, and get and display the results
                try {
                	createPassenger(dbconn);
                    createPassengerHistory(dbconn);
                    createFlight(dbconn);
                    createStaff(dbconn);
                    createFlightStaff(dbconn);
                    System.out.println("Creation of tables successful!");
                    dbconn.close();
                } catch (SQLException e) {
                        System.err.println("*** SQLException:  "
                            + "TABLE CREATION UNSUCCESSFUL.");
                        System.err.println("\tMessage:   " + e.getMessage());
                        System.err.println("\tSQLState:  " + e.getSQLState());
                        System.err.println("\tErrorCode: " + e.getErrorCode());
                        System.exit(-1);
                }
        	}
        	//CHOSE DEBUG
        	else if(user_in.equals("c"))
        	{
        		// Send the query to the DBMS, and get and display the results
                try {
                	insertPassenger(dbconn, file1name);
                    insertPassengerHistory(dbconn, file1name);
                    insertFlight(dbconn, file1name);
                    insertStaff(dbconn, file1name);
                    insertFlightStaff(dbconn, file1name);
                    System.out.println("Insertion of tables successful!");
                    dbconn.close();
                } catch (SQLException e) {
                        System.err.println("*** SQLException:  "
                            + "TABLE INSERTION UNSUCCESSFUL.");
                        System.err.println("\tMessage:   " + e.getMessage());
                        System.err.println("\tSQLState:  " + e.getSQLState());
                        System.err.println("\tErrorCode: " + e.getErrorCode());
                        System.exit(-1);
                }
        	}
        	else if(user_in.equals("quit"))
        	{
        		System.out.print("Goodbye!");
        	}
        	else
        	{
        		System.out.println("ENTER A VALID OPTION.");
        	}
        }
    }
    
    //********** SECTION 1: CREATE TABLES AND DEBUG **********//
    
    /**
     *  The createPassenger() method creates the table 'passenger' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void createPassenger(Connection dbconn)
    {
    	String table_command = "CREATE TABLE passenger (\r\n"
    			+ "	pid			integer,\r\n"
    			+ "	pname		varchar(60) not null,\r\n"
    			+ "	is_student	boolean not null,\r\n"
    			+ "	is_freq		boolean not null,\r\n"
    			+ "	is_priority	boolean not null,\r\n"
    			+ "	primary key (pid)\r\n"
    			+ ");";
    	boolean works;
		try {
			Statement stmt = dbconn.createStatement();
			works = stmt.execute(table_command);
			if(!works)
			{
				System.out.println("Table Passenger created!");
			}
	        stmt.close();
		} 
		catch (SQLException e) {
			System.err.println("*** SQLException:  "
                    + "Problem creating Table passenger");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
			e.printStackTrace();
		}
    }
    /**
     *  The createPassengerHistory() method creates the table 'passenger_history' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void createPassengerHistory(Connection dbconn) {
    	String table_command = "CREATE TABLE passenger_history (\r\n"
    			+ " 	pid			integer,\r\n"
    			+ "	fid			integer,\r\n"
    			+ "	cbag		integer not null,\r\n"
    			+ "	food		boolean not null,\r\n"
    			+ "	\r\n"
    			+ "	CONSTRAINT pid_foreign\r\n"
    			+ "    FOREIGN KEY(pid) REFERENCES passenger(pid) \r\n"
    			+ "    ON DELETE  CASCADE,\r\n"
    			+ "    \r\n"
    			+ "    CONSTRAINT fid_foreign\r\n"
    			+ "    FOREIGN KEY(fid) REFERENCES flight(fid) \r\n"
    			+ "    ON DELETE  CASCADE,\r\n"
    			+ "	primary key (pid, fid)\r\n"
    			+ ");";
    	boolean works;
		try {
			Statement stmt = dbconn.createStatement();
			works = stmt.execute(table_command);
			if(!works)
			{
				System.out.println("Table passenger_history created!");
			}
	        stmt.close();
		} 
		catch (SQLException e) {
			System.err.println("*** SQLException:  "
                    + "Problem creating Table passenger_history");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
			e.printStackTrace();
		}
    }
    /**
     *  The createFlight() method creates the table 'flight' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void createFlight(Connection dbconn) {
    	String table_command = "CREATE TABLE flight (\r\n"
    			+ " 	fid			integer,\r\n"
    			+ "	fairline	varchar(15) not null,\r\n"
    			+ "	bgate		varchar(5),\r\n"
    			+ "	btime		time not null,\r\n"
    			+ "	dtime		time not null,\r\n"
    			+ "	duration	time not null,\r\n"
    			+ "	route		varchar(50) not null,\r\n"
    			+ "	fdate		date not null,\r\n"
    			+ "	is_depart	boolean not null,\r\n"
    			+ "	 \r\n"
    			+ "	primary key (fid)\r\n"
    			+ ");";
    	boolean works;
		try {
			Statement stmt = dbconn.createStatement();
			works = stmt.execute(table_command);
			if(!works)
			{
				System.out.println("Table flight created!");
			}
	        stmt.close();
		} 
		catch (SQLException e) {
			System.err.println("*** SQLException:  "
                    + "Problem creating Table flight");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
			e.printStackTrace();
		}
    }
    /**
     *  The createStaff() method creates the table 'staff' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void createStaff(Connection dbconn){
    	String table_command = "CREATE TABLE staff (\r\n"
    			+ "    sid			integer,\r\n"
    			+ "	sname		varchar(60) not null,\r\n"
    			+ "	srole		varchar(40) not null,\r\n"
    			+ "	sairline	varchar(15) not null,\r\n"
    			+ "	\r\n"
    			+ "	primary key (sid)\r\n"
    			+ ");";
    	boolean works;
		try {
			Statement stmt = dbconn.createStatement();
			works = stmt.execute(table_command);
			if(!works)
			{
				System.out.println("Table staff created!");
			}
	        stmt.close();
		} 
		catch (SQLException e) {
			System.err.println("*** SQLException:  "
                    + "Problem creating Table staff");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
			e.printStackTrace();
		}
    }
    /**
     *  The createFlightStaff() method creates the table 'flight_staff' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void createFlightStaff(Connection dbconn)
    {
    	String table_command = "CREATE TABLE flight_staff (\r\n"
    			+ "    sid			integer,\r\n"
    			+ "	fid			integer,\r\n"
    			+ "	\r\n"
    			+ "	CONSTRAINT sid_foreign\r\n"
    			+ "    FOREIGN KEY(sid) REFERENCES staff(sid) \r\n"
    			+ "    ON DELETE  CASCADE,\r\n"
    			+ "    \r\n"
    			+ "    CONSTRAINT fid_foreign\r\n"
    			+ "    FOREIGN KEY(fid) REFERENCES flight(fid) \r\n"
    			+ "    ON DELETE  CASCADE,\r\n"
    			+ "    \r\n"
    			+ "	primary key (sid, fid)\r\n"
    			+ ");";
    	boolean works;
		try {
			Statement stmt = dbconn.createStatement();
			works = stmt.execute(table_command);
			if(!works)
			{
				System.out.println("Table flight_staff created!");
			}
	        stmt.close();
		} 
		catch (SQLException e) {
			System.err.println("*** SQLException:  "
                    + "Problem creating Table flight_staff");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
			e.printStackTrace();
		}
    }
    /**
     * TODO:
     * The deleteTables() method deletes all existing tables for Program 4, created in this file.
     * @param dbconn
     */
    public static void deleteTables(Connection dbconn)
    {
    	
    }
    
    //********** SECTION 2: INSERTION COMMANDS **********//
    /**
     *  The insertPassenger() method inserts into the table 'passenger' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void insertPassenger(Connection dbconn, String file_name)
    {
    	File fileRef = null;                     // provides exists() method
		BufferedReader reader = null;            // provides buffered text I/O
		System.out.println(file_name);
		// If the CSV file doesn't exist, we can't proceed.
		try 
		{
			fileRef = new File(file_name);
			if (!fileRef.exists()) {
				System.out.println("PROBLEM:  The input file `" + file_name + "' "
						+ "does not exist in the current directory.");
				System.out.println("          Create or copy the file to the "
						+ "current directory and try again.");
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			System.out.println("I/O ERROR: Something went wrong with the "
					+ "detection of the CSV input file.");
			System.exit(-1);
		}
		// Read the content of the CSV file into an ArrayList
		// of DataRecord objects.
		try {
			reader = new BufferedReader(new FileReader(fileRef));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			String line = null;  // content of one line/record of the CSV file
			//
			while((line = reader.readLine()) != null) 
			{
				String[] field = line.split(",");
				//CHECK IF NUMBER OF FIELDS MATCH
				if(field.length == 5)
				{
					//CHECK CONSTRAINTS
					if(checkInt(field[0])&&checkNeg(field[0])==0&&field[1].length()<=60&&
					checkBool(field[2])&&checkBool(field[3])&&checkBool(field[4]))
					{
						//ADD THE ROW OF CSV if passes
						try 
						{	 
							PreparedStatement stmt = null;
							String sql = "INSERT INTO Passenger (pid, pname, is_student, is_freq, is_priority)"
									+ "VALUES (?, ?, ?, ?, ?)";
							stmt = dbconn.prepareStatement(sql);
							stmt.setInt(1, Integer.parseInt(field[0]));
							stmt.setString(2, field[1]);
							stmt.setBoolean(3, Boolean.parseBoolean(field[2]));
							stmt.setBoolean(4, Boolean.parseBoolean(field[3]));
							stmt.setBoolean(5, Boolean.parseBoolean(field[4]));
							int row = stmt.executeUpdate();
							stmt.close();
						} 
						catch (SQLException e) 
						{
							System.err.println("*** SQLException:  "
				                    + "Problem inserting into passenger");
				                System.err.println("\tMessage:   " + e.getMessage());
				                System.err.println("\tSQLState:  " + e.getSQLState());
				                System.err.println("\tErrorCode: " + e.getErrorCode());
				                System.exit(-1);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("I/O ERROR: Couldn't open, or couldn't read "
					+ "from, the CSV file.");
			System.exit(-1);
		}
		//CLOSING FILE
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
					+ "the CSV file!");
			System.exit(-1);
		}
    }
    /**
     *  The insertPassengerHistory() method inserts into the table 'passenger_history' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void insertPassengerHistory(Connection dbconn, String file_name)
    {
    	File fileRef = null;                     // provides exists() method
		BufferedReader reader = null;            // provides buffered text I/O
		System.out.println(file_name);
		// If the CSV file doesn't exist, we can't proceed.
		try 
		{
			fileRef = new File(file_name);
			if (!fileRef.exists()) {
				System.out.println("PROBLEM:  The input file `" + file_name + "' "
						+ "does not exist in the current directory.");
				System.out.println("          Create or copy the file to the "
						+ "current directory and try again.");
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			System.out.println("I/O ERROR: Something went wrong with the "
					+ "detection of the CSV input file.");
			System.exit(-1);
		}
		// Read the content of the CSV file into an ArrayList
		// of DataRecord objects.
		try {
			reader = new BufferedReader(new FileReader(fileRef));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			String line = null;  // content of one line/record of the CSV file
			//
			while((line = reader.readLine()) != null) 
			{
				String[] field = line.split(",");
				//CHECK IF NUMBER OF FIELDS MATCH
				if(field.length == 4)
				{
					//CHECK CONSTRAINTS
					if(checkInt(field[0])&&checkNeg(field[0])==0
					&&checkInt(field[1])&&checkNeg(field[1])==0
					&&checkInt(field[2])&&checkNeg(field[2])==0
					&&checkBool(field[3]))
					{
						//ADD THE ROW OF CSV if passes
						try 
						{	 
							PreparedStatement stmt = null;
							String sql = "INSERT INTO passenger_history (pid, fid, cbag, food)"
									+ "VALUES (?, ?, ?, ?)";
							stmt = dbconn.prepareStatement(sql);
							stmt.setInt(1, Integer.parseInt(field[0]));
							stmt.setInt(2, Integer.parseInt(field[1]));
							stmt.setInt(3, Integer.parseInt(field[2]));
							stmt.setBoolean(4, Boolean.parseBoolean(field[3]));
							int row = stmt.executeUpdate();
							stmt.close();
						} 
						catch (SQLException e) 
						{
							System.err.println("*** SQLException:  "
				                    + "Problem inserting into passenger");
				                System.err.println("\tMessage:   " + e.getMessage());
				                System.err.println("\tSQLState:  " + e.getSQLState());
				                System.err.println("\tErrorCode: " + e.getErrorCode());
				                System.exit(-1);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("I/O ERROR: Couldn't open, or couldn't read "
					+ "from, the CSV file.");
			System.exit(-1);
		}
		//CLOSING FILE
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
					+ "the CSV file!");
			System.exit(-1);
		}
    }
    /**
     *  The insertFlight() method inserts into the table 'flight' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void insertFlight(Connection dbconn, String file_name)
    {
    	File fileRef = null;                     // provides exists() method
		BufferedReader reader = null;            // provides buffered text I/O
		System.out.println(file_name);
		// If the CSV file doesn't exist, we can't proceed.
		try 
		{
			fileRef = new File(file_name);
			if (!fileRef.exists()) {
				System.out.println("PROBLEM:  The input file `" + file_name + "' "
						+ "does not exist in the current directory.");
				System.out.println("          Create or copy the file to the "
						+ "current directory and try again.");
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			System.out.println("I/O ERROR: Something went wrong with the "
					+ "detection of the CSV input file.");
			System.exit(-1);
		}
		// Read the content of the CSV file into an ArrayList
		// of DataRecord objects.
		try {
			reader = new BufferedReader(new FileReader(fileRef));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			String line = null;  // content of one line/record of the CSV file
			//
			while((line = reader.readLine()) != null) 
			{
				String[] field = line.split(",");
				//CHECK IF NUMBER OF FIELDS MATCH
				if(field.length == 5)
				{
					//CHECK CONSTRAINTS
					if(checkInt(field[0])&&checkNeg(field[0])==0&&
					field[1].length()<=15&&field[2].length()<=5&&
					checkTime(field[3])&&checkTime(field[4])&&checkTime(field[5])&&
					field[6].length()<=50&&checkDate(field[7])&&checkBool(field[8]))
					{
						//ADD THE ROW OF CSV if passes
						try 
						{	 
							PreparedStatement stmt = null;
							String sql = "INSERT INTO flight (fid, fairline, bgate, btime, dtime, "
									+ "duration, route, fdate, is_depart)"
									+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
							stmt = dbconn.prepareStatement(sql);
							stmt.setInt(1, Integer.parseInt(field[0]));
							stmt.setString(2, field[1]);
							stmt.setString(3, field[2]);
							stmt.setTime(4, Time.valueOf(field[3]));
							stmt.setTime(5, Time.valueOf(field[4]));
							stmt.setTime(6, Time.valueOf(field[5]));
							stmt.setString(7, field[6]);
							stmt.setDate(8, Date.valueOf(field[7]));
							stmt.setBoolean(9, Boolean.parseBoolean(field[8]));
							int row = stmt.executeUpdate();
							stmt.close();
						} 
						catch (SQLException e) 
						{
							System.err.println("*** SQLException:  "
				                    + "Problem inserting into passenger");
				                System.err.println("\tMessage:   " + e.getMessage());
				                System.err.println("\tSQLState:  " + e.getSQLState());
				                System.err.println("\tErrorCode: " + e.getErrorCode());
				                System.exit(-1);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("I/O ERROR: Couldn't open, or couldn't read "
					+ "from, the CSV file.");
			System.exit(-1);
		}
		//CLOSING FILE
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
					+ "the CSV file!");
			System.exit(-1);
		}
    }
    //TODO
    /**
     *  The insertStaff() method inserts into the table 'staff' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void insertStaff(Connection dbconn, String file_name)
    {
    	File fileRef = null;                     // provides exists() method
		BufferedReader reader = null;            // provides buffered text I/O
		System.out.println(file_name);
		// If the CSV file doesn't exist, we can't proceed.
		try 
		{
			fileRef = new File(file_name);
			if (!fileRef.exists()) {
				System.out.println("PROBLEM:  The input file `" + file_name + "' "
						+ "does not exist in the current directory.");
				System.out.println("          Create or copy the file to the "
						+ "current directory and try again.");
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			System.out.println("I/O ERROR: Something went wrong with the "
					+ "detection of the CSV input file.");
			System.exit(-1);
		}
		// Read the content of the CSV file into an ArrayList
		// of DataRecord objects.
		try {
			reader = new BufferedReader(new FileReader(fileRef));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			String line = null;  // content of one line/record of the CSV file
			//
			while((line = reader.readLine()) != null) 
			{
				String[] field = line.split(",");
				//CHECK IF NUMBER OF FIELDS MATCH
				if(field.length == 5)
				{
					//CHECK CONSTRAINTS
					if(checkInt(field[0])&&checkNeg(field[0])==0&&
					field[1].length()<=60&&field[2].length()<=40&&field[3].length()<=15)
					{
						//ADD THE ROW OF CSV if passes
						try 
						{	 
							PreparedStatement stmt = null;
							String sql = "INSERT INTO staff (sid, sname, srole, sairline) "
									+ "VALUES (?, ?, ?, ?)";
							stmt = dbconn.prepareStatement(sql);
							stmt.setInt(1, Integer.parseInt(field[0]));
							stmt.setString(2, field[1]);
							stmt.setString(3, field[2]);
							stmt.setString(4, field[3]);
							int row = stmt.executeUpdate();
							stmt.close();
						} 
						catch (SQLException e) 
						{
							System.err.println("*** SQLException:  "
				                    + "Problem inserting into passenger");
				                System.err.println("\tMessage:   " + e.getMessage());
				                System.err.println("\tSQLState:  " + e.getSQLState());
				                System.err.println("\tErrorCode: " + e.getErrorCode());
				                System.exit(-1);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("I/O ERROR: Couldn't open, or couldn't read "
					+ "from, the CSV file.");
			System.exit(-1);
		}
		//CLOSING FILE
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
					+ "the CSV file!");
			System.exit(-1);
		}
    }
    //TODO
    /**
     *  The insertFlightStaff() method inserts into the table 'flight_staff' in the DB
     * @param dbconn is the connection object to the DB
     */
    public static void insertFlightStaff(Connection dbconn, String file_name)
    {
    	File fileRef = null;                     // provides exists() method
		BufferedReader reader = null;            // provides buffered text I/O
		System.out.println(file_name);
		// If the CSV file doesn't exist, we can't proceed.
		try 
		{
			fileRef = new File(file_name);
			if (!fileRef.exists()) {
				System.out.println("PROBLEM:  The input file `" + file_name + "' "
						+ "does not exist in the current directory.");
				System.out.println("          Create or copy the file to the "
						+ "current directory and try again.");
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			System.out.println("I/O ERROR: Something went wrong with the "
					+ "detection of the CSV input file.");
			System.exit(-1);
		}
		// Read the content of the CSV file into an ArrayList
		// of DataRecord objects.
		try {
			reader = new BufferedReader(new FileReader(fileRef));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			String line = null;  // content of one line/record of the CSV file
			//
			while((line = reader.readLine()) != null) 
			{
				String[] field = line.split(",");
				//CHECK IF NUMBER OF FIELDS MATCH
				if(field.length == 5)
				{
					//CHECK CONSTRAINTS
					if(checkInt(field[0])&&checkNeg(field[0])==0&&
					checkInt(field[1])&&checkNeg(field[1])==0)
					{
						//ADD THE ROW OF CSV if passes
						try 
						{	 
							PreparedStatement stmt = null;
							String sql = "INSERT INTO flight_staff (sid, fid)"
									+ "VALUES (?, ?)";
							stmt = dbconn.prepareStatement(sql);
							stmt.setInt(1, Integer.parseInt(field[0]));
							stmt.setInt(2, Integer.parseInt(field[8]));
							int row = stmt.executeUpdate();
							stmt.close();
						} 
						catch (SQLException e) 
						{
							System.err.println("*** SQLException:  "
				                    + "Problem inserting into passenger");
				                System.err.println("\tMessage:   " + e.getMessage());
				                System.err.println("\tSQLState:  " + e.getSQLState());
				                System.err.println("\tErrorCode: " + e.getErrorCode());
				                System.exit(-1);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("I/O ERROR: Couldn't open, or couldn't read "
					+ "from, the CSV file.");
			System.exit(-1);
		}
		//CLOSING FILE
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
					+ "the CSV file!");
			System.exit(-1);
		}
    }
    
    //********** SECTION 3: CSV CONSTRAINT CHECKERS **********//
    /**
     * checkInt() is a checker method that reads string and checks if it is 
     * of type integer.
     * @param value, string value being checked.
     * @return true if value is in format of time, false if not.
     */
    public static boolean checkInt(String value)
	{
    	//IF EMPTY
    	if(value == "")
    	{
    		return false;
    	}
    	//IF LENGTH IS 1, CAN'T BE NEGATIVE
    	else if(value.length() == 1)
    	{
    		if(value.charAt(0) < '0'||value.charAt(0) > '9')
			{
				return false;
			}
    		else
    		{
    			return true;
    		}
    	}
    	else if(value.length() > 1)
    	{
    		//ACCOUNT for negative sign
    		if((value.charAt(0) != '-') && (value.charAt(0) < '0'||value.charAt(0) > '9'))
			{
				return false;
			}
    		//SCAN NUMBERS OR DETECT LETTERS
			for(int x = 1; x < value.length(); x++)
			{
				if(value.charAt(x) < '0'||value.charAt(x) > '9')
				{
					return false;
				}
			}
    	}
		return true;
	}
    /**
     * checkDouble() is a checker method that reads string and checks if it is 
     * of type double.
     * @param value, string value being checked.
     * @return true if value is type double, false if not.
     */
	public static boolean checkDouble(String value)
	{
		boolean negative = false;
		boolean decimal = false;
		//check if the String value is at least 2 characters
		if(value.length() < 2)
		{
			return false;
		}
		//check if the String has a negative sign at position 1
		if(value.charAt(0) == '-')
		{
			negative = true;
		}
		else if(value.charAt(0) == '.')
		{
			decimal = true;
		}
		if(value.charAt(1) == '-')
		{
			return false;
		}
		//check if there exists a decimal point already
		else if(value.charAt(1) == '.' && decimal == true)
		{
			return false;
		}
		for(int x = 2; x < value.length(); x++)
		{
			if((value.charAt(x) < '0' || value.charAt(x) > '9') && value.charAt(x) != '.')
			{
				return false;
			}
			else if(value.charAt(x) == '.' && decimal == true)
			{
				return false;
			}
		}
		return true;
	}
	/**
     * checkDate() is a checker method that reads string and checks if it is 
     * equivalent to a date value in format (YYYY-MM-DD),
     * @param value, string value being checked.
     * @return true if value is in format of date, false if not.
     */
    public static boolean checkDate(String value)
    {
    	String [] splitDate = value.split("-");
    	if(splitDate.length != 3)
    	{
    		return false;
    	}
    	else
    	{
    		//CHECK IF FORMAT IS INT OR NOT
    		if(!(checkInt(splitDate[0])) || checkNeg(splitDate[0])!=0 ||
    		!(checkInt(splitDate[1])) || checkNeg(splitDate[1])!=0 ||
    		!(checkInt(splitDate[2])) || checkNeg(splitDate[2])!=0)
        	{return false;}
    		else
    		{
    			int split1 = Integer.parseInt(splitDate[0]);
        		int split2 = Integer.parseInt(splitDate[1]);
        		int split3 = Integer.parseInt(splitDate[2]);
    			//System.out.println("checkDate success");
        		//CHECK IF MONTH AND YEAR WITHIN CONFINES OF LOGIC
    			if((split1<0) || (split2<1||split2>12)) 
    			{return false;}
    			//CHECK DAY NOT NEGATIVE
    			else if(split3 < 1)
    			{return false;}
    			//CHECK MONTH AND DAY
    			//JAN
    			if(split2==1)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//FEB
    			if(split2==2)
    			{
    				//IF LEAP
    				if((split1%4==0)&&(((split1)/4)%2 == 0))
    				{
    					if(split3>=1&&split3<=29)
    					{
    						return true;
    					}
    					return false;
    				}
    				//IF NOT LEAP
    				else 
    				{
    					if(split3>=1&&split3<=28)
    					{
    						return true;
    					}
    					return false;
    				}
    			}
    			//MARCH
    			if(split2==3)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//APRIL
    			if(split2==4)
    			{
    				if(split3>=1&&split3<=30)
					{
    					return true;
					}
					return false;
    			}
    			//MAY
    			if(split2==5)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//JUNE
    			if(split2==6)
    			{
    				if(split3>=1&&split3<=30)
					{
    					return true;
					}
					return false;
    			}
    			//JULY
    			if(split2==5)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//AUGUST
    			if(split2==8)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//SEPTEMBER
    			if(split2==9)
    			{
    				if(split3>=1&&split3<=30)
					{
    					return true;
					}
					return false;
    			}
    			//OCTOBER
    			if(split2==10)
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    			//NOVEMBER
    			if(split2==11)
    			{
    				if(split3>=1&&split3<=30)
					{
    					return true;
					}
					return false;
    			}
    			//DECEMBER
    			else
    			{
    				if(split3>=1&&split3<=31)
					{
    					return true;
					}
					return false;
    			}
    		}
    	}
    }
    /**
     * checkTime() is a checker method that reads string and checks if it is 
     * equivalent to a time value in format HH:MM:SS 00:00:00-23:59:59)
     * @param value, string value being checked.
     * @return true if value is in format of time, false if not.
     */
    public static boolean checkTime(String value)
    {
    	String [] splitTime = value.split(":");
    	//System.out.println(splitTime[0]);
    	if(splitTime.length != 3)
    	{
    		return false;
    	}
    	else
    	{
    		//CHECK IF INT
    		if(!(checkInt(splitTime[0])) || checkNeg(splitTime[0])!=0 ||
    		!(checkInt(splitTime[1])) || checkNeg(splitTime[1])!=0 ||
    		!(checkInt(splitTime[2])) || checkNeg(splitTime[2])!=0)
    		{
    			return false;
    		}
    		else
    		{
    			int split1 = Integer.parseInt(splitTime[0]);
    			int split2 = Integer.parseInt(splitTime[1]);
    			int split3 = Integer.parseInt(splitTime[2]);
    			//CHECK HOURS, MINUTES, AND SECONDS
    			if((split1<0||split1>23) || (split2<0||split2>59) || (split3<0||split3>59))
    			{
    				return false;
    			}
    			else
    			{
    				//System.out.println("checkTime success");
    				return true;
    			}
    		}
    	}
    }
    /**
     * checkBool() is a checker method that reads string and checks if it is 
     * of type boolean.
     * @param value, string value being checked.
     * @return true if value is of type boolean, false if not.
     */
    public static boolean checkBool(String value)
    {
    	if(value.equalsIgnoreCase("true")|| value.equalsIgnoreCase("false"))
    	{
    		return true;
    	}
    	return false;
    }
    /**
     * checkNeg() is a checker method that reads string and checks if it is 
     * of type boolean.
     * @param value, string value being checked.
     * @return 0 if value is positive, 1 if negative, -1 if checkInt fails.
     */
    public static int checkNeg(String value)
    {
    	if(!checkInt(value))
    	{
    		return -1;
    	}
    	else if(Integer.parseInt(value) < 0)
    	{
    		return 1;
    	}
    	else
    	{
    		return 0;
    	}
    }
}
