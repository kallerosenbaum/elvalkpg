package se.elva.lkpg.twitterdemo;

import org.infinispan.Cache;

public class InfinispanTest {
	
	
	
	public static void main(String[] args) {
		Cache<Object, Object> c = CacheCreator.getTweetCache();

//		c.addListener(new CacheListener());
//		int size = Integer.parseInt(args[0]);
//		int start = size * 100;
	//	p("Cache contains " + c.size() + " elements");
		
/*		p("Adding elements [" + start + "," + (start+99) + "]");
		for (int i = start; i < start + 100; i++) {
			c.put(i, "" + i);
		}*/
		while (true) {
			p("Cache contains " + c.size() + " elements");
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
			}
		}
	}

	private static void p(String string) {
		System.out.println(string);
	}
}
