'use strict'
// Template version: 1.3.1
// see http://vuejs-templates.github.io/webpack for documentation.

const path = require('path')

module.exports = {
	dev: {
		// Paths
		assetsSubDirectory: '.',
		assetsPublicPath: '/',
		appServerUrl: process.env.appServerUrl || "http://localhost:9000",

		// Dev Server settings
		host: 'localhost', // can be overwritten by process.env.HOST
		port: 8080, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
		autoOpenBrowser: false,
		errorOverlay: true,
		notifyOnErrors: false,
		poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-
	},

	build: {
		// Paths
		assetsSubDirectory: '.',
		assetsPublicPath: '/',

		bundleAnalyzerReport: process.env.npm_config_report
	}
}
