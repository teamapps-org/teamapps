'use strict';
const path = require('path');
const webpack = require('webpack');
const config = require('./config').build;
const baseWebpackConfig = require('./webpack.base.conf')(config);
const merge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

const webpackConfig = merge(baseWebpackConfig, {
	devtool: config.productionSourceMap ? config.devtool : false,
	output: {
		path: path.resolve(__dirname, '../dist'),
		filename: path.posix.join(config.assetsSubDirectory, 'js/[name].[chunkhash].js'),
		chunkFilename: path.posix.join(config.assetsSubDirectory, 'js/[id].[chunkhash].js'),
        publicPath: config.assetsPublicPath
    },
	plugins: [
		new CleanWebpackPlugin(['dist'], {
			root: path.resolve(__dirname, '..'),
			verbose: true,
			dry: false
		}),
		new UglifyJsPlugin({
			uglifyOptions: {
				compress: {
					warnings: false
				},
				ascii_only: true // https://www.tinymce.com/docs/advanced/usage-with-module-loaders/#minificationwithuglifyjs2
			},
			sourceMap: config.productionSourceMap,
			parallel: true
		}),
		new HtmlWebpackPlugin({
			filename: config.generatedIndexHtml,
			template: 'index.html',
			inject: true,
			minify: {
				removeComments: true,
				collapseWhitespace: true,
				removeAttributeQuotes: true
			},
			chunksSortMode: 'dependency', // necessary to consistently work with multiple chunks via CommonsChunkPlugin
			webSocketUrl: config.webSocketUrl
		})
	]
});

if (config.productionGzip) {
	const CompressionWebpackPlugin = require('compression-webpack-plugin');

	webpackConfig.plugins.push(
		new CompressionWebpackPlugin({
			asset: '[path].gz[query]',
			algorithm: 'gzip',
			test: new RegExp(
				'\\.(' +
				config.productionGzipExtensions.join('|') +
				')$'
			),
			threshold: 10240,
			minRatio: 0.8
		})
	)
}

if (config.bundleAnalyzerReport) {
	const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
	webpackConfig.plugins.push(new BundleAnalyzerPlugin())
}

module.exports = webpackConfig;
