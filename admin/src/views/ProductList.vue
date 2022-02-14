<template>
    <div id="category-list">
        <div style="margin-top: 20px">
            <el-button type="success" @click="openAdd"><i class="el-icon-document-add"></i>添加</el-button>
            <el-button type="warning" @click="openEdit"><i class="el-icon-edit-outline"></i>修改</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入商品名称搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getProductList">搜索</el-button>
        </div>
        <el-table
                ref="multipleTable"
                :data="tableData"
                tooltip-effect="dark"
                style="width: 100%"
                @selection-change="handleSelectionChange">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    prop="id"
                    label="编号"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="name"
                    label="商品名称"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="info"
                    label="商品简介"
                    width="500">
            </el-table-column>
            <el-table-column
                    prop="price"
                    label="商品价格(元)"
                    width="120" align="center">
            </el-table-column>
            <el-table-column
                    label="商品图片"
                    width="110">
                <template slot-scope="scope">
                    <img :src="scope.row.photo|filterPhoto" style="width:100px;height:70px;" />
                </template>
            </el-table-column>
            <el-table-column
                    prop="categoryDTO.name"
                    label="商品分类名称"
                    width="120" align="center">
            </el-table-column>
            <el-table-column
                    prop="stock"
                    label="商品库存(件)"
                    width="120" align="center">
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="上架时间"
                    width="200">
            </el-table-column>
        </el-table>
        <el-pagination
                background
                layout="prev, pager, next"
                :total="pageData.total"
                :page-size="pageData.size"
                :current-page="pageData.page"
                @current-change="changePage"
                style="padding-top:10px;">
        </el-pagination>
        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="商品名称" label-width="120px">
                    <el-input  v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="商品图片" label-width="120px">
                    <input type="file" id="photo-file" style="display:none;" @change="upload">
                        <img :src="form.photo|filterPhoto" id="photo-view" style="width:100px; height:70px;" />
                    <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                </el-form-item>
                <el-form-item label="商品价格" label-width="120px">
                    <el-input-number v-model="form.price" :min="0" :controls="false" :precision="2"></el-input-number>
                </el-form-item>
                <el-form-item label="商品库存" label-width="120px">
                    <el-input-number v-model="form.stock" :min="0" :controls="true" :precision="0"></el-input-number>
                </el-form-item>
                <el-form-item label="商品简介" label-width="120px">
                    <el-input type="textarea" :rows="2" placeholder="请输入商品简介" v-model="form.info"></el-input>
                </el-form-item>
                <el-form-item label="商品所属分类" label-width="120px">
                    <el-select v-model="form.categoryId" placeholder="请选择商品分类">
                        <el-option v-for="(item,index) in categoryList" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="saveProduct">确 定</el-button>
            </div>

        </el-dialog>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                tableData: [],
                multipleSelection: [],
                dialogFormVisible: false,
                title: "",
                form: {
                    id: "",
                    name: "",
                    price: 0,
                    stock: 0,
                    info: "",
                    categoryId: "",
                    photo: "common/no_image.jpg"
                },
                searchContent: "",
                pageData: {
                    page: 1,
                    size: 5,
                    total: 0
                },
                categoryList: []
            }
        },
        methods: {
            handleSelectionChange(val) {
                let _this = this;
                _this.multipleSelection = val;
            },
            openAdd(){
                let _this = this;
                _this.title = "添加商品信息";
                _this.form.id = "";
                _this.dialogFormVisible = true;
            },
            openEdit(){
                let _this = this;
                if(_this.multipleSelection.length !== 1){
                    _this.$message.warning("请选择一条数据进行修改！");
                    return false;
                }
                _this.form = _this.multipleSelection[0];
                _this.title = "修改商品信息";
                _this.dialogFormVisible = true;
            },
            uploadPhoto(){
                $("#photo-file").click();
            },
            upload(){
                let _this = this;
                if($("#photo-file").val() === '')return;
                let config = {
                    headers:{'Content-Type':'multipart/form-data'}
                };
                let formData = new FormData();
                formData.append('photo',document.getElementById('photo-file').files[0]);
                // 普通上传
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/photo/upload_photo", formData, config).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        $("#photo-view").attr('src', process.env.VUE_APP_SERVER + '/photo/view?filename=' + resp.data);
                        _this.form.photo = resp.data;
                        _this.$message.success(resp.msg);
                    }else{
                        _this.$message.error(resp.msg);
                    }
                    $("#photo-file").val("");
                });
            },
            openRemove(){
                let _this = this;
                if(_this.multipleSelection.length !== 1){
                    _this.$message.warning("请选择一条数据进行删除！");
                    return false;
                }
                _this.form.id = _this.multipleSelection[0].id;
                this.$confirm('确定要删除编号为：' + _this.form.id + ' 的记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    _this.removeProduct();
                });
            },
            removeProduct(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/product/remove", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getProductList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            saveProduct(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/product/save", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.dialogFormVisible = false;
                        _this.$message.success(resp.msg);
                        _this.getProductList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getProductList(){
                let _this = this;
                let data = {
                    searchContent: _this.searchContent,
                    page: _this.pageData.page
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/product/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.tableData = resp.data.list;
                        _this.pageData.page = resp.data.page;
                        _this.pageData.size = resp.data.size;
                        _this.pageData.total = resp.data.total;
                    }
                });
            },
            getCategoryList(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/category/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.categoryList = resp.data;
                    }
                });
            },
            changePage(currentPage){
                let _this = this;
                _this.pageData.page = currentPage;
                _this.getProductList();
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            }
        },
        mounted(){
            let _this = this;
            _this.getProductList();
            _this.getCategoryList();
        }
    }
</script>
