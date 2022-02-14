package com.lyf.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyf.programmer.dao.CategoryMapper;
import com.lyf.programmer.dao.ProductMapper;
import com.lyf.programmer.dao.my.MyCategoryMapper;
import com.lyf.programmer.bean.CodeMsg;
import com.lyf.programmer.domain.Category;
import com.lyf.programmer.domain.CategoryExample;
import com.lyf.programmer.domain.Product;
import com.lyf.programmer.domain.ProductExample;
import com.lyf.programmer.dto.CategoryDTO;
import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ProductDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.ICategoryService;
import com.lyf.programmer.service.IProductService;
import com.lyf.programmer.util.CommonUtil;
import com.lyf.programmer.util.CopyUtil;
import com.lyf.programmer.util.UuidUtil;
import com.lyf.programmer.util.ValidateEntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 11:12
 */
@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private IProductService productService;

    @Resource
    private MyCategoryMapper myCategoryMapper;

    /**
     * 分页获取商品分类数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<CategoryDTO>> getCategoryListByPage(PageDTO<CategoryDTO> pageDTO) {
        CategoryExample categoryExample = new CategoryExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            categoryExample.createCriteria().andNameLike("%"+pageDTO.getSearchContent()+"%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        categoryExample.setOrderByClause("sort desc");
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出商品分类数据
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<CategoryDTO> categoryDTOList = CopyUtil.copyList(categoryList, CategoryDTO.class);
        pageDTO.setList(categoryDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存商品分类数据(添加、修改)
     * @param categoryDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveCategory(CategoryDTO categoryDTO) {
        if(categoryDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(categoryDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Category category = CopyUtil.copy(categoryDTO, Category.class);
        if(CommonUtil.isEmpty(category.getId())){
            // id为空 说明是添加数据
            // 判断商品分类名称是否存在
            if(isCategoryNameExist(category, "")){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_NAME_EXIST);
            }
            // 生产8位id
            category.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(categoryMapper.insertSelective(category) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断商品分类名称是否存在
            if(isCategoryNameExist(category, category.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_NAME_EXIST);
            }
            // 修改数据库中数据
            if(categoryMapper.updateByPrimaryKeySelective(category) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除商品分类数据
     * @param categoryDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeCategory(CategoryDTO categoryDTO) {
        if(categoryDTO == null || CommonUtil.isEmpty(categoryDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除此分类下所有商品信息
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andCategoryIdEqualTo(categoryDTO.getId());
        List<Product> productList = productMapper.selectByExample(productExample);
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        for(ProductDTO productDTO : productDTOList){
            productService.removeProduct(productDTO);
        }
        // 删除商品分类信息
        if(categoryMapper.deleteByPrimaryKey(categoryDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取所有商品分类数据
     * @return
     */
    @Override
    public ResponseDTO<List<CategoryDTO>> getAllCategoryList() {
        List<Category> categoryList = categoryMapper.selectByExample(new CategoryExample());
        List<CategoryDTO> categoryDTOList = CopyUtil.copyList(categoryList, CategoryDTO.class);
        return ResponseDTO.success(categoryDTOList);
    }

    /**
     * 前台获取推荐商品分类
     * @return
     */
    @Override
    public ResponseDTO<List<CategoryDTO>> allCategory() {
        // 获取十条商品分类数据 按排序高到低获取
        PageDTO<Category> categoryPageDTO = new PageDTO<>();
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort desc");
        categoryPageDTO.setPage(1);
        categoryPageDTO.setSize(10);
        PageHelper.startPage(categoryPageDTO.getPage(), categoryPageDTO.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        List<CategoryDTO> categoryDTOList = CopyUtil.copyList(categoryList, CategoryDTO.class);
        for(CategoryDTO categoryDTO : categoryDTOList){
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andCategoryIdEqualTo(categoryDTO.getId());
            productExample.setOrderByClause("create_time desc");
            // 每个商品分类获取6条商品数据
            PageDTO<Product> productPageDTO = new PageDTO<>();
            productPageDTO.setPage(1);
            productPageDTO.setSize(6);
            PageHelper.startPage(productPageDTO.getPage(), productPageDTO.getSize());
            List<Product> productList = productMapper.selectByExample(productExample);
            List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
            categoryDTO.setProductDTOList(productDTOList);
        }
        return ResponseDTO.success(categoryDTOList);
    }

    /**
     * 根据id获取商品分类
     * @param categoryDTO
     * @return
     */
    @Override
    public ResponseDTO<CategoryDTO> getById(CategoryDTO categoryDTO) {
        if(CommonUtil.isEmpty(categoryDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 根据id获取商品分类信息
        Category category = categoryMapper.selectByPrimaryKey(categoryDTO.getId());
        return ResponseDTO.success(CopyUtil.copy(category, CategoryDTO.class));
    }

    /**
     * 获取五个成交额最高的商品分类
     * @return
     */
    @Override
    public ResponseDTO<List<CategoryDTO>> getCategoryListByPrice() {
        return ResponseDTO.success(myCategoryMapper.getCategoryListByPrice());
    }

    /**
     * 判断商品分类名称是否重复
     * @param category
     * @param id
     * @return
     */
    public Boolean isCategoryNameExist(Category category, String id) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andNameEqualTo(category.getName());
        List<Category> selectedCategoryList = categoryMapper.selectByExample(categoryExample);
        if(selectedCategoryList != null && selectedCategoryList.size() > 0) {
            if(selectedCategoryList.size() > 1){
                return true; //出现重名
            }
            if(!selectedCategoryList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }
}
