package io.renren.modules.region.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 行政区表
 * 
 * @author zk
 * @email zk@gmail.com
 * @date 2020-11-03 16:42:54
 */
@Data
@TableName("sys_region")
public class SysRegionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 行政区编号
	 */
	private String regionCode;
	/**
	 * 行政区名称
	 */
	private String regionName;
	/**
	 * 类型1.省级 2.市级 3.县区 4.镇 5.村
	 */
	private Integer regionType;
	/**
	 * 城乡分类标识111主城区 112城乡结合区 121镇中心区 122镇乡结合区 123特殊区域 210乡中心区 220村庄
	 */
	private String countyType;
	/**
	 * 上级code
	 */
	private String parentCode;
	/**
	 * 全部上级
	 */
	private String parentPath;
	/**
	 * 省级名称
	 */
	private String proviceName;
	/**
	 * 市级名称
	 */
	private String cityName;
	/**
	 * 区县级名称
	 */
	private String countyName;
	/**
	 * 乡镇级名称
	 */
	private String townName;
	/**
	 * 村级名称
	 */
	private String villageName;
	/**
	 * 经度
	 */
	private String longitude;
	/**
	 * 纬度
	 */
	private String latitude;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date createDate;
	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateDate;

}
