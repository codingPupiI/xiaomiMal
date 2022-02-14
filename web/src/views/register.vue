<template>
    <div id="register">
        <form :model="form">
            <div class="regist">
                <div class="regist_center">
                    <div class="regist_top">
                        <div class="left fl">用户注册</div>
                        <div class="right fr"><router-link to="/index">小米商城</router-link></div>
                        <div class="clear"></div>
                        <div class="xian center"></div>
                    </div>
                    <div class="regist_main center">
                        <div class="username">昵&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称:&nbsp;&nbsp;<input class="shurukuang" v-model="form.username" type="text" name="username" placeholder="请输入你的昵称"/><span>请输入8位以内字符</span></div>
                        <div class="username">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;&nbsp;<input class="shurukuang" v-model="form.password" type="password" name="password" placeholder="请输入你的密码"/><span>请输入6-16位字符</span></div>

                        <div class="username">确认密码:&nbsp;&nbsp;<input class="shurukuang" type="password" v-model="form.rePassword" name="rePassword" placeholder="请确认你的密码"/><span>两次密码要输入一致哦</span></div>
                        <div class="username">手&nbsp;&nbsp;机&nbsp;&nbsp;号:&nbsp;&nbsp;<input v-model="form.phone" class="shurukuang" type="text" name="phone" placeholder="请填写正确的手机号"/><span>填写下手机号吧，方便我们联系您！</span></div>
                        <div class="username">
                            <div class="left fl">验&nbsp;&nbsp;证&nbsp;&nbsp;码:&nbsp;&nbsp;<input v-model="form.captcha" class="yanzhengma" type="text" name="captcha" placeholder="请输入验证码"/></div>
                            <div class="right fl"><the-captcha :identify-code="form.correctCaptcha"></the-captcha></div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="regist_submit">
                        <input class="submit" type="button" name="submit" @click="register" value="立即注册" >
                    </div>

                </div>
            </div>
        </form>
    </div>
</template>

<script>
    import TheCaptcha from "../components/the-captcha.vue";
    export default {
        name: 'register',
        components: {TheCaptcha},
        data() {
            return {
                form:{
                    username: "",
                    password: "",
                    captcha: "",
                    rePassword: "",
                    phone: "",
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
            register(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/register", _this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.reset();
                    }else{
                        _this.$message.error(resp.msg);
                        _this.generatorCode();
                    }
                });
            },
            reset(){
                let _this = this;
                _this.form.username = "";
                _this.form.password = "";
                _this.form.captcha = "";
                _this.form.rePassword = "";
                _this.form.phone = "";
                _this.form.correctCaptcha = "";
            }
        }
    }

</script>


<style lang="css" scoped src="../../public/static/css/login.css">

</style>
