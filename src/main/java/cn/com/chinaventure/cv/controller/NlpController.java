package cn.com.chinaventure.cv.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiModel;
import com.feizhou.swagger.annotation.ApiOperation;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;

import cn.com.chinaventure.cv.common.annotation.RouteBind;
import cn.com.chinaventure.cv.common.util.StringUtil;
import cn.com.chinaventure.cv.entity.dto.HanlpDTO;
import cn.com.chinaventure.cv.entity.vo.JsonResult;

/**
 * Created by YZP on 2018/4/23. Updated by Mark.zhang on 2018/4/25.
 */
@RouteBind("v1/nlp")
@Api(tag = "nlp", description = "天眼查API")
public class NlpController extends Controller {

	private Logger log = LoggerFactory.getLogger(NlpController.class);

	@ApiOperation(url = "v1/nlp/enterprise", tag = "nlp", httpMethod = "post", consumes = "application/json", description = "企业名称分词接口")
	@ApiModel(HanlpDTO.class)
	@Before({ POST.class })
	public void enterprise() {
		String readData = HttpKit.readData(getRequest());
		HanlpDTO dto = FastJson.getJson().parse(readData, HanlpDTO.class);
		String text = dto.getText();
		// 设置返回结果词性不显示
		HanLP.Config.ShowTermNature = false;
		
		if (StringUtils.isEmpty(text.trim())) {
			renderJson(new JsonResult<Void>("1", "text字段不能为空"));
			return;
		}
		// 前端按竖线分割
		String[] split = text.split("\\|");

		List<String> result = new ArrayList<String>();
		List<Term> seg = null;
		String str = null;
		for (String sp : split) {
			
			str = sp.trim();// 自动去除字符串前后空格
			if(str.contains(" ")){
				result.addAll(StringUtil.getStringsByBlankSpace(str.split(" ")));
			} else{
				
				seg = HanLP.segment(str);
				if (seg != null) {
					// 获取连续词分组
					result.addAll(StringUtil.getStringsByTerms(seg));
				}
			}
			result.add(sp); // 英文分词后会将中间的空格去掉，可能组不成传来的原词，故增加原词操作
		}

		// 去重
		Set<String> nlps = StringUtil.ListToSortSet(result);
		log.info("请求参数：{}, 分词结果：{}", dto.toString(), nlps.toString());
		renderJson(new JsonResult<Set<String>>(nlps));
	}

}
