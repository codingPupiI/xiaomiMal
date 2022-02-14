import Vue from 'vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue'
import store from './store/index'
import axios from 'axios'
import $ from 'jquery'

//productionTip设置为 false 可以阻止 vue 在启动时生成生产提示
Vue.config.productionTip = false;
Vue.use(ElementUI);
//全局使用的一个标识
Vue.prototype.$ajax = axios;


/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
  console.log("请求：", config);
  let token = Tool.getLoginAdmin();
  console.log("取出的token：", token);
  if (Tool.isNotEmpty(token)) {
    config.headers.token = token;
    console.log("请求headers增加token:", token);
  }
  return config;
}, error => {});


axios.interceptors.response.use(function (response) {
  console.log("返回结果：", response);
  return response;
}, error => {});

// 路由登录拦截
router.beforeEach((to, from, next) => {
  // 要不要对meta.loginRequire属性做监控拦截
  if (to.matched.some(function (item) {
    return item.meta.loginRequire
  })) {
    let loginAdmin = Tool.getLoginAdmin();
    if (Tool.isEmpty(loginAdmin)) {
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
});

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')


console.log("环境：", process.env.NODE_ENV);