package se.elva.lkpg.twitterdemo.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.infinispan.lucene.InfinispanDirectory;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;

import twitter4j.Tweet;

@Stateless
public class LuceneStuff {
	private static final Logger log = Logger.getLogger(LuceneStuff.class);

	@EJB
	private CacheCreator cacheCreator;

	/** The Analyzer used in all methods **/	
	private static final Analyzer analyzer = new StandardAnalyzer(
			Version.LUCENE_33);
	private static final String TEXT = "text";
	private static final String ID = "id";

	private static final Version VERSION = Version.LUCENE_33;

	private Directory getDirectory() {
		return new InfinispanDirectory(cacheCreator.getIndexCache(), "tweetIndex");
	}

	public void addNewDocument(List<Tweet> tweets) throws IOException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(VERSION, analyzer);
		IndexWriter iw = new IndexWriter(getDirectory(), writerConfig);
		try {
			for (Tweet tweet : tweets) {
				Document doc = new Document();			
				doc.add(new Field(ID, Long.toString(tweet.getId()), Store.YES, Index.ANALYZED));
				doc.add(new Field(TEXT, tweet.getText(), Store.NO, Index.ANALYZED));
				iw.addDocument(doc);
			}
			iw.commit();
		} finally {
			iw.close();
		}
	}

	public List<Long> listStoredValuesMatchingString(String queryString, int limit) {
		Query query;
		try {
			query = parseQuery(queryString);
		} catch (ParseException e) {
			log.error("Couldn't parse query '" + queryString + "'", e);
			return Collections.emptyList();
		}
		return listStoredValuesMatchingQuery(query, limit);
	}
	
	/**
	 * Runs a Query and returns the stored field for each matching document
	 * 
	 * @throws IOException
	 */
	public List<Long> listStoredValuesMatchingQuery(Query query, int limit) {
		try {
			IndexReader indexReader = IndexReader.open(getDirectory());
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, null, limit);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			List<Long> list = new ArrayList<Long>();
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				list.add(Long.parseLong(doc.get(ID)));
			}
			return list;
		} catch (IOException ioe) {
			return Collections.emptyList();
		}
	}

	/**
	 * Parses a query using the single field as default
	 * 
	 * @throws ParseException
	 */
	public Query parseQuery(String queryLine) throws ParseException {
		QueryParser parser = new QueryParser(VERSION, TEXT, analyzer);
		return parser.parse(queryLine);
	}

	/**
	 * Returns a list of the values of all stored fields
	 * 
	 * @throws IOException
	 */
	public List<Long> listAllDocuments() {
		MatchAllDocsQuery q = new MatchAllDocsQuery();
		return listStoredValuesMatchingQuery(q, 100000000);
	}

	/**
	 * Returns a list of Addresses of all members in the cluster
	 */
	public List<Address> listAllMembers() {
		EmbeddedCacheManager cacheManager = cacheCreator.getIndexCache().getCacheManager();
		for (String name : cacheManager.getCacheNames()) {
			log.debug("Cache name " + name);
		}
		return cacheManager.getMembers();
	}
}
