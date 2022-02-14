<template>
    <div id="self-info">
        <top-menu :category-list="categoryList"></top-menu>

        <el-dialog title="修改信息" :visible.sync="dialogFormVisible">
            <el-form>
                <el-form-item label="昵称" label-width="120px">
                    <el-input v-model="user.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码" label-width="120px">
                    <el-input type="password" v-model="user.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="手机号" label-width="120px">
                    <el-input v-model="user.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户性别" label-width="120px">
                    <el-select v-model="user.sex" placeholder="请选择用户性别">
                        <el-option label="男" :value="1"></el-option>
                        <el-option label="女" :value="2"></el-option>
                        <el-option label="未知" :value="3"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="updateUserInfo">保 存</el-button>
            </div>
        </el-dialog>


        <div class="grzxbj">
            <div class="selfinfo center">
                <div class="lfnav fl">
                    <div class="ddzx">个人中心</div>
                    <div class="subddzx">
                        <ul>
                            <li><router-link to="/self-order">我的订单</router-link></li>
                            <li><router-link to="/self-info" style="color:#ff6700;font-weight:bold;">我的信息</router-link></li>
                        </ul>
                    </div>
                </div>
                <div class="rtcont fr">
                    <div class="grzlbt ml40">我的资料</div>
                    <input type="file" id="photo-file" style="display:none;" @change="upload">
                    <div><img :src="user.headPic|filterPhoto" style="width:90px;height:60px;padding-left:40px;" />
                    <el-button @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-45px;margin-left:120px;" type="danger" >上传头像</el-button>
                    </div>
                    <div class="subgrzl ml40"><span>昵称</span><span v-text="user.username"></span></div>
                    <div class="subgrzl ml40"><span>密码</span><span>************</span></div>
                    <div class="subgrzl ml40"><span>手机号</span><span v-text="user.phone"></span></div>
                    <div class="subgrzl ml40"><span>性别</span>
                        <span v-if="user.sex === 1">男</span>
                        <span v-else-if="user.sex === 2">女</span>
                        <span v-else>未知</span>
                    </div>
                    <div class="subgrzl ml40"><span>角色</span>
                        <span v-if="user.roleId === 'IvADLevN'">普通用户</span>
                        <span v-else-if="user.roleId === 'SwjNNKK7'">管理员</span>
                    </div>
                    <div style="margin-left:40px;"><el-button type="primary" @click="openEditUser">修改信息</el-button></div>
                </div>

                <div class="clear"></div>
            </div>
        </div>
    </div>
</template>

<script>
    import TopMenu from "../components/top-menu.vue";
    export default {
        name: 'self-info',
        data(){
            return{
                categoryList: [],
                user: {},
                dialogFormVisible: false
            }
        },
        created(){
            let _this = this;
            let token = Tool.getLoginUser();
            _this.user.token = token;
            if (Tool.isEmpty(token)) {
                _this.$router.push("/login");
            }else{
                //用token去查询用户数据是否仍存在
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/check_login", {token:token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user = resp.data;
                        _this.getCategoryList();
                    }else{
                        _this.$router.push("/login");
                    }
                });
            }
        },
        components:{TopMenu},
        methods:{
            openEditUser(){
                let _this = this;
                _this.dialogFormVisible = true;
            },
            getCategoryList(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/category/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.categoryList = resp.data;
                    }
                });
            },
            updateUserInfo(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/update", _this.user).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.dialogFormVisible = false;
                        _this.$router.push("/self-info");
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
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
                        _this.user.headPic = resp.data;
                        _this.$message.success(resp.msg);
                        _this.updateUserInfo();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                    $("#photo-file").val("");
                });
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            }
        }
    }
</script>