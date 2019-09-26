'use strict';
const webpack = require('webpack');
const config = require('./config').dev;
const baseWebpackConfig = require('./webpack.base.conf')(config);
const merge = require('webpack-merge');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin');
const portfinder = require('portfinder');

const HOST = process.env.HOST;
const PORT = process.env.PORT && Number(process.env.PORT);

const devWebpackConfig = merge(baseWebpackConfig, {
	devtool: "cheap-module-source-map",
	output: {
		filename: path.posix.join(config.assetsSubDirectory, 'js/[name].js'),
		publicPath: config.assetsPublicPath
	},
	devServer: {
		clientLogLevel: "warning",
		contentBase: false, // since we use CopyWebpackPlugin.
		compress: true,
		host: HOST || config.host,
		port: PORT || config.port,
		open: false,
		overlay: {warnings: false, errors: true},
		publicPath: "/",
		proxy: [{
			context: [
				'**',
				'!/',
				'!/index.html',
				'!/test-harness.html',
				"!/css/*",
				"!/favicon/*",
				"!/fonts/*",
				"!/img/*",
				"!/js/*",
				"!/resources/*",
				"!/runtime-resources/*",
				"!/tsd/*"
			],
			target: config.appServerUrl,
		}],
		quiet: true, // necessary for FriendlyErrorsPlugin
		watchOptions: {
			poll: false
		}
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env': {NODE_ENV: "'development'"}
		}),
		new HtmlWebpackPlugin({
			filename: 'index.html',
			template: 'index.html',
			inject: true,
			appServerUrl: config.appServerUrl
		}),
		new HtmlWebpackPlugin({
			filename: 'test-harness.html',
			template: 'test-harness.html',
			inject: true,
			appServerUrl: config.appServerUrl
		})
	]
});

module.exports = new Promise((resolve, reject) => {
	portfinder.basePort = process.env.PORT || config.port;
	portfinder.getPort((err, port) => {
		if (err) {
			reject(err)
		} else {
			// publish the new Port, necessary for e2e tests
			process.env.PORT = port;
			// add port to devServer config
			devWebpackConfig.devServer.port = port;

			// Add FriendlyErrorsPlugin
			devWebpackConfig.plugins.push(new FriendlyErrorsPlugin({
				compilationSuccessInfo: {
					messages: [`Your dev server is running here: http://${devWebpackConfig.devServer.host}:${port} ( proxy for ${config.appServerUrl} )`],
				}
			}));

			resolve(devWebpackConfig)
		}
	})
});
