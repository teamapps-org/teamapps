import esbuild from 'esbuild';
import {lessLoader} from 'esbuild-plugin-less';
import path from "node:path";
import alias from 'esbuild-plugin-alias';
import {fileURLToPath} from 'url';
import fs from 'node:fs'

const __dirname = path.dirname(fileURLToPath(import.meta.url));

let result = await esbuild.build({
    watch: process.argv.some(a => a === '--watch'),
    entryPoints: ['target/js-dist/lib/index.js'],
    bundle: true,
    outfile: 'target/js-dist/index.js',
    format: 'esm',
    platform: "node",
    mainFields: ["browser", "module", "main"],
    sourcemap: true,
    loader: {
        ".png": "file",
        ".svg": "dataurl",
    },
    assetNames: "assets/[name]-[hash]",
    plugins: [
        alias({
            '@less/index.less': path.resolve(__dirname, `src/main/less/index.less`),
        }),
        lessLoader(),
    ],
    minify: process.env.NODE_ENV === 'production'
});
console.log("⚡ esbuild complete! ⚡")

