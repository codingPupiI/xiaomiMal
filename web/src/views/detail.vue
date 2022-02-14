<template>
    <div id="detail">

        <top-menu :category-list="categoryList"></top-menu>

        <!-- xiangqing -->
        <form>
            <div class="xiangqing">
                <div class="neirong w">
                    <div class="xiaomi6 fl" style="width: 600px;" v-text="product.name"></div>
                    <div class="clear"></div>
                </div>
            </div>

            <div class="jieshao mt20 w">
                <div class="left fl"><img :src="photo" style="width:560px;height:560px;"></div>
                <div class="right fr">
                    <div class="h3 ml20 mt20" v-text="product.name"></div>
                    <div class="jianjie mr40 ml20 mt10" v-text="product.info"></div>
                    <div class="jiage ml20 mt10"><span v-text="product.price"></span>元</div>
                    <div class="jianjie mr40 ml20 mt10">
                        库存：<span v-text="product.stock"></span>件
                    </div>
                    <div class="jianjie mr40 ml20 mt10">
                        上架时间：<span v-text="product.createTime"></span>
                    </div>
                    <div style="margin-top:10px; margin-left:20px;">
                        <el-input-number v-model="quantity" :min="1" :controls="true" :precision="0"></el-input-number>
                    </div>
                    <div class="xiadan ml20 mt20">
                        <input class="jrgwc" @click="checkLogin('addCart')" type="button" name="jrgwc" value="加入购物车" />
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </form>

    </div>
</template>






<script>
    import TopMenu from "../components/top-menu.vue";
    export default {
        name: 'detail',
        components:{TopMenu},
        data() {
            return {
                categoryList: [],
                product: {},
                quantity: 1,
                photo: "",
                user: {}
            }
        },
        //mounted 页面渲染完后执行 钩子函数
        mounted:function(){
            let _this = this;
            // 获取路由中的id参数
            let query = _this.$route.query;
            _this.getProduct(query.id);
            _this.getCategoryList();
        },
        methods:{
            getCategoryList(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/category/all").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.categoryList = resp.data;
                    }
                });
            },
            getProduct(id){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/product/get", {id:id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.product = resp.data;
                        _this.photo = process.env.VUE_APP_SERVER + "/photo/view?filename=" + _this.product.photo;
                    }
                });
            },
            addCart(){
                let _this = this;
                // 检查是否登录
                if(Tool.isEmpty(_this.user.token)){
                    _this.$message.error("还未登录或会话失效，请重新登录！");
                }else{
                    let data = {
                        productId: _this.product.id,
                        userId: _this.user.id,
                        quantity: _this.quantity
                    };
                    _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/cart/add", data).then((response)=>{
                        let resp = response.data;
                        if(resp.code === 0){
                            _this.$message.success(resp.msg);
                        }
                    });
                }
            },
            checkLogin(type){
                let _this = this;
                let token = Tool.getLoginUser();
                _this.user.token = token;
                if (Tool.isEmpty(token)) {
                    _this.$message.error("还未登录或会话失效，请重新登录！");
                    _this.user.token = "";
                }else{
                    //用token去查询用户数据是否仍存在
                    _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/user/check_login", {token:token}).then((response)=>{
                        let resp = response.data;
                        if(resp.code === 0){
                            _this.user = resp.data;
                            switch(type){
                                case 'addCart':
                                    _this.addCart();
                                    break;
                            }
                        }else{
                            _this.$message.error("还未登录或会话失效，请重新登录！");
                            _this.user.token = "";
                        }
                    });
                }
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            }
        }
    }
</script>