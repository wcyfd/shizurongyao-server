package com.randioo.shizurongyao_server.formatter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.randioo.shizurongyao_server.entity.po.Resource;

public class ResourceFormatter {
	public static String formatResources(Set<Resource> set) {
		StringBuilder sb = new StringBuilder();
		for (Resource res : set) {
			sb.append(res.getResType()).append(",");
			sb.append(res.getItemId()).append(",");
			sb.append(res.getNum()).append(";");
		}

		return sb.toString();

	}

	public static String formatResources(List<Resource> list){
		StringBuilder sb = new StringBuilder();
		for (Resource res : list) {
			sb.append(res.getResType()).append(",");
			sb.append(res.getItemId()).append(",");
			sb.append(res.getNum()).append(";");
		}

		return sb.toString();
	}
	public static String formatResources(Resource res) {
		StringBuilder sb = new StringBuilder();
		sb.append(res.getResType()).append(",");
		sb.append(res.getItemId()).append(",");
		sb.append(res.getNum()).append(";");
		return sb.toString();
	}

	public static Set<Resource> formatResouces(String o) {
		Set<Resource> set = new HashSet<>();
		String[] datas = o.split(";");
		for (String data : datas) {
			String[] s = data.split(",");
			byte resType = Byte.parseByte(s[0]);
			int itemId = Integer.parseInt(s[1]);
			int num = Integer.parseInt(s[2]);

			Resource obj = new Resource();
			obj.setItemId(itemId);
			obj.setNum(num);
			obj.setResType(resType);

			set.add(obj);
		}
		return set;
	}

	/**
	 * 合并资源
	 * 
	 * @param addResSet
	 * @param targetResSet
	 * @author wcy 2016年8月9日
	 */
	public static void combineResource(Set<Resource> addResSet, Set<Resource> targetResSet) {
		for (Resource res : addResSet) {
			boolean hasExist = false;
			for (Resource current : targetResSet) {
				if (res.getResType() == current.getResType() && res.getItemId() == current.getItemId()) {
					hasExist = true;
					int resNum = res.getNum();
					current.setNum(current.getNum() + resNum);
				}
			}
			if (!hasExist) {
				targetResSet.add(res);
			}
		}
	}

	/**
	 * 合并资源
	 * 
	 * @param addResSet
	 * @param targetResSet
	 * @author wcy 2016年8月9日
	 */
	public static void combineResource(String addResSet, Set<Resource> targetResSet) {
		if (addResSet == null)
			return;
		Set<Resource> set = formatResouces(addResSet);
		combineResource(set, targetResSet);
	}
}
