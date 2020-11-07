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
	private String regionId;
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
	 * 上级id
	 */
	private String pid;
	/**
	 * 创建者
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新者
	 */
	private Long updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;

}
