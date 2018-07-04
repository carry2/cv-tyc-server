package cn.com.chinaventure.cv.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.feizhou.swagger.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cn.com.chinaventure.cv.common.annotation.RouteBind;
import cn.com.chinaventure.cv.entity.TransportClientFactory;
import cn.com.chinaventure.cv.entity.dto.LawsuitDTO;
import cn.com.chinaventure.cv.entity.vo.JsonResult;
import cn.com.chinaventure.cv.impl.CompanyServiceImpl;
import cn.com.chinaventure.cv.service.CompanyService;

/**
 * Created by YZP on 2018/5/15.
 */
@RouteBind("v1/api/company")
@Api(tag = "api", description = "法律信息")
public class CompanyController extends Controller {

	private Logger log = LoggerFactory.getLogger(CompanyController.class);
	private CompanyService companyService = new CompanyServiceImpl();

	@ApiOperation(url = "v1/api/company/lawsuit", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索法院判决接口比对正文、标题、参与人")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void lawsuit() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.lawsuit.index");
		String type = PropKit.get("es.lawsuit.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("plaintext", companyName))
					.should(QueryBuilders.matchPhraseQuery("party", companyName))
					.should(QueryBuilders.matchPhraseQuery("title", companyName));
			// QueryBuilder boolQueryBuilder =
			// QueryBuilders.multiMatchQuery(companyName, "party", "title",
			// "plaintext")
			// .type(MultiMatchQueryBuilder.Type.PHRASE).slop(0);
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "submittime");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/announcement", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索法院公告接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void announcement() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.announcement.index");
		String type = PropKit.get("es.announcement.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("party1", companyName))
					.should(QueryBuilders.matchPhraseQuery("party2", companyName))
					.should(QueryBuilders.matchPhraseQuery("content", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false, "desc", "publishdate");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/notices", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索开庭公告接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void notices() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.notices.index");
		String type = PropKit.get("es.notices.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("plaintiff", companyName))
					.should(QueryBuilders.matchPhraseQuery("defendant", companyName))
					.should(QueryBuilders.matchPhraseQuery("litigant", companyName))
					.should(QueryBuilders.matchPhraseQuery("content", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false);
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/dishonestinfo", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索失信人接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void dishonestinfo() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.dishonestinfo.index");
		String type = PropKit.get("es.dishonestinfo.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("iname", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "regDate");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/zhixinginfo", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索被执行人接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void zhixinginfo() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.zhixinginfo.index");
		String type = PropKit.get("es.zhixinginfo.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("pname", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "caseCreateTime");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/tax", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索欠税信息公告接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void tax() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.tax.index");
		String type = PropKit.get("es.tax.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("name", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "publish_date");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/bid", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索招投标信息接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void bid() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.bid.index");
		String type = PropKit.get("es.bid.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("proxy", companyName))
					.should(QueryBuilders.matchPhraseQuery("purchaser", companyName))
					.should(QueryBuilders.matchPhraseQuery("title", companyName))
					.should(QueryBuilders.matchPhraseQuery("content", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false);
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/land", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索购地情况接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void land() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.land.index");
		String type = PropKit.get("es.land.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("assignee", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false);
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/tminfo", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索商标信息接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void tminfo() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.tminfo.index");
		String type = PropKit.get("es.tminfo.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicant_cn", companyName));
			;
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "app_date");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/patentinfo", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索专利信息接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void patentinfo() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.patentinfo.index");
		String type = PropKit.get("es.patentinfo.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicantname", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false, "desc", "pubDate");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/copyrightreg", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索软件著作权接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void copyrightreg() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.copyrightreg.index");
		String type = PropKit.get("es.copyrightreg.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("author_nationality", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true,"desc","regTime");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/copyrightworks", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索作品著作权接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void copyrightworks() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.copyrightworks.index");
		String type = PropKit.get("es.copyrightworks.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("author", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, true, "desc", "regTime");
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/certificate", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索资质证书-mongo接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void certificate() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.certificate.index");
		String type = PropKit.get("es.certificate.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("company_name", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false);
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}

	@ApiOperation(url = "v1/api/company/bond", tag = "api", httpMethod = "post", consumes = "application/json", description = "ES检索债券信息接口")
	@ApiModel(LawsuitDTO.class)
	@Before({ POST.class })
	public void bond() {
		Date begin = new Date();
		// 获取参数
		LawsuitDTO dto = this.parseParams();
		// 获取索引
		String index = PropKit.get("es.bond.index");
		String type = PropKit.get("es.bond.type");
		String companyName = companyService.getCompanyName(dto);
		if (companyName != null) {
			// 如果cv_id为空 以companyName为准 根据公司名模糊查询 定义查询规则
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("publisher_name", companyName));
			// 分页查询结果 返回
			this.commonHandle(dto, index, type, boolQueryBuilder, false);
			log.debug("查询企业名称：{},请求参数：{}, 耗时：{}", companyName, dto.toString(),
					System.currentTimeMillis() - begin.getTime());
		} else {
			renderJson(new JsonResult<Void>("1", "没有找到对应的cv_id"));
		}
	}
	
	private void commonHandle(LawsuitDTO dto, String index, String type, QueryBuilder queryBuilder, Boolean isOrder) {
		commonHandle(dto, index, type, queryBuilder, isOrder, null, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param dto
	 * @param index
	 * @param type
	 * @param queryBuilder
	 */
	private void commonHandle(LawsuitDTO dto, String index, String type, QueryBuilder queryBuilder, Boolean isOrder,
			String order, String colmn) {
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();
		TransportClient client = null;
		Map<String, Object> p = new HashMap<>();
		try {
			// 获取TransportClient
			client = TransportClientFactory.getLawsuitInstance();
			// 这样就可以使用client执行查询了
			SearchRequestBuilder srb = client.prepareSearch(index);
			srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
			srb.setTypes(type);

			// srb.setProfile(true);
			if (isOrder) {
				if (order.equals("asc")) {
					srb.addSort(colmn, SortOrder.ASC);
				}
				if (order.equals("desc")) {
					srb.addSort(colmn, SortOrder.DESC);
				}
			}

			// 如果cv_id为空 以companyName为准 根据公司名模糊查询
			srb.setQuery(queryBuilder);
			// 分页查询
			srb.setFrom((pageIndex - 1) * pageSize).setSize(pageSize).setExplain(true);
			log.debug("es 检索条件：\n{} ", srb.toString());
			SearchResponse response = srb.execute().actionGet();

			SearchHits hits = response.getHits();
			List<Map<String, Object>> list = Lists.newArrayList();
			for (SearchHit hit : response.getHits()) {
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				sourceAsMap.put("id", hit.getId());
				list.add(sourceAsMap);
			}
			p.put("pageIndex", pageIndex);
			p.put("pageSize", pageSize);
			p.put("totalCount", (int) hits.getTotalHits());
			int totalPage = 0;
			if (hits.getTotalHits() % pageSize == 0) {
				totalPage = (int) hits.getTotalHits() / pageSize;
			} else {
				totalPage = (int) hits.getTotalHits() / pageSize + 1;
			}
			p.put("totalPage", totalPage);
			p.put("resultData", list);
			p.put("resultDataSize", list.size());
		} catch (Exception e) {
			log.error(e.getMessage());
			renderJson(new JsonResult<Void>("1", "未知错误，请检验参数是否正确"));
			return;
		} finally {
			// if(client != null){
			// client.close();
			// }
		}
		renderJson(new JsonResult<Map<String, Object>>(p));
	}

	/**
	 * 分析请求 转化参数
	 * 
	 * @return LawsuitDTO
	 */
	public LawsuitDTO parseParams() {
		String readData = HttpKit.readData(getRequest());

		LawsuitDTO dto = FastJson.getJson().parse(readData, LawsuitDTO.class);
		if (StringUtils.isEmpty(dto.getCompanyName()) && StringUtils.isEmpty(dto.getCvId())) {
			renderJson(new JsonResult<Void>("1", "参数cvId和companyName不能都为空"));
		}

		if (dto.getPageIndex() == null) {
			dto.setPageIndex(getParaToInt("pageIndex", 1));
		}

		if (dto.getPageSize() == null) {
			dto.setPageSize(getParaToInt("pageSize", 10));
		}
		return dto;
	}

	@ApiOperation(url = "v1/api/company/getLawsuitById", tag = "api", httpMethod = "get", description = "根据ID查询法院判决信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getLawsuitById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db2_prism1").findById("company_lawsuit", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getAnnouncementById", tag = "api", httpMethod = "get", description = "根据ID查询法院公告信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getAnnouncementById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db2_prism1").findById("court_announcement", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getNoticesById", tag = "api", httpMethod = "get", description = "根据ID查询开庭公告信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getNoticesById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db2_prism1").findById("court_notices", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getDishonestinfoById", tag = "api", httpMethod = "get", description = "根据ID查询失信人信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getDishonestinfoById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db4_prism1").findById("dishonestinfo", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getZhixinginfoById", tag = "api", httpMethod = "get", description = "根据ID查询被执行人信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getZhixinginfoById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db4_prism1").findById("zhixinginfo", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getTaxById", tag = "api", httpMethod = "get", description = "根据ID查询欠税信息公告")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getTaxById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db4_prism1").findById("company_own_tax", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getBidById", tag = "api", httpMethod = "get", description = "根据ID查询招投标信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getBidById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db4_prism1").findById("company_bid", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getLandById", tag = "api", httpMethod = "get", description = "根据ID查询购地情况")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getLandById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db4_prism1").findById("company_purchase_land", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getTminfoById", tag = "api", httpMethod = "get", description = "根据ID查询商标信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getTminfoById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db3_tmdatabase").findById("tm_info", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getPatentInfoById", tag = "api", httpMethod = "get", description = "根据ID查询专利信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "String")
	})
	@Before({ GET.class })
	public void getPatentInfoById() {
		String id = getPara("id");
		Record byId = null;
		try {
			byId = Db.use("db3_patent").findById("ent_patent_info", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getCopyrightRegById", tag = "api", httpMethod = "get", description = "根据ID查询软件著作权")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getCopyrightRegById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db3_prism_cq").findById("copyright_reg", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getCopyrightWorksById", tag = "api", httpMethod = "get", description = "根据ID查询作品著作权")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getCopyrightWorksById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db3_prism1").findById("copyright_works", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getCertificateById", tag = "api", httpMethod = "get", description = "根据ID查询资质证书")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getCertificateById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db3_prism_cq").findById("certificate", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}

	@ApiOperation(url = "v1/api/company/getBondById", tag = "api", httpMethod = "get", description = "根据ID查询债券信息")
	@Params({
			@Param(name = "id", description = "id", required = true, dataType = "Long")
	})
	@Before({ GET.class })
	public void getBondById() {
		int id = getParaToInt("id");
		Record byId = null;
		try {
			byId = Db.use("db3_prism_cq").findById("bond", id);
		} catch (Exception e) {
			renderJson(new JsonResult<Void>("1", "数据库连接异常！"));
		}
		renderJson(new JsonResult<>(byId));
	}
}
