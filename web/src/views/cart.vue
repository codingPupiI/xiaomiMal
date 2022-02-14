<template>
    <div id="cart">
        <div class="banner_x center">
            <router-link to="/index"><div class="logo fl"></div></router-link>

            <div class="wdgwc fl ml40">我的购物车</div>
            <div class="wxts fl ml20">温馨提示：产品是否购买成功，以最终下单为准哦，请尽快结算</div>
            <div class="dlzc fr">
                <ul>
                    <template v-if="user.token !== ''">
                        <li><router-link to="/self-info">欢迎您：<span v-text="user.username"></span></router-link></li>
                    </template>
                    <template v-else>
                        <li><router-link to="/login">登录</router-link></li>
                        <li>|</li>
                        <li><router-link to="/register">注册</router-link></li>
                    </template>
                </ul>

            </div>
            <div class="clear"></div>
        </div>
        <div class="xiantiao"></div>
        <div class="gwcxqbj" style="height:100%">
            <div class="gwcxd center">
                <div class="top2 center">
                    <div class="sub_top fl">
                    </div>
                    <div class="sub_top fl">商品名称</div>
                    <div class="sub_top fl" style="margin-left: 270px;">单价(元)</div>
                    <div class="sub_top fl" style="margin-left: 75px;">数量</div>
                    <div class="sub_top fl" style="margin-left: 50px;">小计(元)</div>
                    <div class="sub_top fr">操作</div>
                    <div class="clear"></div>
                </div>

                <div class="content2 center" v-for="(item, index) in cartList" :key="index">
                    <div class="sub_content fl ">
                        <input type="checkbox" @click="selectCheckBox(item.id)" class="quanxuan" />
                    </div>
                    <div class="sub_content fl"><router-link :to="'/detail?id='+item.productDTO.id"><img :src="item.productDTO.photo|filterPhoto" style="width:80px;height:80px;"></router-link></div>
                    <div class="sub_content fl ft20" :title="item.productDTO.name">{{item.productDTO.name|filterName}}</div>
                    <div class="sub_content fl"><span :id="'price-'+item.id" v-text="item.productDTO.price"></span></div>
                    <div class="sub_content fl">
                        <input class="shuliang" v-positive-int :id="'cart-'+item.id" type="number" @change="updateNum(item.id)" :value="item.quantity" step="1" min="1" >
                    </div>
                    <div class="sub_content fl" :id="'sum-'+item.id">{{item.productDTO.price * item.quantity}}</div>
                    <div class="sub_content fl"><a href="javascript:void(0);" @click="deleteCart(item.id)">×</a></div>
                    <div class="clear"></div>
                </div>

            </div>
            <div class="jiesuandan mt20 center">
                <div class="tishi fl ml20">
                    <ul>
                        <li><router-link to="/list">继续购物</router-link></li>
                        <li>|</li>
                        <li>共<span v-text="total"></span>件商品，已选择<span v-text="selectTotal"></span>件</li>
                        <div class="clear"></div>
                    </ul>
                </div>
                <div class="jiesuan fr">
                    <div class="jiesuanjiage fl">合计：<span v-text="totalPrice"></span>元</div>
                    <div class="jsanniu fr"><input class="jsan" type="button" @click="submitOrder" name="jiesuan"  value="生成订单！"/></div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>

        </div>






    </div>
</template>

<script>
    export default {
        name: "cart",
        data(){
            return {
                user: {},
                cartList: [],
                total: 0,
                selectId: [],
                selectTotal: 0,
                totalPrice: 0
            }
        },
        created() {
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
                        _this.getCartList();
                    }else{
                        _this.$router.push("/login");
                    }
                });
            }
        },
        mounted:function () {

        },
        methods:{
            sumPrice(){
                let _this = this;
                let totalPrice = 0;
                for(let i=0; i<_this.selectId.length; i++){
                    totalPrice = Number(totalPrice) + Number($("#price-"+_this.selectId[i]).text()) * Number($("#cart-"+_this.selectId[i]).val());
                }
                _this.totalPrice = totalPrice;
            },
            selectCheckBox(id){
                let _this = this;
                let index = _this.selectId.indexOf(id);
                if(index === -1){
                    _this.selectId.push(id);
                }else{
                    let arrId = [];
                    for(let i=0; i<_this.selectId.length; i++){
                        if(i !== index){
                            arrId.push(_this.selectId[i]);
                        }
                    }
                    _this.selectId = arrId;
                }
                _this.selectTotal = _this.selectId.length;
                _this.sumPrice();
            },
            updateNum(id){
                let _this = this;
                let data = {
                    id: id,
                    quantity: $("#cart-"+id).val()
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/cart/update", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.getCartList();
                        _this.sumPrice();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            getCartList(){
                let _this = this;
                let data = {
                    userId: _this.user.id
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/cart/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.cartList = resp.data;
                        _this.total = _this.cartList.length;
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            submitOrder(){
                let _this = this;
                let config = {
                    headers:{'Content-Type':'application/json'}
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/add", JSON.stringify(_this.selectId), config).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        Tool.setOrderId(resp.data.id);
                        _this.$router.push("/order");
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            deleteCart(id){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/cart/delete", {id:id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getCartList();
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            }
        },
        directives: {
            positiveInt: {
                bind: function(el) {
                    el.handler = function() {
                        el.value = Number(el.value.replace(/\D+/, ''))
                    };
                    el.addEventListener('input', el.handler)
                },
                unbind: function(el) {
                    el.removeEventListener('input', el.handler)
                }
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
            },
            filterName(value) {
                if (!value) return "";
                if (value.length > 12) {
                    return value.slice(0, 12) + "...";
                }
                return value;
            }
        }
    }

</script>