package se.elva.lkpg.twitterdemo.web.jsf;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.infinispan.Cache;
import org.junit.Before;
import org.junit.Test;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.CacheKeys;

public class SubjectControllerTest {

	private CacheCreator cacheCreator;
	private Cache<String, String> cache;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		cacheCreator = mock(CacheCreator.class);
		cache = mock(Cache.class);
		when(cacheCreator.getTweetMaxIdCache()).thenReturn(cache);
	}

	@Test
	public void shouldGetSubjectsFromDB() throws Exception {
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("ruby");

		SubjectController subjectController = createSubjectController(cacheCreator);
		subjectController.setNewSubject("java");
		subjectController.addSubject();
		assertEquals("ruby,java", subjectController.getSubjects());
	}

	@Test
	public void shouldStoreSubjectsInDB() throws Exception {
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("", "", "java");

		SubjectController subjectController = createSubjectController(cacheCreator);
		subjectController.setNewSubject("java");
		subjectController.addSubject();
		subjectController.setNewSubject("python");
		subjectController.addSubject();

		verify(cache).put(CacheKeys.TWITTER_SUBJECTS, "java");
		verify(cache).put(CacheKeys.TWITTER_SUBJECTS, "java,python");
	}

	@Test
	public void shouldNotStoreTheSameSubjectTwice() throws Exception {
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("java");

		SubjectController subjectController = createSubjectController(cacheCreator);
		subjectController.setNewSubject("java");
		subjectController.addSubject();

		assertEquals("java", subjectController.getSubjects());
	}

	@Test
	public void shouldNotAddNullSubject() throws Exception {
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("");

		SubjectController subjectController = createSubjectController(cacheCreator);
		subjectController.setNewSubject(null);
		subjectController.addSubject();

		verify(cache, times(0)).put(CacheKeys.TWITTER_SUBJECTS, null);
	}

	@Test
	public void shouldNotAddEmptySubject() throws Exception {
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("");

		SubjectController subjectController = createSubjectController(cacheCreator);
		subjectController.setNewSubject("");
		subjectController.addSubject();

		verify(cache, times(0)).put(CacheKeys.TWITTER_SUBJECTS, "");
	}

	private SubjectController createSubjectController(CacheCreator cacheCreator) {
		SubjectController subjectController = new SubjectController(cacheCreator);
		subjectController.postConstruct();
		return subjectController;
	}
}
