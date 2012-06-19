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

	private String subjects;

	private Cache<String, String> cache;

	public SubjectController() {
	}

	@Inject
	SubjectController(CacheCreator cacheCreator) {
		this.cacheCreator = cacheCreator;
	}

	@PostConstruct
	void postConstruct() {
		cache = cacheCreator.getTweetMaxIdCache();
		addSubjectToDB();
	}

	public void addSubject() {
		if (subjectIsEntered()) {
			addSubjectToDB();
			newSubject = "";
		}
	}

	private void addSubjectToDB() {
		subjects = getSubjectsFromDb();
		if (subjectAlreadyExists()) {
			return;
		}

		if (!subjects.isEmpty()) {
			subjects += ",";
		}
		subjects += newSubject;
		cache.put(CacheKeys.TWITTER_SUBJECTS, subjects);
	}

	private boolean subjectAlreadyExists() {
		String[] subjectArray = subjects.split(",");
		for (String subject : subjectArray) {
			if (subject.equals(newSubject)) {
				return true;
			}
		}
		return false;
	}

	String getSubjects() {
		return subjects;
	}

	private String getSubjectsFromDb() {
		String subjectsFromDb = cache.get(CacheKeys.TWITTER_SUBJECTS);
		if (subjectsFromDb != null) {
			return subjectsFromDb;
		}
		return "";
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

}