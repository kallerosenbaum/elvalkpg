package se.elva.lkpg.twitterdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.infinispan.Cache;
import org.infinispan.lucene.InfinispanDirectory;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;

import twitter4j.Tweet;

public class LuceneStuff {

	private Cache<Object, Object> cache;
	private Directory directory;
	/** The Analyzer used in all methods **/
	private static final Analyzer analyzer = new StandardAnalyzer(
			Version.LUCENE_33);
	private static final String TEXT = "text";
	private static final Version VERSION = Version.LUCENE_33;
 
	{
		IndexWriterConfig.setDefaultWriteLockTimeout(30000);
	}
	
	public LuceneStuff(Cache<Object, Object> cache) {
		this.cache = cache;
		this.directory = new InfinispanDirectory(cache, "tweetIndex");
	}

	public void addNewDocument(Tweet tweet) throws IOException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(VERSION, analyzer);
		IndexWriter iw = new IndexWriter(directory, writerConfig);
		try {
			Document doc = new Document();
			Field field = new Field(TEXT, tweet.getText(), Store.YES, Index.ANALYZED);
			doc.add(field);
			iw.addDocument(doc);
			iw.commit();
		} finally {
			iw.close();
		}
	}

	/**
	 * Runs a Query and returns the stored field for each matching document
	 * 
	 * @throws IOException
	 */
	public List<String> listStoredValuesMatchingQuery(Query query) {
		try {
			IndexReader indexReader = IndexReader.open(directory);
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, null, 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			List<String> list = new ArrayList<String>();
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				list.add(doc.get(TEXT));
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
	public List<String> listAllDocuments() {
		MatchAllDocsQuery q = new MatchAllDocsQuery();
		return listStoredValuesMatchingQuery(q);
	}

	/**
	 * Returns a list of Addresses of all members in the cluster
	 */
	public List<Address> listAllMembers() {
		EmbeddedCacheManager cacheManager = cache.getCacheManager();
		for (String name : cacheManager.getCacheNames()) {
			System.out.println("Cache name " + name);
		}
		return cacheManager.getMembers();
	}
}
