package XMLParser;

import java.sql.*;

import javax.sql.*;

import java.io.*;

import javax.xml.parsers.*;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
public class xmlParser extends DefaultHandler {

		public static HashMap<String, MoviesMysql> xmlMovies;
		public ArrayList<StarsMysql> xmlActors;
		
		private String tempVal;
		 
		private String tempDirector;
		private String tempID;
		
		private StarsMysql tempStar;
		private MoviesMysql tempMovie;
		private GenresMysql tempMysql; 
		private String dataType;

		private int wrongCount = 0;
		
		public xmlParser(){
		}
		
		@Override
		public void startDocument() throws SAXException {
			System.out.println("Start document");
		}
		
		@Override
		public void endDocument() throws SAXException {
			System.out.println("End document");
		}

		public void executeParse(String fileName) throws SAXException{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			
			try{

				SAXParser sp = spf.newSAXParser();
				sp.parse(new File(fileName), this);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		

		// startElement(), characters(), endElement()
		// Help take the information from xml parsing and convert it into the appropriate classes 
		// 		[ Genres, Movies, Stars]
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes )
			throws SAXException{
				//System.out.println("Start element: " + qName);
				tempVal = "";
			try{
				if(qName.equalsIgnoreCase("movies")){
					xmlMovies = new HashMap<String, MoviesMysql>();
				}
				if(qName.equalsIgnoreCase("actors")){
					xmlActors = new ArrayList<StarsMysql>();
				}
				if(qName.equalsIgnoreCase("actor")){
					tempStar = new StarsMysql();
					//System.out.println("CREATED NEW STAR");
					//System.out.println("");
				}
				if(qName.equalsIgnoreCase("film")){
					tempMovie = new MoviesMysql();
					System.out.println("CREATED NEW MOVIE");
					System.out.println("");
				}
				if( (qName.equalsIgnoreCase("released")) || (qName.equalsIgnoreCase("rereleased")))
				{
					tempMovie.setYear(tempVal);
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
				System.out.println("ERROR");
			}

		}
		//IS triggered whenever string of characters is encountered
		//Can happen multiple times, before a start element and end element
		@Override
		public void characters(char[] ch, int start, int length){
			tempVal = new String(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
			throws SAXException{
			
			//System.out.println("End element: " + qName);
			if(qName.equalsIgnoreCase("actor")){
				if(tempStar.isValid()){
					xmlActors.add(tempStar);
				}else{
					this.wrongCount += 1;
					System.out.println("******ERROR: " + this.wrongCount + "********");
					//System.out.println(tempStar.toString());
				}
			}else if(qName.equalsIgnoreCase("dob")){
				tempStar.setDob(tempVal);
				//System.out.println("SET DATE");
				
			}else if(qName.equalsIgnoreCase("stagename")) {
				String[] full_name= tempVal.split(" ");
				int length = full_name.length;
				tempStar.setLast_name(full_name[length-1]);
				tempStar.setFirst_name(full_name[0]);
			}//Adding a film
			else if(qName.equalsIgnoreCase("film")) {
				//tempMovie.setDirector(tempDirector);
				xmlMovies.put(tempMovie.getId(), tempMovie);
				//System.out.println("ADDED MOVIE");
				//System.out.println("");
			}
			else if(qName.equalsIgnoreCase("dirname")) {
				//System.out.println("Directors name: " + tempVal.getClass().getName());
				tempDirector= tempVal;
				//System.out.println("SET DIRECTOR NAME");
			}
			else if(qName.equalsIgnoreCase("dirn")) {
				
				tempMovie.setDirector(tempVal);
				//System.out.println("SET DIRECTOR NAME");
			}
			else if(qName.equalsIgnoreCase("t")) {
				tempMovie.setTitle(tempVal);
				//System.out.println("SET TITLE NAME");
			}
			else if(qName.equalsIgnoreCase("cat")) {
				tempMovie.addGenre(tempVal);
				//System.out.println("ADDED GENRE");
			}
			else if( (qName.equalsIgnoreCase("released")) ||  (qName.equalsIgnoreCase("rereleased")) || (qName.equalsIgnoreCase("year")))
			{
				System.out.println("Year released= " + tempVal);
				tempMovie.setYear(tempVal);
				System.out.println("SET YEAR RELEASED");
			}
			else if(qName.equalsIgnoreCase("fid")) {
				tempMovie.setId(tempVal);
				System.out.println("ADDED VALUE");
			}//For setting movie value
			else if(qName.equalsIgnoreCase("f")) {
				tempID= tempVal;
				System.out.println("SET MOVIE VALUE");
			}else if(qName.equalsIgnoreCase("a")) {
				StarsMysql new_star = new StarsMysql();
				
				System.out.println("CREATED NEW STAR");
				
				System.out.println(tempVal);
				String[] full_name= tempVal.split(" ");
				int length = full_name.length;
				new_star.setLast_name(full_name[length-1]);
				new_star.setFirst_name(full_name[0]);
				
				System.out.println("SET NAME FOR NEW STAR");
				System.out.println(tempID);
				
				MoviesMysql new_movie = xmlMovies.get(tempID);
				//If there is not a matching movie in Main compared to Cast, then do not add!
				if (new_movie != null) 
				{
				
				System.out.println(new_movie);
				System.out.println(new_star);
				
				System.out.println("RETRIEVED MOVIE");
				new_movie.addActor(new_star);
				
				System.out.println("ADDED ACTOR");
				xmlMovies.put(tempID, new_movie);
				
				System.out.println("ADDED ACTOR TO MOVIE");
				}
			}
			

		}

	public void addBatch(String fileType){
		String MoviesString = "";

		String StarsString = "insert into stars(first_name, last_name, dob, photo_url) values(?,?,?,?);";
		
		
		
		if(fileType.equals("stars")){
			fileType = StarsString;
			try{
				
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false", "root", "F4uit_8!");
	
				
				PreparedStatement psInsert = null;		
				connection.setAutoCommit(false);
				
				int batchCount = (xmlActors.size()/1000)+1;
				
				for(int i = 0; i < batchCount; i++){
					psInsert = connection.prepareStatement(fileType);
					executeBatch(connection, psInsert, i);
				}
				
					
				connection.close();
			
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}catch(SQLException e){
				System.out.println("Exception:");
				System.out.println(e);
			}
		}else if (fileType.equals("cast")){
			try{
				
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false", "root", "F4uit_8!");
	
				
				PreparedStatement psInsert = null;		
				connection.setAutoCommit(false);
				String q = "CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement cs = connection.prepareStatement(q);
				//ITERATING THROUGH HASH MAP
				Iterator it= this.xmlMovies.entrySet().iterator();
				
				int counter = 0;
				int hashSize = xmlMovies.size();
				int batchCount = (hashSize/1000)+1;

				while (it.hasNext()) {
					
					Map.Entry pair = (Map.Entry)it.next();
					
					MoviesMysql movie= (MoviesMysql) pair.getValue();
					
					String title= movie.getTitle();
					String year= movie.getYear();
					String director = movie.getDirector();
					String all_null = "null";
					
					ArrayList<StarsMysql> actor_list = movie.getActors();
					
					
					
					ArrayList<String> genre_list = movie.getGenres();
					for(int i =0; i < genre_list.size(); i++) {
						String genre_name = genre_list.get(i);
						
						cs.setString(1,  title);
						cs.setString(2,  year);
						cs.setString(3,  director);
						cs.setString(4,  all_null);
						cs.setString(5,  all_null);
						cs.setString(6,  all_null);
						cs.setString(7,  all_null);
						cs.setNull(8,  0);
						cs.setString(9,  all_null);
						cs.setString(10,  genre_name); 
						counter++;
						cs.addBatch();
						
						
					}
					if(counter%1000 == 0){
						cs.executeBatch();
						counter = 0;
					}
					
					
					
					
					
					
					it.remove();
				}
				cs.executeBatch();
				
				connection.close();
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e){
			System.out.println("Exception:");
			System.out.println(e);
		}
		}
	}
	
	
	public void insertMovies()
	{
		HashMap<String, String> movie_hash= new HashMap<String, String>();
		ArrayList<String> genre_list= new ArrayList<String>();
		
		
		int nullz= 0;
		int movies_added = 0;
		int movies_rejected = 0;

		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false", "root", "F4uit_8!");
			Statement select = connection.createStatement();
			
			ResultSet rs= select.executeQuery("SELECT * FROM movies");
			
			while (rs.next())
			{
				String title= rs.getString(2).toLowerCase().replaceAll("'.,\n ", "");
				String director =  rs.getString(4).toLowerCase().replaceAll("'.,\n ", "");
				movie_hash.put(title, director);
			}
			
			rs= select.executeQuery("SELECT * FROM genres");
			while (rs.next())
			{
				String g_name= rs.getString(2);
				genre_list.add(g_name.toLowerCase());
			}
			
			
			
			//iterating through movies
			Iterator it= this.xmlMovies.entrySet().iterator();
			
			int counter = 0;
			int hashSize = xmlMovies.size();
			int batchCount = (hashSize/1000)+1;

			while (it.hasNext()) {
				
				Map.Entry pair = (Map.Entry)it.next();
				
				MoviesMysql movie= (MoviesMysql) pair.getValue();
				
				String title= movie.getTitle();
				int year= 0;
				try{
					year= Integer.parseInt(movie.getYear());
				}
				catch (Exception e){
					System.out.println("Invalid year entry, MOVIE NOT ADDED!!!");
					movies_rejected ++;
					continue;
				}
				String director = movie.getDirector();
				System.out.println(director);
			
				if ( !(movie_hash.containsKey(title.toLowerCase().replaceAll("'.,\n ", "")) && movie_hash.get(title.toLowerCase().replaceAll("'.,\n ", "")).equals(director.toLowerCase().replaceAll("'.,\n ", ""))    )  )
				{
					String query = "INSERT INTO movies VALUES(?, ?, ?, ?, ?, ?)";
					
					PreparedStatement ps= connection.prepareStatement(query);
					ps.setNull(1, nullz); 
					ps.setString(2,title);
					ps.setInt(3, year);

					ps.setString(4, director);
					ps.setNull(5, nullz);
					ps.setNull(6, nullz);
					try{
						ps.executeUpdate();
					}
					catch(Exception e)
					{
						
						System.out.println("Movie not added *************************************************************************************************");
						movies_rejected ++;
						continue;
					}
					
					movies_added ++;
					//movie_hash.put(title.toLowerCase().replaceAll("'.,\n ", ""), director.toLowerCase().replaceAll("'.,\n ", ""));
					
					ArrayList<String> movie_genres = movie.getGenres();
					
					rs= select.executeQuery("SELECT max(id) from movies");
					
					rs.next();
					int movie_id = rs.getInt(1);
					
					
					for (int i=0; i < movie_genres.size(); i++){
						String g_name =movie_genres.get(i);
						
						if (!(genre_list.contains(g_name.toLowerCase())))
						{
							String genre_query = "INSERT INTO genres VALUES(?, ?)";
							
							ps= connection.prepareStatement(genre_query);
							ps.setNull(1, nullz); 
							ps.setString(2,g_name);
							
							ps.executeUpdate();
							genre_list.add(g_name.toLowerCase());
							System.out.println("GENRE ADDED!!!!!!!!");
							
							
							
						}
						else
						{
							System.out.println("GENRE REJECTED ********************************************************************");
						}
						
						query = "SELECT id FROM genres WHERE name=" + g_name;
						
						rs= select.executeQuery("SELECT id FROM genres WHERE name='" + g_name + "'");
						
						rs.next();
						int genre_id = rs.getInt(1);
						
						
						
						
						//ADDING GENRE TO GIM
						
						String gim_query = "INSERT INTO genres_in_movies VALUES(?, ?)";
						
						ps= connection.prepareStatement(gim_query);
						ps.setInt(1, genre_id); 
						ps.setInt(2, movie_id);
						ps.executeUpdate();
						System.out.println("GENRE AND MOVIE HAVE BEEN LINKED*********************************************************************************");
						
						
					}
					
					
					
					
					
					System.out.println(title + " added!");
				}
				else {
					System.out.println("ALREADY EXISTS");
				}
				
			}
			
			//System.out.println("MOVIES ADDED: " + Integer.toString(movies_added));
			System.out.println("MOVIES REJECTED: " + Integer.toString(movies_rejected));
			connection.close();	
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			
		}catch(SQLException e){
			System.out.println("Exception:");
			System.out.println(e);
		}
	}
	
	
	
	private void executeBatch(Connection con, PreparedStatement ps, int index) throws SQLException{
		Statement select = con.createStatement();
		
			int batchStart = index*1000;
			int batchEnd = (batchStart+1000 < xmlActors.size())?batchStart+1000: xmlActors.size();
			
			for(int j = batchStart; j < batchEnd; j++){
				StarsMysql insertStar = (StarsMysql)xmlActors.get(j);
				ResultSet rs = select.executeQuery(String.format("SELECT COUNT(*) FROM stars WHERE first_name = \"%s\" AND last_name = \"%s\";", 
						insertStar.getFirst(), insertStar.getLast(), insertStar.getDob()));
				rs.next();
				if(rs.getInt(1) != 0){ 
					continue; 
				}
				ps.setString(1, insertStar.getFirst());
				ps.setString(2, insertStar.getLast());
				ps.setString(3, insertStar.getDob());
				ps.setString(4, insertStar.getPhoto());
				ps.addBatch();
			}
			System.out.print(String.format("^^^^^^^^^^^^^^####%s",index));
			ps.executeBatch();
			con.commit();
	}
	

	
	
	public static void main(String[] args) throws SAXException {
		xmlParser xmlP = new xmlParser();
		xmlP.executeParse("actors63.xml");
		xmlP.addBatch("stars");
		xmlP.executeParse("mains243.xml");
		xmlP.executeParse("casts124.xml");
		
		System.out.println(xmlMovies.size());
		
		xmlP.insertMovies();
		
		

	}
}