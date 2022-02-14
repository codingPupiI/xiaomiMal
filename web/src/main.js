import Vue from 'vue'
import router from './router'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'
import $ from 'jquery'

Vue.config.productionTip = false;
Vue.use(ElementUI);
//定义Vue全局名称
Vue.prototype.$ajax = axios;

/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
  console.log("请求：", config);
  let token = Tool.getLoginUser();
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
    let loginUser = Tool.getLoginUser();
    if (Tool.isEmpty(loginUser)) {
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
  render: h => h(App),
}).$mount('#app');


console.log("环境：", process.env.NODE_ENV);
