<template>
    <div id="self-order">
        <top-menu :category-list="categoryList"></top-menu>


        <!-- self_info -->
        <div class="grzxbj">
            <div class="selfinfo center">
                <div class="lfnav fl">
                    <div class="ddzx">个人中心</div>
                    <div class="subddzx">
                        <ul>
                            <li><router-link to="/self-order" style="color:#ff6700;font-weight:bold;">我的订单</router-link></li>
                            <li><router-link to="/self-info" >我的信息</router-link></li>
                        </ul>
                    </div>
                </div>
                <div class="rtcont fr" style="height:auto">
                    <div class="ddzxbt">交易订单</div>


                    <div class="ddxq" v-for="(item,index) in orderList" :key="index">

                        <div class="ddbh fl">订单号：<span v-text="item.tradeNo"></span></div>
                        <div class="ztxx fr">
                            <ul>

                                <li style="width:200px;" v-if="item.state === 1">未支付(
                                    <a style="color:green;" @click="continuePay(item.id)" href="javascript:void(0);">继续支付</a>,
                                    <a style="color:red;" @click="cancelOrder(item.id)" href="javascript:void(0);">取消</a>
                                    )
                                </li>
                                <li style="width:160px;" v-else-if="item.state === 2">已支付，待发货</li>
                                <li style="width:160px;" v-else-if="item.state === 3">已发货，待收货</li>
                                <li style="width:160px;" v-else-if="item.state === 4">已收货</li>
                                <li style="width:160px;" v-else-if="item.state === 5">已取消</li>
                                <li v-text="'￥'+item.totalPrice"></li>
                                <li style="width:180px;" v-text="item.createTime"></li>
                                <li><a href="javascript:void(0);" @click="openOrderItem(item.id)">订单详情></a></li>
                                <div class="clear"></div>
                            </ul>
                        </div>
                        <div class="clear"></div>
                    </div>



                </div>
                <div class="clear"></div>
            </div>
        </div>
        <!-- self_info -->

        <el-dialog title="订单详情信息" :visible.sync="dialogFormVisible">
            <span v-text="'订单号：'+order.tradeNo"></span><br>
            <span v-text="'收货地址：'+order.address"></span><br>
            <span v-text="'收货人姓名：'+order.receiverName"></span><br>
            <span v-text="'收货人电话：'+order.receiverPhone"></span><br>
            <span v-text="'收货人电子邮箱：'+order.receiverEmail"></span><br>
            <span v-text="'订单备注：'+order.remark"></span>
            <el-table :data="orderItemList">
                <el-table-column property="productName" label="商品名称" width="250"></el-table-column>
                <el-table-column align="center" property="productPrice" label="商品价格(元)" width="100"></el-table-column>
                <el-table-column align="center" property="quantity" label="购买数量" width="100"></el-table-column>
                <el-table-column align="center" label="商品图片" width="100">
                    <template slot-scope="scope">
                        <img :src="scope.row.productPhoto|filterPhoto" style="width:90px;height:70px;" />
                    </template>
                </el-table-column>
                <el-table-column align="center" property="sum" label="小计(元)" show-overflow-tooltip></el-table-column>
            </el-table>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="dialogFormVisible=false">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    import TopMenu from "../components/top-menu.vue";
    export default {
        name: 'self-order',
        data(){
            return{
                categoryList: [],
                orderList: [],
                user: {},
                dialogFormVisible: false,
                orderItemList: [],
                order:{}
            }
        },
        components:{TopMenu},
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
                        _this.payReturn();
                        _this.getCategoryList();
                        _this.getOrderList();
                    }else{
                        _this.$router.push("/login");
                    }
                });
            }
        },
        mounted(){},
        methods:{
            cancelOrder(id){
                let _this = this;
                let data = {
                    id: id,
                    state: 5
                };
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/cancel", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.$message.success(resp.msg);
                        _this.getOrderList();
                    }
                });
            },
            payReturn(){
                let _this = this;
                if (_this.$route.query && _this.$route.query.out_trade_no){
                    // 支付回调
                    console.log("进入回调...");
                    console.log("交易号："+ _this.$route.query.out_trade_no);
                    _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/pay/success", {tradeNo:  _this.$route.query.out_trade_no}).then((response)=>{
                        let resp = response.data;
                        if(resp.code === 0){
                            _this.$message.success(resp.msg);
                            _this.$router.push("/self-order");
                        }else{
                            _this.$message.error(resp.msg);
                        }
                    });
                }
            },
            continuePay(id){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/web/pay/aliPay', {id: id})
                    .then(resp => {
                        // 添加之前先删除一下，如果单页面，页面不刷新，添加进去的内容会一直保留在页面中，二次调用form表单会出错
                        const divForm = document.getElementsByTagName('div');
                        if (divForm.length) {
                            document.body.removeChild(divForm[0]);
                        }
                        const div = document.createElement('div');
                        div.innerHTML = resp.data; // data就是接口返回的form 表单字符串
                        document.body.appendChild(div);
                        //document.forms[0].setAttribute('target', '_blank');// 新开窗口跳转
                        document.forms[0].submit();
                    })
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
            getOrderList(){
                let _this = this;
                let data = {userId: _this.user.id};
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/self-order", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.orderList = resp.data;
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            openOrderItem(id){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/order-item", {id:id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.orderItemList = resp.data.orderItemDTOList;
                        _this.order = resp.data;
                        _this.dialogFormVisible = true;
                    }else{
                        _this.$message.error(resp.msg);
                    }
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

