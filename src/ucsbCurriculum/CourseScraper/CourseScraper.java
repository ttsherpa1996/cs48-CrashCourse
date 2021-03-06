package ucsbCurriculum.CourseScraper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import ucsbCurriculum.Utility.Course;
import ucsbCurriculum.Utility.Util;

public class CourseScraper {
    
    private ArrayList<Course> courseList; // store all courses grabbed from website
    
    @Override
    public String toString(){
    	return "stub";
    }
  
    
    public Course get_course_by_name(String courseName){
        for(int i = 0; i < courseList.size(); i++){
        		if(courseList.get(i).get_name() == courseName)
        			return courseList.get(i);
        }
        return null;
    }
	
	// Prints every course department 
	public static void printDepartments() {
		try {
		    String url = "https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx";
		    Connection.Response ucsbCurriculum = Jsoup.connect(url).method(Connection.Method.GET).execute();
		    Document page = ucsbCurriculum.parse();
		    		    
		    Elements subjectAreas = page.select("#ctl00_pageContent_courseList option");
		   
		    for (Element subject : subjectAreas) {
	    		System.out.println(subject.text());
		    }

		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	// courseDepartment: Input the abbreviated subject area of the class you are searching for.
	//				Examples: Computer Science -> "CMPSC", Black Studies -> "BL ST", Music -> "MUS"
	// 				Run printDepartments() to see every department and its abbreviation printed out.
	//
	// quarter: Formatting is as follows "(year)(quarter#)". For example, "20172" = Spring 2017. 
	//			1 = Winter, 2 = Spring, 3 = Summer, and 4 = Fall
	// 			(based on when quarter begins within calendar year)
	// 			More examples: "20143" = Summer 2014, "20691" = Winter 2069
	//
	// courseLevel: "Undergraduate", "Graduate", or "All" (only 3 options, and first letter should be capitalized)
	public void getCourseListFor(String courseDepartment, String quarter, String courseLevel) {
		try {
		    String url = "https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx";
		    Connection.Response ucsbCurriculum = Jsoup.connect(url).method(Connection.Method.GET).execute();
		    Document page = ucsbCurriculum.parse();
		    
		    Element e = page.select("input[id=__VIEWSTATE]").first();
		    String viewState = e.attr("value");
		    e = page.select("input[id=__VIEWSTATEGENERATOR]").first();
		    String viewStateGenerator= e.attr("value");
		    e = page.select("input[id=__EVENTVALIDATION]").first();
		    String eventValidation = e.attr("value");
		    
		    ucsbCurriculum = Jsoup.connect(url)
		    		.data("__VIEWSTATE", viewState)
		    		.data("__VIEWSTATEGENERATOR", viewStateGenerator)
		    		.data("__EVENTVALIDATION", eventValidation)
		    		.data("ctl00$pageContent$courseList", courseDepartment.toUpperCase())
		    		.data("ctl00$pageContent$quarterList", quarter)
		    		.data("ctl00$pageContent$dropDownCourseLevels", courseLevel)
		    		.data("ctl00$pageContent$searchButton.x", "99")
		    		.data("ctl00$pageContent$searchButton.y", "99")
		    		.cookies( ucsbCurriculum.cookies() )
		    		.maxBodySize(0)
		    	    .timeout(60000)
		    		.method(Connection.Method.POST)
		    		.execute();
		    
		    page = ucsbCurriculum.parse(); 
		    
		    Course c = null;
		    Elements courseRow = page.select("tr.CourseInfoRow");
		    for (Element row : courseRow) 
		    {
		    		Elements Professors = row.select("td:nth-child(6)");
		    		Elements courseID = row.select("td:nth-child(2)");
		    		Elements courseTitle = row.select("td:nth-child(3)");
		    		Elements Days = row.select("td:nth-child(7)");
		    		Elements Time = row.select("td:nth-child(8)");
		    		Elements Location = row.select("td:nth-child(9)");
		    		
		    		String id = courseID.text().split("Click")[0];
		    		String title = courseTitle.text().split("Click")[0];
		    		String professorName = Professors.text();
		    		String day = Days.text();
		    		String time = Time.text();
		    		String location = Location.text();
		    		String formatted;
		    		
		    		if (professorName.length() > 1 && title.length() > 1) 
		    		{
		    			if(c != null)
		    				courseList.add(c);
		    			c = new Course(title, location, professorName);
		    			
		    			for(int i = 0; i < day.length(); i++)
		    				if(Character.isLetter(day.charAt(i)))
		    				{
		    					c.add_lectureTime(Util.converts_to_minutes(""+day.charAt(i), time));
		    				}
		    			
//		    			formatted = id + ": " + title + "// " + professorName + ", " + 
//		    						day + " @ " + time + ", " + location;
//	
//		    			System.out.println(formatted);
		    		} 
		    		else 
		    		{
		    			
		    			for(int i = 0; i < day.length(); i++)
		    				if(Character.isLetter(day.charAt(i)))
		    				{
		    					c.add_sectionTime(Util.converts_to_minutes(""+day.charAt(i), time));
		    				}
		    			
		    			
//		    			String tab = "";
//		    			for (int i = 0; i < id.length()+2; i++) {
//		    				tab += " ";
//		    			}
//		    			
//		    			formatted = tab + day + " @ " + time + ", " + location;
//		    			System.out.println(formatted);	
		    		}
		    }
		    courseList.add(c);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		getCourseListFor("CMPSC", "20172", "Undergraduate");
//		printDepartments();
	}
}
