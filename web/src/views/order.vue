<template>
    <div id="order">
        <div id="Orders" class="Inside_pages  clearfix">
            <div class="Orders_style clearfix">
                <div class="address clearfix">
                    <div class="title">收货人信息</div>
                    <div class="adderss_list clearfix">
                        <div class="title_name">填写收货地址 <a href="javascript:void(0);" @click="openEditAddress">编辑地址</a></div>
                        <div class="Shipping_address">
                            <ul class="detailed">
                                <li><label style="width: 90px;">收货人姓名</label><span v-text="order.receiverName"></span></li>
                                <li><label style="width: 90px;">详细地址</label><span v-text="order.address"></span></li>
                                <li><label style="width: 90px;">手机号码</label><span v-text="order.receiverPhone"></span></li>
                                <li><label style="width: 90px;">电子邮箱</label><span v-text="order.receiverEmail"></span></li>
                            </ul>
                        </div>
                    </div>

                    <el-dialog title="编辑地址信息" :visible.sync="dialogFormVisible">
                        <el-form>
                            <el-form-item label="收货人姓名" label-width="120px">
                                <el-input v-model="order.receiverName" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item label="详细地址" label-width="120px" style="padding: 10px 0">
                                <el-input  v-model="order.address" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item label="手机号码" label-width="120px">
                                <el-input  v-model="order.receiverPhone" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item label="电子邮箱" label-width="120px" style="padding: 10px 0">
                                <el-input  v-model="order.receiverEmail" autocomplete="off"></el-input>
                            </el-form-item>
                        </el-form>
                        <div slot="footer" class="dialog-footer">
                            <el-button type="primary" @click="dialogFormVisible=false">确 定</el-button>
                        </div>
                    </el-dialog>

                    <form class="form" method="post">
                        <fieldset>
                            <!--产品列表-->
                            <div class="Product_List">
                                <table>
                                    <thead><tr class="title"><td class="name">商品名称</td><td class="price">商品单价</td><td class="Quantity">购买数量</td><td class="Money">金额</td></tr></thead>
                                    <tbody>

                                    <tr v-for="item in order.orderItemDTOList">
                                        <td class="Product_info">
                                            <img :src="item.productPhoto|filterPhoto"  width="100px" height="100px"/>
                                            <a href="javascript:void(0);" class="product_name" :title="item.productName">{{item.productName|filterName}}</a>
                                        </td>
                                        <td><i style="font-size:18px;"><span v-text="'￥'+item.productPrice"></span></i></td>
                                        <td style="font-size:18px;">{{item.quantity}}</td>
                                        <td class="Moneys"><i style="font-size:18px;"><span v-text="'￥'+item.sum"></span></i></td>
                                    </tr>

                                    </tbody>
                                </table>
                                <div class="Pay_info">
                                    <label>订单留言</label><input id="remark" v-model="order.remark" type="text" class="text_name " placeholder="请输入64字以内的订单留言" />
                                </div>
                                <!--价格-->
                                <div class="price_style">
                                    <div class="right_direction">
                                        <ul>
                                            <li class="shiji_price"><label>实&nbsp;&nbsp;付&nbsp;&nbsp;款</label><i style="font-size:18px;"><span v-text="'￥'+order.totalPrice"></span></i><span></span></li>
                                        </ul>
                                        <div class="btn"><input @click="updateOrder"  type="button" value="提交订单" class="submit_btn"/><router-link to="/cart"><input type="button" value="返回购物车"  class="return_btn"/></router-link> </div>
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>


    </div>
</template>

<script>
    import '../../public/static/order/js/jquery-1.9.1.min'
    import '../../public/static/order/js/jquery.reveal'
    import '../../public/static/order/js/jquery.SuperSlide.2.1.1'
    import '../../public/static/order/js/jquery.sumoselect.min'
    import '../../public/static/order/js/common_js'
    import '../../public/static/order/js/footer'
    $(document).ready(function () {
        window.asd = $('.SlectBox').SumoSelect({ csvDispCount: 3 });
        window.test = $('.testsel').SumoSelect({okCancelInMulti:true });
    });
    export default {
        name: 'order',
        data(){
            return {
                order: {},
                dialogFormVisible: false,
            }
        },
        created(){
            let _this = this;
            let orderId = Tool.getOrderId();
            if (Tool.isEmpty(orderId)) {
                _this.$message.error("订单数据加载失败！");
            }else{
                _this.getOrder(orderId);
            }
        },
        methods:{
            getOrder(id){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/get", {id:id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.order = resp.data;
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            openEditAddress(){
                let _this = this;
                _this.dialogFormVisible = true;
            },
            updateOrder(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + "/web/order/update", _this.order).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        _this.toPay()
                    }else{
                        _this.$message.error(resp.msg);
                    }
                });
            },
            toPay(){
                let _this = this;
                _this.$ajax.post(process.env.VUE_APP_SERVER + '/web/pay/aliPay', {id: _this.order.id})
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
            }
        },
        filters:{
            filterPhoto(img){
                return process.env.VUE_APP_SERVER + "/photo/view?filename=" + img;
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

<style lang="css" scoped src="../../public/static/order/css/css.css"></style>
<style lang="css" scoped src="../../public/static/order/css/common.css"></style>
<style lang="css" scoped src="../../public/static/order/css/Orders.css"></style>