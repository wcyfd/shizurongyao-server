package com.randioo.shizurongyao_server.module.card;

/**
 * 
 * @author wcy 2016年5月26日
 *
 */
public class CardConstant {
	/** 英雄卡 */
	public static final byte CARD_HERO = 0;
	/** 士兵卡 */
	public static final byte CARD_SOLDIER = 1;
	/** 魔法卡 */
	public static final byte CARD_MAGIC = 2;

	/**
	 * 卡片类型
	 */
	public static final byte QUALITY_BAD = 0;
	public static final byte QUALITY_NORMAL = 1;
	public static final byte QUALITY_GOOD = 2;
	public static final byte QUALITY_BEST = 3;

	/** 卡牌属性 */
	public static final String ATTRIBUTE_TRADE = "trade";
	public static final String ATTRIBUTE_LEARN = "learn";
	public static final String ATTRIBUTE_FORCE = "force";
	
	public static final int MAX_CARD_COUNT = 999;
}
