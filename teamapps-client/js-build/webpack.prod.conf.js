'use strict';
const path = require('path');
const webpack = require('webpack');
const config = require('./config').build;
const baseWebpackConfig = require('./webpack.base.conf')(config);
const merge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

const webpackConfig = merge(baseWebpackConfig, {
	devtool: "source-map",
	output: {
		path: path.resolve(__dirname, '../dist'),
		filename: path.posix.join(config.assetsSubDirectory, 'js/[name].[chunkhash].js'),
		chunkFilename: path.posix.join(config.assetsSubDirectory, 'js/[id].[chunkhash].js'),
		publicPath: config.assetsPublicPath
	},
	plugins: [
		new CleanWebpackPlugin({
			verbose: true,
			dry: true
		}),
		new HtmlWebpackPlugin({
			filename: path.resolve(__dirname, '../dist/index.html'),
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
	],
	optimization: {
		minimizer: [new TerserPlugin({
			sourceMap: true,
			parallel: true,
			terserOptions: {
				ascii_only: true // https://www.tinymce.com/docs/advanced/usage-with-module-loaders/#minificationwithuglifyjs2
			}
		})],
	}
});

const CompressionWebpackPlugin = require('compression-webpack-plugin');

webpackConfig.plugins.push(
	new CompressionWebpackPlugin({
		filename: '[path].gz[query]',
		algorithm: 'gzip',
		test: /\.(js|css|html|svg)$/,
		threshold: 10240,
		minRatio: 0.8,
		deleteOriginalAssets: false
	})
);

if (config.bundleAnalyzerReport) {
	const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
	webpackConfig.plugins.push(new BundleAnalyzerPlugin())
}

module.exports = webpackConfig;
