package se.elva.lkpg.twitterdemo.web.jsf;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.Cache;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.CacheKeys;

@Named
@RequestScoped
public class SubjectController {

	private CacheCreator cacheCreator;

	private String newSubject;

	private String response;

	private String subjects;

	private Cache<String, String> cache;

	public SubjectController() {
	}

	@Inject
	SubjectController(CacheCreator cacheCreator) {
		this.cacheCreator = cacheCreator;
	}

	@PostConstruct
	public void postConstruct() {
		cache = cacheCreator.getTweetMaxIdCache();
		addSubjectToDB();
	}

	public void addSubject() {
		if (subjectIsEntered()) {
			addSubjectToDB();
			response = "Adding subject \"" + newSubject
					+ "\"! Now following subjects are followed: " + subjects;
			newSubject = "";
		} else {
			response = "Please add subject!";
		}
	}

	private void addSubjectToDB() {
		subjects = getSubjects();
		if (subjects == null) {
			subjects = "";
		}

		// if (subjectAlreadyExists()) {
		// return;
		// }

		if (newSubject != null && !"".equals(newSubject)) {
			if (!"".equals(subjects)) {
				subjects += ",";
			}
			subjects += newSubject;
		}
		cache.put(CacheKeys.TWITTER_SUBJECTS, subjects);
	}

	private boolean subjectAlreadyExists() {
		String[] subjectArray = subjects.split(",");
		for (String subject : subjectArray) {
			if (newSubject.equals(subject)) {
				return true;
			}
		}
		return false;
	}

	private String getSubjects() {
		return cache.get(CacheKeys.TWITTER_SUBJECTS);
	}

	public void clearSubjects() {
		cache.put(CacheKeys.TWITTER_SUBJECTS, "");
	}

	private boolean subjectIsEntered() {
		return newSubject != null && !newSubject.isEmpty();
	}

	public String getNewSubject() {
		return newSubject;
	}

	public void setNewSubject(String newSubject) {
		this.newSubject = newSubject;
	}

	public String getResponse() {
		return response;
	}

}