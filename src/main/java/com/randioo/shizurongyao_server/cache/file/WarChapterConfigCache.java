package com.randioo.shizurongyao_server.cache.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.WarChapterConfig;

public class WarChapterConfigCache {

	private static int minChapterId = 0;
	private static Map<Integer, WarChapterConfig> map = new HashMap<>();
	private static List<Integer> chapterIdList = new ArrayList<>();

	public static void putConfig(WarChapterConfig config) {
		map.put(config.chapterId, config);
		if(minChapterId == 0){
			minChapterId = config.chapterId;
		}
		chapterIdList.add(config.chapterId);
	}

	public static List<Integer> getChapterIdList() {
		return chapterIdList;
	}
	
	public static WarChapterConfig getChapterById(int chapterId){
		return map.get(chapterId);
	}
	
	public static int getMinChapterId(){
		return minChapterId;
	}
}
