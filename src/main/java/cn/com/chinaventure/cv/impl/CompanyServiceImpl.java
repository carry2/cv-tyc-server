package cn.com.chinaventure.cv.impl;

import cn.com.chinaventure.cv.entity.dto.LawsuitDTO;
import cn.com.chinaventure.cv.entity.vo.JsonResult;
import cn.com.chinaventure.cv.service.CompanyService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by YZP on 2018/5/30.
 */
public class CompanyServiceImpl implements CompanyService {

    /**
     * 根据参数获取公司名称
     * @param lawsuitDTO
     * @return
     */
    @Override
    public String getCompanyName(LawsuitDTO lawsuitDTO) {
        String companyName = null;
        if (lawsuitDTO.getCvId() != null && !StringUtils.isEmpty(lawsuitDTO.getCvId().trim())) {
            /**
             *  此处没有直接用cv_id去查询的场景，而是应该将cv_id转换为company_name再按照如下逻辑实现
             */
            Record company = Db.use("db1_prism1").findFirst("select name from company where cv_id= ?", lawsuitDTO.getCvId());
            if(company != null){
                companyName = company.getStr("name");
            }
        }else{
            companyName = lawsuitDTO.getCompanyName();
        }
        return companyName;
    }
}
