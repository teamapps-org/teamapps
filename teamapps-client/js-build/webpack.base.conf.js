'use strict';
const path = require('path');
const webpack = require('webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

function resolve(dir) {
	return path.join(__dirname, '..', dir)
}

module.exports = function (stageConfig) {
	return {
		context: path.resolve(__dirname, '../'),
		entry: {
			teamapps: resolve('/ts/modules/index.ts'),
			UiRichTextEditor: resolve('/ts/modules/formfield/UiRichTextEditor.ts')
		},
		output: {
			library: "[name]",
			libraryTarget: "umd",
			umdNamedDefine: true
		},
		// Enable sourcemaps for debugging webpack's output.
		devtool: "source-map",

		resolve: {
			extensions: ['.ts', '.tsx', '.vue', '.json', '.less', '.js', '.css'],
			alias: {
				"@less": resolve('less/'),
				"./images/sort-asc.gif": resolve('node_modules/slickgrid/images/sort-asc.gif'),
				"./images/sort-desc.gif": resolve('node_modules/slickgrid/images/sort-desc.gif'),
			},
			modules: [resolve("node_modules")]
		},
		module: {
			rules: [
				{
					test: /\.tsx?$/,
					loader: require.resolve('ts-loader')
				},
				{
					test: /\.less$/,
					use: [
						MiniCssExtractPlugin.loader,
						{
							loader: "css-loader",
							options: {
								sourceMap: true
							}
						}, {
							loader: 'postcss-loader',
							options: {
								sourceMap: true,
								plugins: () => [require('autoprefixer')],
							}
						}, {
							loader: "less-loader",
							options: {
								sourceMap: true
							}
						}
					]
				},
				{
					test: /\.css$/,
					use: [
						MiniCssExtractPlugin.loader,
						{
							loader: "css-loader",
							options: {
								sourceMap: true
							}
						}, {
							loader: 'postcss-loader',
							options: {
								sourceMap: true,
								plugins: () => [require('autoprefixer')],
							}
						}
					]
				},
				{
					test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
					loader: 'url-loader',
					options: {
						limit: 10000,
						name: path.posix.join(stageConfig.assetsSubDirectory, 'img/[name].[hash:7].[ext]')
					}
				},
				{
					test: /\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/,
					loader: 'url-loader',
					options: {
						limit: 10000,
						name: path.posix.join(stageConfig.assetsSubDirectory, 'media/[name].[hash:7].[ext]')
					}
				},
				{
					test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
					loader: 'url-loader',
					options: {
						limit: 10000,
						name: path.posix.join(stageConfig.assetsSubDirectory, 'fonts/[name].[hash:7].[ext]')
					}
				},
				{
					test: /\.js$/,
					use: ["source-map-loader"],
					enforce: "pre"
				}
			]
		},
		optimization: {
			splitChunks: {
				chunks: 'all'
			},
			noEmitOnErrors: true

		},
		node: {
			// prevent webpack from injecting useless setImmediate polyfill because Vue
			// source contains it (although only uses it if it's native).
			setImmediate: false,
			// prevent webpack from injecting mocks to Node native modules
			// that does not make sense for the client
			dgram: 'empty',
			fs: 'empty',
			net: 'empty',
			tls: 'empty',
			child_process: 'empty'
		},

		plugins: [
			// new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),
			new MiniCssExtractPlugin({
				filename: "css/teamapps.[hash].css"
			}),
			// copy custom static assets
			new CopyWebpackPlugin([
				{
					from: resolve('favicon'),
					to: path.posix.join(stageConfig.assetsSubDirectory, 'favicon'),
					ignore: ['**/.*']
				},
				{
					from: resolve('resources'),
					to: path.posix.join(stageConfig.assetsSubDirectory, 'resources'),
					ignore: ['**/.*']
				},
				{
					from: resolve('node_modules/moment/locale'),
					to: path.posix.join(stageConfig.assetsSubDirectory, 'runtime-resources/moment-locales')
				},
				{
					from: resolve('node_modules/@fullcalendar/core/locales'),
					to: path.posix.join(stageConfig.assetsSubDirectory, '/runtime-resources/fullcalendar-locales')
				},
				{
					from: resolve('node_modules/mediaelement/build'),
					to: path.posix.join(stageConfig.assetsSubDirectory, '/runtime-resources/mediaelement')
				},
				{
					from: resolve('node_modules/tinymce'),
					to: path.posix.join(stageConfig.assetsSubDirectory, "/runtime-resources/tinymce")
				},
				{
					from: resolve('node_modules/tinymce-i18n/langs'),
					to: path.posix.join(stageConfig.assetsSubDirectory, "/runtime-resources/tinymce/langs")
				}
			])
		]
	}
};
