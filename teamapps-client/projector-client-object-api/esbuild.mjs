import esbuild from 'esbuild';
import {lessLoader} from 'esbuild-plugin-less';
import path from "node:path";
import alias from 'esbuild-plugin-alias';
import {createReadStream, createWriteStream} from "fs";
import {createGzip} from "zlib";


import {fileURLToPath} from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

esbuild.build({
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
        ".eot": "file",
        ".woff2": "file",
        ".woff": "file",
        ".ttf": "file",
        ".svg": "file",
    },
    assetNames: "assets/[name]-[hash]",
    plugins: [
        alias({
            '@less/teamapps.less': path.resolve(__dirname, `src/main/less/teamapps.less`),
        }),
        lessLoader(),
    ],
    minify: process.env.NODE_ENV === 'production'
})
    .then(async (result, x, y) => {
        console.log("⚡ esbuild complete! ⚡")
    })
    .catch(() => process.exit(1));
