import Vue from 'vue'
import Router from 'vue-router'
import mainWindow from '@/components/mainWindow'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'mainPage',
      component: mainWindow
    }
  ]
})
