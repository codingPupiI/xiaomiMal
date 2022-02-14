<template>
    <div id="list">
        <top-menu :category-list="categoryList"></top-menu>
        <!-- start danpin -->
        <div class="danpin center">
            <div class="biaoti center" v-if="search !== ''">搜索内容：{{search}}</div>
            <div class="biaoti center" v-else-if="category.name !== ''">{{category.name}}</div>
            <div class="biaoti center" v-else>全部商品</div>
            <div class="main center">
                <template v-for="(item, index) in productList">
                    <div class="mingxing fl mb20" v-if="index < 5" :key="index" style="border:2px solid #fff;width:230px;cursor:pointer;" onmouseout="this.style.border='2px solid #fff'" onmousemove="this.style.border='2px solid red'">
                        <div class="sub_mingxing"><router-link :to="'/detail?id='+item.id"><img :src="item.photo|filterPhoto" alt=""></router-link></div>
                        <div class="pinpai"><router-link :title="item.name" :to="'/detail?id='+item.id">{{item.name|filterName}}</router-link></div>
                        <div class="youhui" :title="item.info">{{item.info|filterInfo}}</div>
                        <div class="jiage"><span v-text="item.price"></span>元</div>
                    </div>
                </template>


                <div class="clear"></div>
            </div>
            <template v-for="i in total">

                <div class="main center mb20" v-if="i > 1">
                    <template v-for="(item, index) in productList">
                        <div class="mingxing fl mb20" :key="index" v-if="index >= 5*(i-1) && index < 5*i" style="border:2px solid #fff;width:230px;cursor:pointer;" onmouseout="this.style.border='2px solid #fff'" onmousemove="this.style.border='2px solid red'">
                            <div class="sub_mingxing"><router-link :to="'/detail?id='+item.id"><img :src="item.photo|filterPhoto" alt=""></router-link></div>
                            <div class="pinpai"><router-link :title="item.name" :to="'/detail?id='+item.id">{{item.name|filterName}}</router-link></div>
                            <div class="youhui" :title="item.info">{{item.info|filterInfo}}</div>
                            <div class="jiage"><span v-text="item.price"></span>元</div>
                        </div>
                    </template>
                    <div class="clear"></div>
                </div>

            </template>


        </div>



    </div>
</template>

<script>
    import TopMenu from "../components/top-menu.vue";
    export default {
        data(){
            return{
                categoryList: [],
                productList: [],
                total : 1,
                category: {},
                search: ""
            }
        },
        components:{TopMenu},
        created() {
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
                // 获取路由中的id参数
                let query = _this.$route.query;
                let data = {};
                if(query.categoryId){
                     _this.category.id = query.categoryId;
                     data = {categoryId: query.categoryId};
                     _this.getCategory();
                }else if(query.search){
                    _this.category.name = "";
                    _this.search = query.search;
                    data = {name: query.search};
                }else{
                    _this.category.name = "";
                }
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/product/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.productList = resp.data;
                        if(_this.productList.length % 5 !== 0){
                            _this.total = parseInt(_this.productList.length / 5) + 1;
                        }else{
                            _this.total = parseInt(_this.productList.length / 5);
                        }
                    }
                });
            },
            getCategory(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/category/get", {id:_this.category.id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.category = resp.data;
                    }else{
                        _this.category.name = "";
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