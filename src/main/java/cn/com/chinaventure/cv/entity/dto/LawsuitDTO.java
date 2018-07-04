package cn.com.chinaventure.cv.entity.dto;

import com.feizhou.swagger.annotation.ApiModelProperty;

/**
 * Created by YZP on 2018/5/15.
 * 查询法院判决的DTO
 */
public class LawsuitDTO {


    @ApiModelProperty(notes = "cvId", required = false)
    private String  cvId;

    @ApiModelProperty(notes = "companyName", required = false, example = "上海投中信息咨询股份有限公司")
    private String  companyName;
    
    @ApiModelProperty(notes = "pageIndex", required = false)
    private Integer pageIndex;
    
    @ApiModelProperty(notes = "pageSize", required = false)
    private Integer pageSize;

    public String getCvId() {
        return cvId;
    }

	public void setCvId(String cvId) {
        this.cvId = cvId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "LawsuitDTO [cvId=" + cvId + ", companyName=" + companyName + ", pageIndex=" + pageIndex + ", pageSize="
				+ pageSize + "]";
	}
    
}
