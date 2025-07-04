import {defineConfig} from 'vite';
import * as path from 'node:path';
import {fileURLToPath} from 'url';
import dts from 'vite-plugin-dts';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default defineConfig({
	build: {
		lib: { // library mode
			entry: path.resolve(__dirname, 'src/main/ts/index.ts'),
			formats: ['es'],
			fileName: (format) => {
				if (format === 'es') {
					return 'index.js';
				} else {
					throw new Error(`Unexpected format: ${format}`);
				}
			},
		},
		outDir: 'target/js-dist',
		sourcemap: true,
		minify: process.env.NODE_ENV === 'production',
		cssCodeSplit: true,
	},
	plugins: [
		dts(), // emit TS declaration files
	],
	css: {
		preprocessorOptions: {
			less: {
				paths: [path.resolve(__dirname, 'src/main/less')],
			},
		},
	},
	resolve: {
		alias: {
			'@less/index.less': path.resolve(__dirname, 'src/main/less/index.less'),
		},
	},
});