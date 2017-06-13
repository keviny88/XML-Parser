package XMLParser;

import java.util.ArrayList;

public class MoviesMysql {
	private String id;
	private String title;
	private String year;
	private String director;

	private ArrayList<String> genres;
	private ArrayList<StarsMysql> actors;
	
	public MoviesMysql(){
		this.genres = new ArrayList<String>();
		this.actors = new ArrayList<StarsMysql>();
	}
	public MoviesMysql(String id, String title, String year, String director, String banner_url, String trailer_url){
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
		this.genres = new ArrayList<String>();
	}
	
	public String getId(){
		return this.id;
	}
	public String getTitle(){
		return this.title;
	}
	public String getYear(){
		return this.year;
	}
	public String getDirector(){
		return this.director;
	}
	public ArrayList<String> getGenres(){
		return this.genres;
	}
	public ArrayList<StarsMysql> getActors(){
		return this.actors;
	}

	
	//SETTERS
	
	public void setId(String id){
		this.id= id;
	}
	public void setTitle(String title){
		this.title= title;
	}
	public void setYear(String year){
		this.year = year;
	}
	public void setDirector(String director){
		this.director = director;
	}
	public void addGenre(String genre){
		this.genres.add(genre);
	}
	public void addActor(StarsMysql actor){
		this.actors.add(actor);
	}
	
	
	public String toString() {
		String result= "\n--------------\n";
		result += "ID: " + this.id + "\n Title: " + this.title + "\n Year: " + this.year
				+ "\n " + "Director: " + this.director + "\n" + "Genres: ";
		for (int i=0; i < genres.size(); i++)
		{
			result += genres.get(i) + ", ";
		}
		result += "\nActors: ";
		for (int i=0; i < actors.size(); i++)
		{
			result += actors.get(i) + ", ";
		}
		return result;
	}
	
	
	
}

