package XMLParser;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StarsMysql {
	private int id;
	private String first_name;
	private String last_name;
	private String dob;
	private String photo_url;
	private String stage_name;
	
	public StarsMysql(){
	}
	public StarsMysql(int id, String first_name, String last_name, String dob, String photo_url, String stage_name){
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.dob = dob;
		this.photo_url = photo_url;
		this.stage_name= stage_name;
	}
	// GETTERS
	public int getId(){
		return this.id;
	}
	public String getFirst(){
		return this.first_name;
	}
	public String getLast(){
		return this.last_name;
	}
	public String getDob(){
		String returnDob =(this.dob.length() != 0)? String.format("%s-00-00", this.dob): null;
		return returnDob;
	}
	public String getPhoto(){
		return this.photo_url;
	}
	public String getStage(){
		return this.stage_name;
	}
	//SETTERS
	public void setId( int Id ){
		this.id = Id;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public void setDob(String dob) {
		
		this.dob = dob;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
	public void setStage(String stage_name) {
		this.stage_name= stage_name;
	}
	
	public Boolean isValid(){
		Boolean valid = true;
		
		String pattern = "([^-.\\w\\d])";
		Pattern r = Pattern.compile(pattern);
		
		Matcher fn = r.matcher(this.first_name);
		Matcher ln = r.matcher(this.last_name);
		
		if(fn.find()){ return false; }
		else if(ln.find()){ return false; }

		
		if(this.dob != ""){
			try{
				int date = Integer.parseInt(this.dob);
			}catch(NumberFormatException e){
				return false;
			}
		}
		
		return valid;
	}
	
	
	public String toString() {
		String result= "\n--------------\n";
		result += "First name: " + this.first_name + "\n Last name: " + this.last_name
				+ "\n " + "DOB: " + this.dob + "\n"
				+ "Photo Url: " + this.photo_url;
		return result;
		
		
		
		
		
		
	}
}