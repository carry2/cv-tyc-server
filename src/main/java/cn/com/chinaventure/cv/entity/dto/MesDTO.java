package cn.com.chinaventure.cv.entity.dto;

import com.feizhou.swagger.annotation.ApiModelProperty;

import java.util.List;

/**
 * Created by YZP on 2018/4/17.
 */

public class MesDTO {

	@ApiModelProperty(notes = "增量表数组", required = true)
    private List<String>  tables;


    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String>  tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "MesDTO{" +
                "tables=" + tables +
                '}';
    }
}
