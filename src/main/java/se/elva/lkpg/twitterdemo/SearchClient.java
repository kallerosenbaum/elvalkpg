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
		Printer.p(storedValues.size() + " matching tweets found:\n");
		int i = 0;
		TweetPrinter printer = new TweetPrinter();
		for (Long id : storedValues) {
			Tweet tweet = tweetCache.get(id);
			if (tweet != null) {
				printer.processTweet(tweet);
			}
		}
	}
	
	private void printTweet(Tweet tweet) {
		String result = tweet.getId() + " :\n" + tweet.getText() + "\n - " + tweet.getFromUser();
		Printer.p(result);
	}

	private void doQuery(Scanner scanner) {
		scanner.nextLine();
		Query query = null;
		while (query == null) {
			Printer.p("Enter a query:");
			String queryLine = scanner.nextLine();
			try {
				query = lucene.parseQuery(queryLine);
			} catch (ParseException e) {
				Printer.p("Wrong syntax in query: " + e.getMessage());
				Printer.p("type it again: ");
			}
		}
		List<Long> listMatches = lucene.listStoredValuesMatchingQuery(query, 100);
		printResult(listMatches);
	}

	private void countAllDocuments() {
		List<Long> listMatches = lucene.listAllDocuments();
		Printer.p("Number of tweets: " + listMatches.size());
	}

	private void listMembers() {
		List<Address> members = lucene.listAllMembers();
		Printer.p("\tmembers:\t" + members);
	}

	private void showOptions() {
		Printer.p("Options:\n" + "\t[1] List cluster members\n"
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
					Printer.p("Invalid option, try again:");
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
					Printer.p("Quit.");
					return;
				default:
					Printer.p("Invalid option.");
				}
				Printer.p("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
