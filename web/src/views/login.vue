<template>
    <div id="login">
        <!-- login -->
        <div class="top center">
            <div class="logo center">
                <a href="javascript:void(0);"><router-link to="/index"><img src="/static/image/mistore_logo.png" alt=""></router-link></a>
            </div>
        </div>
        <form :model="form" class="form center">
            <div class="login">
                <div class="login_center">
                    <div class="login_top">
                        <div class="left fl">用户登录</div>
                        <div class="right fr">您还不是我们的用户？<router-link to="/register">立即注册</router-link></div>
                        <div class="clear"></div>
                        <div class="xian center"></div>
                    </div>
                    <div class="login_main center">
                        <div class="username">昵&nbsp;&nbsp;&nbsp;&nbsp;称:&nbsp;<input class="shurukuang" v-model="form.username" type="text" name="username" placeholder="请输入你的昵称"/></div>
                        <div class="username">密&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;<input class="shurukuang" v-model="form.password" type="password" name="password" placeholder="请输入你的密码"/></div>
                        <div class="username">
                            <div class="left fl">验证码:&nbsp;<input class="yanzhengma" v-model="form.captcha" type="text" name="captcha" placeholder="请输入验证码"/></div>
                            <div class="right fl">
                                <the-captcha :identify-code="form.correctCaptcha"></the-captcha>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="login_submit">
                        <input class="submit" type="button" name="submit" @click="login" value="立即登录" >
                    </div>

                </div>
            </div>
        </form>

    </div>
</template>

<script>
    import TheCaptcha from "../components/the-captcha.vue";
    export default {
        name: 'login',
        components: {TheCaptcha},
        data() {
            return {
                form:{
                    username: "",
                    password: "",
                    captcha: "",
                    correctCaptcha: "",
                }
            }
        },
        //mounted 页面渲染完后执行 钩子函数
        mounted:function(){
            let _this = this;
            _this.generatorCode();
        },
        methods:{
            generatorCode(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/captcha/generate_captcha").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.form.correctCaptcha = resp.data;
                    }
                });
            },
            login(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/login", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        Tool.setLoginUser(resp.data.token);
                        _this.$message.success(resp.msg);
                        _this.$router.push("/index");
                    }else{
                        _this.generatorCode();
                        _this.$message.error(resp.msg);
                    }
                });
            }
        }
    }

</script>


<style lang="css" scoped src="../../public/static/css/login.css">

</style>
