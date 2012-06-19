package se.elva.lkpg.twitterdemo.web.jsf;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.infinispan.Cache;
import org.junit.Ignore;
import org.junit.Test;

import se.elva.lkpg.twitterdemo.common.CacheCreator;
import se.elva.lkpg.twitterdemo.common.CacheKeys;

public class SubjectControllerTest {

	@Test
	public void shouldGetSubjectsFromDB() throws Exception {
		CacheCreator cacheCreator = mock(CacheCreator.class);
		SubjectController subjectController = new SubjectController(cacheCreator);

		@SuppressWarnings("unchecked")
		Cache<String, String> cache = mock(Cache.class);
		when(cacheCreator.getTweetMaxIdCache()).thenReturn(cache);
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn("ruby");

		subjectController.postConstruct();
		subjectController.setNewSubject("java");
		subjectController.addSubject();
		assertEquals(
				"Adding subject \"java\"! Now following subjects are followed: ruby,java",
				subjectController.getResponse());
	}

	@Ignore
	@Test
	public void shouldStoreSubjectsInDB() throws Exception {
		CacheCreator cacheCreator = mock(CacheCreator.class);
		SubjectController subjectController = new SubjectController(cacheCreator);

		@SuppressWarnings("unchecked")
		Cache<String, String> cache = mock(Cache.class);
		when(cacheCreator.getTweetMaxIdCache()).thenReturn(cache);
		String subjects = "";
		when(cache.get(CacheKeys.TWITTER_SUBJECTS)).thenReturn(subjects);
		// when(cache.put(any(String.class))).then;

		subjectController.postConstruct();
		subjectController.setNewSubject("java");
		subjectController.addSubject();
		subjectController.setNewSubject("python");

		assertEquals(
				"Adding subject \"java\"! Now following subjects are followed: java,python",
				subjectController.getResponse());
	}
}
