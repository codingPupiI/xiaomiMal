<!-- start header -->
<template>
    <div id="top-menu">
        <header>
            <div class="top center">
                <div class="left fl">
                    <ul>
                        <li><router-link to="/index">小米商城</router-link></li>
                        <div class="clear"></div>
                    </ul>
                </div>
                <div class="right fr">
                    <router-link to="/cart"><div class="gouwuche fr">购物车</div></router-link>
                    <div class="fr">
                        <ul>
                            <template v-if="user.token === ''">
                                <li><router-link to="/login">登录</router-link></li>
                                <li>|</li>
                                <li><router-link to="/register">注册</router-link></li>
                                <li>|</li>
                            </template>
                            <template v-if="user.token !== ''">
                                <li><router-link to="/self-info">欢迎您：<span v-text="user.username"></span>！</router-link></li>
                                <li>|</li>
                                <li><a href="javascript:void(0);" @click="logout">退出登录</a></li>
                            </template>
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </header>
        <!--end header -->

        <!-- start banner_x -->
        <div class="banner_x center">
            <router-link to="/index"><div class="logo fl"></div></router-link>
            <router-link to="/index"><div class="ad_top fl"></div></router-link>
            <div class="nav fl">
                <ul>
                    <li v-for="(category,category_index) in categoryList" :key="category_index"><router-link :to="'/list?categoryId='+category.id" v-text="category.name"></router-link></li>
                </ul>
            </div>
            <div class="search fr">
                <form>
                    <div class="text fl">
                        <input type="text" v-model="search" class="shuru"  placeholder="请输入你要搜索的内容...">
                    </div>
                    <div class="submit fl">
                        <input type="button" @click="searchContent()" class="sousuo" value="搜索"/>
                    </div>
                    <div class="clear"></div>
                </form>
                <div class="clear"></div>
            </div>
        </div>
        <!-- end banner_x -->
    </div>
</template>

<script>
    export default {
        name: 'top-menu',
        props: {
            categoryList: {
                type: Array,
                default: () => {
                    return []
                }
            }
        },
        data() {
            return {
                search: "",
                user: {}
            }
        },
        created() {
            let _this = this;
            let token = Tool.getLoginUser();
            _this.user.token = token;
            if (Tool.isEmpty(token)) {
                _this.user.token = "";
            }else{
                //用token去查询用户数据是否仍存在
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/check_login", {token:token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user = resp.data;
                    }else{
                        _this.user.token = "";
                    }
                });
            }
        },
        mounted:function () {

        },
        methods:{
            searchContent(){
                let _this = this;
                _this.$router.push("/list?search=" + _this.search);
            },
            logout(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/logout", {token:_this.user.token}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.user.token = "";
                        SessionStorage.remove(SESSION_KEY_LOGIN_USER);
                        _this.$message.success(resp.msg);
                        _this.$router.push("/index");
                    }
                });
            }
        }
    }
</script>
