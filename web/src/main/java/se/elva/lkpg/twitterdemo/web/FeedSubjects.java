package se.elva.lkpg.twitterdemo.web;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.CacheKeys;
import twitter4j.TwitterFactory;

/**
 * Servlet implementation class FeedSubjects
 */
@WebServlet("/FeedSubjects")
public class FeedSubjects extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@EJB
	private CacheCreator cacheCreator;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedSubjects() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cache<String, String> cache = cacheCreator.getTweetMaxIdCache();
		String subjects = cache.get(CacheKeys.TWITTER_SUBJECTS);
		if (subjects == null) {
			subjects = "";
		}
		
		String addSubjects = request.getParameter("addSubject");
		String setSubjects = request.getParameter("setSubjects");
		if (addSubjects != null && !"".equals(addSubjects)) {
			if (!"".equals(subjects)) {
				subjects += ",";
			}
			subjects += addSubjects; 
		} else if (setSubjects != null) {
			subjects = setSubjects;
		}
		cache.put(CacheKeys.TWITTER_SUBJECTS, subjects);
		
		Writer writer = response.getWriter();
		writer.write("Subjects: " + subjects);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
