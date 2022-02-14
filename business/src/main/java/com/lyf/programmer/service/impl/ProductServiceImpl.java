package com.lyf.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyf.programmer.dao.CartMapper;
import com.lyf.programmer.dao.CategoryMapper;
import com.lyf.programmer.dao.ProductMapper;
import com.lyf.programmer.bean.CodeMsg;
import com.lyf.programmer.domain.CartExample;
import com.lyf.programmer.domain.Category;
import com.lyf.programmer.domain.Product;
import com.lyf.programmer.domain.ProductExample;
import com.lyf.programmer.dto.CategoryDTO;
import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ProductDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.IProductService;
import com.lyf.programmer.util.CommonUtil;
import com.lyf.programmer.util.CopyUtil;
import com.lyf.programmer.util.UuidUtil;
import com.lyf.programmer.util.ValidateEntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 12:26
 */
@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CartMapper cartMapper;

    /**
     * 分页获取商品数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<ProductDTO>> getProductListByPage(PageDTO<ProductDTO> pageDTO) {
        ProductExample productExample = new ProductExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            productExample.createCriteria().andNameLike("%"+pageDTO.getSearchContent()+"%");
        }
        productExample.setOrderByClause("create_time desc");
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出商品数据
        List<Product> productList = productMapper.selectByExample(productExample);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        // 分装商品分类信息
        for(ProductDTO productDTO : productDTOList){
            Category category = categoryMapper.selectByPrimaryKey(productDTO.getCategoryId());
            CategoryDTO categoryDTO = CopyUtil.copy(category, CategoryDTO.class);
            productDTO.setCategoryDTO(categoryDTO);
        }
        pageDTO.setList(productDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存商品数据(添加、修改)
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveProduct(ProductDTO productDTO) {
        if(productDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(productDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Product product = CopyUtil.copy(productDTO, Product.class);
        if(CommonUtil.isEmpty(product.getId())){
            // id为空 说明是添加数据
            // 判断商品名称是否存在
            if(isProductNameExist(product, "")){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NAME_EXIST);
            }
            // 生产8位id
            product.setId(UuidUtil.getShortUuid());
            product.setCreateTime(new Date());
            // 添加数据到数据库
            if(productMapper.insertSelective(product) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断商品名称是否存在
            if(isProductNameExist(product, product.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NAME_EXIST);
            }
            // 修改数据库中数据
            if(productMapper.updateByPrimaryKeySelective(product) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除商品数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeProduct(ProductDTO productDTO) {
        if(productDTO == null || CommonUtil.isEmpty(productDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除属于此商品的购物车信息
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andProductIdEqualTo(productDTO.getId());
        cartMapper.deleteByExample(cartExample);
        // 删除商品信息
        if(productMapper.deleteByPrimaryKey(productDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 根据商品id获取商品
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<ProductDTO> getById(ProductDTO productDTO) {
        if(CommonUtil.isEmpty(productDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 从数据库中根据商品id查询商品信息
        Product product = productMapper.selectByPrimaryKey(productDTO.getId());
        return ResponseDTO.success(CopyUtil.copy(product, ProductDTO.class));
    }

    /**
     * 根据商品分类或者搜索内容获取商品列表数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> getByCategoryOrSearch(ProductDTO productDTO) {
        ProductExample productExample = new ProductExample();
        if(!CommonUtil.isEmpty(productDTO.getName())){
            // 搜索内容不为空
            ProductExample.Criteria c1 = productExample.createCriteria();
            c1.andNameLike("%" + productDTO.getName() + "%");
            ProductExample.Criteria c2 = productExample.createCriteria();
            c2.andInfoLike("%" + productDTO.getName() + "%");
            productExample.or(c2);
        }else if(!CommonUtil.isEmpty(productDTO.getCategoryId())){
            // 分类搜素不为空
            productExample.createCriteria().andCategoryIdEqualTo(productDTO.getCategoryId());
        }
        productExample.setOrderByClause("create_time desc");
        List<Product> productList  = productMapper.selectByExample(productExample);
        return ResponseDTO.success(CopyUtil.copyList(productList, ProductDTO.class));
    }

    /**
     * 获取商品总数
     * @return
     */
    @Override
    public ResponseDTO<BigInteger> getProductTotal() {
        BigInteger total = productMapper.countByExample(new ProductExample());
        return ResponseDTO.success(total);
    }

    /**
     * 获取库存最少的三个商品
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> getProductListByStock() {
        ProductExample productExample = new ProductExample();
        productExample.setOrderByClause("stock asc");
        PageDTO<ProductDTO> pageDTO = new PageDTO<>();
        pageDTO.setPage(1);
        pageDTO.setSize(3);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        List<Product> productList = productMapper.selectByExample(productExample);
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        return ResponseDTO.success(productDTOList);
    }


    /**
     * 判断商品名称是否重复
     * @param product
     * @param id
     * @return
     */
    public Boolean isProductNameExist(Product product, String id) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andNameEqualTo(product.getName());
        List<Product> selectedProductList = productMapper.selectByExample(productExample);
        if(selectedProductList != null && selectedProductList.size() > 0) {
            if(selectedProductList.size() > 1){
                return true; //出现重名
            }
            if(!selectedProductList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }
}
