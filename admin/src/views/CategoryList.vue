<template>
    <div id="category-list">
        <div style="margin-top: 20px">
            <el-button type="success" @click="openAdd"><i class="el-icon-document-add"></i>添加</el-button>
            <el-button type="warning" @click="openEdit"><i class="el-icon-edit-outline"></i>修改</el-button>
            <el-button type="danger" @click="openRemove"><i class="el-icon-delete"></i>删除</el-button>
            <el-input style="width:200px;height:50px;padding-left: 20px;" v-model="searchContent" placeholder="输入商品分类名称搜索..."/>
            <el-button style="margin-left: 5px;" type="primary" @click="getCategoryList">搜索</el-button>
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
                    width="300">
            </el-table-column>
            <el-table-column
                    prop="sort"
                    label="排序"
                    width="300">
            </el-table-column>
            <el-table-column
                    prop="name"
                    label="商品分类名称"
                    show-overflow-tooltip>
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
                <el-form-item label="商品分类名称" label-width="120px">
                    <el-input  v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="商品分类排序" label-width="120px">
                    <el-input-number v-model="form.sort" :min="0" :controls="true" :precision="0"></el-input-number>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="saveCategory">确 定</el-button>
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
                form: {
                    id: "",
                    name: "",
                    sort: 0
                },
                title: "",
                searchContent: "",
                pageData: {
                    page: 1,
                    size: 5,
                    total: 0
                }
            }
        },
        methods: {
            handleSelectionChange(val) {
                let _this = this;
                _this.multipleSelection = val;
            },
            openAdd(){
                let _this = this;
                _this.form.id = "";
                _this.title = '添加商品分类信息';
                _this.dialogFormVisible = true;
            },
            openEdit(){
                let _this = this;
                if(_this.multipleSelection.length !== 1){
                    _this.$message.warning("请选择一条数据进行修改！");
                    return false;
                }
                _this.title = '修改商品分类信息';
                _this.form = _this.multipleSelection[0];
                _this.dialogFormVisible = true;
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
                    _this.removeCategory();
                });
            },
            removeCategory(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/category/remove", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getCategoryList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            saveCategory(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/category/save", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.dialogFormVisible = false;
                        _this.$message.success(resp.msg);
                        _this.getCategoryList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getCategoryList(){
                let _this = this;
                let data = {
                    searchContent: _this.searchContent,
                    page: _this.pageData.page
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/admin/category/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.tableData = resp.data.list;
                        _this.pageData.page = resp.data.page;
                        _this.pageData.size = resp.data.size;
                        _this.pageData.total = resp.data.total;
                    }
                });
            },
            changePage(currentPage){
                let _this = this;
                _this.pageData.page = currentPage;
                _this.getCategoryList();
            }
        },
        mounted(){
            let _this = this;
            _this.getCategoryList();
        }
    }
</script>
