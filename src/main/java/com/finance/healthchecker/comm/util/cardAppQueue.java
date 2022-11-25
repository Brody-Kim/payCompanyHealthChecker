package com.finance.healthchecker.comm.util;

import org.springframework.stereotype.Component;

import java.util.Hashtable;

@Component
public class cardAppQueue {

	private Hashtable hash = new Hashtable();

	public void put(String key, Object object) {
		hash.put(key, object);
	}
	public Object pull(String key) {
		if(this.hash == null || key == null){
			return null;
		}
		if(hash.containsKey(key) == true) {
			 return hash.remove(key);
		} else {
			return null;
		}
	}
	public int size() {
		return this.hash.size();
	}
	public Hashtable getHash() {
		return this.hash;
	}
}
