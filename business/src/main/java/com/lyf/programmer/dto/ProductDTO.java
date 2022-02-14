package com.lyf.programmer.dto;

import com.lyf.programmer.annotation.ValidateEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 12:21
 */
public class ProductDTO {

    private String id;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=32,minLength=1,errorRequiredMsg="商品名称不能为空！",errorMaxLengthMsg="商品名称长度不能大于32！",errorMinLengthMsg="商品名称不能为空！")
    private String name;

    private String photo;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999.99,minValue=0.00,errorRequiredMsg="商品价格不能为空！",errorMaxValueMsg="商品价格不能大于99999999.99元！",errorMinValueMsg="商品价格不能小于0.00元！")
    private BigDecimal price;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=128,minLength=1,errorRequiredMsg="商品简介不能为空！",errorMaxLengthMsg="商品简介长度不能大于128！",errorMinLengthMsg="商品简介不能为空！")
    private String info;

    @ValidateEntity(required=true,requiredMinLength=true,minLength=1,errorRequiredMsg="商品所属分类不能为空！",errorMinLengthMsg="商品所属分类不能为空！")
    private String categoryId;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999,minValue=0,errorRequiredMsg="商品库存不能为空！",errorMaxValueMsg="商品库存不能大于99999999！",errorMinValueMsg="商品库存不能为小于0！")
    private Integer stock;

    private CategoryDTO categoryDTO;

    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CategoryDTO getCategoryDTO() {
        return categoryDTO;
    }

    public void setCategoryDTO(CategoryDTO categoryDTO) {
        this.categoryDTO = categoryDTO;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", photo=").append(photo);
        sb.append(", price=").append(price);
        sb.append(", info=").append(info);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", createTime=").append(createTime);
        sb.append(", categoryDTO=").append(categoryDTO);
        sb.append(", stock=").append(stock);
        sb.append("]");
        return sb.toString();
    }
}
