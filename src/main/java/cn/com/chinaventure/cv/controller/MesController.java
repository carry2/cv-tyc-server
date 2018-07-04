package cn.com.chinaventure.cv.controller;


import cn.com.chinaventure.cv.common.annotation.RouteBind;
import cn.com.chinaventure.cv.common.thread.MesThread;
import cn.com.chinaventure.cv.entity.dto.MesDTO;
import cn.com.chinaventure.cv.entity.vo.JsonResult;
import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiModel;
import com.feizhou.swagger.annotation.ApiOperation;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by YZP on 2018/4/23. Updated by Mark.zhang on 2018/4/25.
 */
@RouteBind("v1/mes")
@Api(tag = "mes", description = "发送消息")
public class MesController extends Controller {

	private Logger log = LoggerFactory.getLogger(MesController.class);

	@ApiOperation(url = "v1/mes/messages", tag = "mes", httpMethod = "post", consumes = "application/json", description = "发送MQ消息")
	@ApiModel(MesDTO.class)
	@Before({ POST.class })
	public void messages() {
		String readData = HttpKit.readData(getRequest());
		MesDTO dto = FastJson.getJson().parse(readData, MesDTO.class);
		List<String> tables = dto.getTables();
		if (tables.size() == 0) {
			renderJson(new JsonResult<Void>("1", "增量表数组不能为空"));
			return;
		}
		MesThread mesThread = new MesThread();
		mesThread.setTables(tables);
		Thread workerThread = new Thread(mesThread);
		workerThread.start();
		log.info("请求参数：{}", dto.toString());
		renderJson(new JsonResult("获取成功"));
	}

}
