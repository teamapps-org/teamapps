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
	// cheap-module-eval-source-map is faster for development
	devtool: 'cheap-module-source-map', // TODO change back to cheap-module-eval-source-map!!! https://github.com/webpack-contrib/mini-css-extract-plugin/issues/29
	output: {
		filename: path.posix.join(config.assetsSubDirectory, 'js/[name].js'),
		publicPath: config.assetsPublicPath
	},
	// these devServer options should be customized in /config/index.js
	devServer: {
		clientLogLevel: 'warning',
		hot: true,
		contentBase: false, // since we use CopyWebpackPlugin.
		compress: true,
		host: HOST || config.host,
		port: PORT || config.port,
		open: config.autoOpenBrowser,
		overlay: config.errorOverlay
			? {warnings: false, errors: true}
			: false,
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
			poll: config.poll,
		}
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env': {NODE_ENV: "'development'"}
		}),
		new webpack.HotModuleReplacementPlugin(),
		new webpack.NamedModulesPlugin(), // HMR shows correct file names in console on update.
		new webpack.NoEmitOnErrorsPlugin(),
		// https://github.com/ampedandwired/html-webpack-plugin
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

const createNotifierCallback = () => {
	const notifier = require('node-notifier');

	return (severity, errors) => {
		if (severity !== 'error') {
			return;
		}

		const error = errors[0];
		const filename = error.file && error.file.split('!').pop();

		notifier.notify({
			title: require('../package.json').name,
			message: severity + ': ' + error.name,
			subtitle: filename || '',
			icon: path.join(__dirname, 'logo.png'),
			timeout: 3
		})
	}
};

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
				},
				onErrors: config.notifyOnErrors
					? createNotifierCallback()
					: undefined
			}));

			resolve(devWebpackConfig)
		}
	})
});
