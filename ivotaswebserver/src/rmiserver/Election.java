package rmiserver;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Election implements Serializable{
    String name;
    int id;
    Department department;
    String description;
    Date startDate, endDate;  //Timestamp end = new Timestamp(endDate.getTime());
    int blankVotes, nullVotes;

	public Election(String name, int id, Department department, String description, Date startDate, Date endDate,
			int blankVotes, int nullVotes) {
		this.name = name;
		this.id = id;
		this.department = department;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.blankVotes = blankVotes;
		this.nullVotes = nullVotes;
	}

	public String getPrettyStartDate(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		if(startDate.after(new Date())){
			double secs = Math.floor((startDate.getTime() - new Date().getTime()) / 1000);
			if (secs < 60) return df.format(this.startDate) + " (in " + secs + " sec(s) )";
			if (secs < 3600) return df.format(this.startDate) + " (in " + Math.floor(secs / 60) + " min(s))";
			if (secs < 86400) return df.format(this.startDate) + " (in " + Math.floor(secs / 3600) + " hour(s))";
			if (secs < 604800) return df.format(this.startDate) + " (in " +  Math.floor(secs / 86400) + " day(s))";
			
		}else { //good
			double secs = Math.floor((new Date().getTime() - startDate.getTime()) / 1000);
			if (secs < 60) return df.format(this.startDate) + " (" + secs + " sec(s) ago";
			if (secs < 3600) return df.format(this.startDate) + " (" + Math.floor(secs / 60) + " min(s) ago)";
			if (secs < 86400) return df.format(this.startDate) + " (" + Math.floor(secs / 3600) + " hour(s) ago)";
			if (secs < 604800) return df.format(this.startDate) + " (" +  Math.floor(secs / 86400) + " day(s) ago)";
		}
		
		return df.format(this.endDate);
	}
	
	public String getPrettyEndDate(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		if(endDate.after(new Date())){
			double secs = Math.floor((endDate.getTime() - new Date().getTime()) / 1000);
			if (secs < 60) return df.format(this.endDate) + " (in " + secs + " sec(s))";
			if (secs < 3600) return df.format(this.endDate) + " (in " + Math.floor(secs / 60) + " min(s))";
			if (secs < 86400) return df.format(this.endDate) + " (in " + Math.floor(secs / 3600) + " hour(s))";
			if (secs < 604800) return df.format(this.endDate) + " (in " +  Math.floor(secs / 86400) + " day(s))";
			
		}else { //good
			double secs = Math.floor((new Date().getTime() - endDate.getTime()) / 1000);
			if (secs < 60) return df.format(this.endDate) + " (" + secs + " sec(s) ago)";
			if (secs < 3600) return df.format(this.endDate) + " (" + Math.floor(secs / 60) + " min(s) ago)";
			if (secs < 86400) return df.format(this.endDate) + " (" + Math.floor(secs / 3600) + " hour(s) ago)";
			if (secs < 604800) return df.format(this.endDate) + " (" +  Math.floor(secs / 86400) + " day(s) ago)";
		}
		
		return df.format(this.endDate);
	}
	
 	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getBlankVotes() {
		return blankVotes;
	}

	public void setBlankVotes(int blankVotes) {
		this.blankVotes = blankVotes;
	}

	public int getNullVotes() {
		return nullVotes;
	}

	public void setNullVotes(int nullVotes) {
		this.nullVotes = nullVotes;
	}
	

}