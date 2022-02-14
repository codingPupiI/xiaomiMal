<template>
    <div id="index">

        <top-menu :category-list="categoryList"></top-menu>

        <!-- start banner_y -->
        <div class="banner_y center">
            <div class="nav">
                <ul>
                    <li style="text-align: center;" v-for="(category, category_index) in categoryList" :key="category_index">
                        <a href="javascript:void(0);" v-text="category.name"></a>
                        <div class="pop" style="width: 600px;">
                            <div class="left fl" style="width: 590px;">

                                <div v-for="(product, product_index)  in category.productDTOList" :key="product_index">
                                    <div class="xuangou_left fl">
                                        <router-link :to="'/detail?id='+product.id">
                                            <div class="img fl"><img :src="product.photo|filterPhoto" style="width:40px;height:40px;" alt=""></div>
                                            <span class="fl" v-text="product.name"></span>
                                            <div class="clear"></div>
                                        </router-link>
                                    </div>
                                    <div class="xuangou_right fr"><router-link :to="'/detail?id='+product.id">选购</router-link></div>
                                    <div class="clear"></div>
                                </div>

                            </div>
                        </div>
                    </li>
                </ul>
            </div>

        </div>

        <div class="sub_banner center">
            <div class="sidebar fl">
                <div class="fl"><img src="/static/image/hjh_01.gif"></div>
                <div class="fl"><img src="/static/image/hjh_02.gif"></div>
                <div class="fl"><img src="/static/image/hjh_03.gif"></div>
                <div class="fl"><img src="/static/image/hjh_04.gif"></div>
                <div class="fl"><img src="/static/image/hjh_05.gif"></div>
                <div class="fl"><img src="/static/image/hjh_06.gif"></div>
                <div class="clear"></div>
            </div>
            <div class="datu fl"><img src="/static/image/hongmi4x.png" style="width:316px;height:170px;" alt=""></div>
            <div class="datu fl"><img src="/static/image/xiaomi5.jpg" style="width:316px;height:170px;" alt=""></div>
            <div class="datu fr"><img src="/static/image/pinghengche.jpg" style="width:316px;height:170px;" alt=""></div>
            <div class="clear"></div>


        </div>
        <!-- end banner -->

        <!-- start danpin -->
        <div class="danpin center">

            <div class="biaoti center">小米最新商品</div>
            <div class="main center">
                <template v-for="(item, index) in productList">
                    <div class="mingxing fl" :key="index" v-if="index < 5">
                        <div class="sub_mingxing"><router-link :to="'/detail?id='+item.id"><img :src="item.photo|filterPhoto" alt=""></router-link></div>
                        <div class="pinpai"><router-link :title="item.name" :to="'/detail?id='+item.id">{{item.name|filterName}}</router-link></div>
                        <div class="youhui" :title="item.info">{{item.info|filterInfo}}</div>
                        <div class="jiage"><span v-text="item.price"></span>元</div>
                    </div>
                </template>
                <div class="clear"></div>
            </div>
        </div>
        <footer class="mt20 center">
          <router-link to="/Customer-Care"><div>客服</div></router-link>
        </footer>
    </div>
</template>

<script>
    import TopMenu from "../components/top-menu.vue";
    export default {
        name: 'index',
        components: {TopMenu},
        data: function() {
            return {
                categoryList: [],
                productList: []
            }
        },
        //mounted 页面渲染完后执行 钩子函数
        mounted:function(){
            let _this = this;
            _this.getCategoryList();
            _this.getProductList();
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
            getProductList(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/product/list", {}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.productList = resp.data;
                    }
                });
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            },
            filterInfo(value) {
                if (!value) return "";
                if (value.length > 35) {
                    return value.slice(0, 35) + "...";
                }
                return value;
            },
            filterName(value) {
                if (!value) return "";
                if (value.length > 15) {
                    return value.slice(0, 15) + "...";
                }
                return value;
            }
        }
    }

</script>


<style lang="scss">
@import "../../public/static/css/style.css";

</style>
