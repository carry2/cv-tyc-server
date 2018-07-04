package cn.com.chinaventure.cv.common.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.seg.common.Term;

/**
 * 字符串工具类
 */
public class StringUtil {

	public static Set<String> ListToSortSet(List<String> list) {
		Set<String> set = new LinkedHashSet<>(list);
		return set;

	}

	/**
	 * 将List<Term> 转换为 List<String> 并按顺序组合词库
	 * 	投中，信息，咨询：
	 * 		投中信息
	 * 		投中信息咨询
	 * 		信息咨询
	 * @param terms
	 * @return
	 */
	public static List<String> getStringsByTerms(List<Term> terms) {
		List<String> list = new ArrayList<>();
		boolean flag = true;
		for (Term term : terms) {
			
			if(term.toString().length() == 1){
				if(flag){
					flag = false;
					list.add(term.toString());
				}else{
					list.set(list.size() - 1, list.get(list.size() -1 ).concat(term.toString()));
				}
			}else{
				flag = true;
				list.add(term.toString());
			}
		}
		
		List<String> result = new ArrayList<>();
		StringBuffer buffer = null;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String str = list.get(i);
			if(StringUtils.isEmpty(str.trim())){
				continue;
			}
			result.add(str);
			
			buffer = new StringBuffer();
			buffer.append(str);
			for (int k = i + 1; k < size; k++) {
				String ci = list.get(k);
				if(StringUtils.isEmpty(ci.trim())){
					continue;
				}
				buffer.append(ci);
				result.add(buffer.toString());
			}
		}
		return result;
	}
	
	public static List<String> getStringsByBlankSpace(String[] strs) {
		List<String> result = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append(str).append(" ");
			if(str.length() > 1){
				result.add(str.trim());
			}
		}
		result.add(sb.toString().trim());
		return result;
	}

	public static boolean isNullOrEmpty(String str){
		if(null == str || "".equals(str.trim()) || "null".equals(str.trim().toLowerCase())){
			return true;
		}else{
			return false;
		}
	}
}
