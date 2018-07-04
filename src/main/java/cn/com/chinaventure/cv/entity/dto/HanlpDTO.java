package cn.com.chinaventure.cv.entity.dto;

import com.feizhou.swagger.annotation.ApiModelProperty;

/**
 * Created by YZP on 2018/4/17.
 */

public class HanlpDTO {

	@ApiModelProperty(notes = "分词字段", required = true, example = "上海投中信息咨询股份有限公司|投中信息|China Ventrue")
    private String  text;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "HanlpDTO{" +
                "text='" + text + '\'' +
                '}';
    }
}
