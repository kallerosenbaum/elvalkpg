package se.elva.lkpg.twitterdemo;

import java.util.List;
import java.util.Scanner;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.infinispan.Cache;
import org.infinispan.remoting.transport.Address;

import twitter4j.Tweet;

public class SearchClient {

	LuceneStuff lucene;
	Cache<Long, Tweet> tweetCache;

	private SearchClient(LuceneStuff luceneStuff, Cache<Long, Tweet> tweetCache) {
		this.lucene = luceneStuff;
		this.tweetCache = tweetCache;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cache<Object, Object> cache = CacheCreator.getIndexCache();
		LuceneStuff lucene = new LuceneStuff(cache);
		SearchClient client = new SearchClient(lucene,
				CacheCreator.getTweetCache());
		client.run();
	}

	private void printResult(List<Long> storedValues) {
		System.out.println(storedValues.size() + " matching tweets found:\n");
		int i = 0;
		for (Long id : storedValues) {
			Tweet tweet = tweetCache.get(id);
			if (tweet != null) {
				System.out.println(tweet);
			}
		}

	}

	private void doQuery(Scanner scanner) {
		scanner.nextLine();
		Query query = null;
		while (query == null) {
			System.out.println("Enter a query:");
			String queryLine = scanner.nextLine();
			try {
				query = lucene.parseQuery(queryLine);
			} catch (ParseException e) {
				System.out.println("Wrong syntax in query: " + e.getMessage());
				System.out.println("type it again: ");
			}
		}
		List<Long> listMatches = lucene.listStoredValuesMatchingQuery(query, 100);
		printResult(listMatches);
	}

	private void countAllDocuments() {
		List<Long> listMatches = lucene.listAllDocuments();
		System.out.println("Number of tweets: " + listMatches.size());
	}

	private void listMembers() {
		List<Address> members = lucene.listAllMembers();
		System.out.println("\tmembers:\t" + members);
	}

	private void showOptions() {
		System.out.println("Options:\n" + "\t[1] List cluster members\n"
				+ "\t[2] Count all documents in index\n"
				+ "\t[4] enter a query\n" + "\t[5] quit");
	}

	private void run() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			showOptions();
			boolean warned = false;
			while (!scanner.hasNextInt()) {
				if (!warned) {
					System.out.println("Invalid option, try again:");
					warned = true;
				}
				scanner.nextLine();
			}
			int result = scanner.nextInt();
			try {
				switch (result) {
				case 1:
					listMembers();
					break;
				case 2:
					countAllDocuments();
					break;
				case 4:
					doQuery(scanner);
					break;
				case 5:
					System.out.println("Quit.");
					return;
				default:
					System.out.println("Invalid option.");
				}
				System.out.println("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
