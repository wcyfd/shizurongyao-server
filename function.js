/**
 * 购买需要花费的银币数量
 * 
 * @param type
 *            0白 1蓝 2橙 3紫
 * @param count
 *            购买次数
 * 
 * @param buyType
 *            购买方式1银币,2人民币
 * @returns
 */
function getCostMoney(type, count, buyType) {
	if(buyType == 2){
		return 648;
	}
	var baseCount = 0;
	if (type == 0) {
		// 白
		baseCount = 20;
	} else if (type == 1) {
		// 蓝
		baseCount = 120;
	} else if (type == 2) {
		// 橙
		baseCount = 600;
	} else if (type == 3) {
		// 紫
		baseCount = 2700;
	}
	return Math.ceil(baseCount * Math.pow(1.4, (count - 1)));
}

/**
 * 刷新所需要的银币数量
 */
function refreshMarketNeedMoney(refreshCount) {
	var freeCount =5;
	if (refreshCount < freeCount)
		return 0;

	return 200;
}

function getMarketFreeRefreshMaxCount() {
	var i = 5;
	return i;
}

function getMarketItemCount(){
	return 4;
}

/**
 * 掠夺后奖励的积分
 * 
 * @gameResult 1 胜利,2 失败,3 平局
 */
function getPillagePoint(gameResult){
	var result = 0;
	
	if(gameResult==1){
		result = 20;
	}else if(gameResult == 2){
		result = 2;
	}else if(gameResult== 3){
		result = 0;
	}
	return result;
}