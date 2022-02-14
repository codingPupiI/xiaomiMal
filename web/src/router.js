import Vue from "vue"
import Router from "vue-router"
import Index from "./views/index.vue"
import Login from "./views/login.vue"
import Register from "./views/register.vue"
import Detail from "./views/detail.vue"
import List from "./views/list.vue"
import Cart from "./views/cart.vue"
import Order from "./views/order.vue"
import SelfOrder from "./views/self-order.vue"
import SelfInfo from "./views/self-info.vue"

Vue.use(Router);


export default new Router({
    mode: "history",
    base: process.env.BASE_URL,
    routes: [{
        path: "*",
        redirect: "/index",
    }, {
        path: "/index",
        component: Index
    },{
        path: "/login",
        component: Login
    },{
        path: "/register",
        component: Register
    },{
        path: "/detail",
        component: Detail
    },{
        path: "/list",
        component: List
    },{
        path: "/cart",
        component: Cart,
        meta: {
            loginRequire: true
        }
    },{
        path: "/order",
        component: Order,
        meta: {
            loginRequire: true
        }
    },{
        path: "/self-order",
        component: SelfOrder,
        meta: {
            loginRequire: true
        }
    },{
        path: "/self-info",
        component: SelfInfo,
        meta: {
            loginRequire: true
        }
    }]
})


// 导航栏中用到了路由 防止重复点同一个菜单给出错误提示
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};
