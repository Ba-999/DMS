import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('dms_token') || '',
    user: JSON.parse(localStorage.getItem('dms_user') || 'null'),
    userType: localStorage.getItem('dms_user_type') || ''
  },
  mutations: {
    SET_LOGIN(state, { token, user }) {
      state.token = token
      state.user = user
      state.userType = (user && user.userType) || 'admin'
      localStorage.setItem('dms_token', token)
      localStorage.setItem('dms_user', JSON.stringify(user))
      localStorage.setItem('dms_user_type', state.userType)
      sessionStorage.setItem('dms_has_logged_in', '1')
    },
    LOGOUT(state) {
      state.token = ''
      state.user = null
      state.userType = ''
      localStorage.removeItem('dms_token')
      localStorage.removeItem('dms_user')
      localStorage.removeItem('dms_user_type')
      sessionStorage.removeItem('dms_has_logged_in')
    }
  },
  actions: {
    setLogin({ commit }, payload) { commit('SET_LOGIN', payload) },
    logout({ commit }) { commit('LOGOUT') }
  }
})
