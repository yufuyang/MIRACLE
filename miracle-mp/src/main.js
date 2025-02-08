import { createSSRApp } from "vue";
import App from "./App.vue";
import { createPinia } from 'pinia'
import uviewPlus from 'uview-plus'

// 引入全局样式
import './styles/index.scss';
import './styles/mixins.scss'

let app;
let pinia;

export function createApp() {
	app = createSSRApp(App);
	pinia = createPinia();
	
	app.use(pinia);
	app.use(uviewPlus);
	
	return {
		app,
		pinia // 使用小写的 pinia
	};
}

// 导出 app 和 pinia 实例，以便在其他地方使用
export function getApp() {
	return app;
}

export function getPinia() {
	return pinia;
}
