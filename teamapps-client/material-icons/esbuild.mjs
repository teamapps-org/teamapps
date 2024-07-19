import esbuild from 'esbuild';
import path from "node:path";
import {fileURLToPath} from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

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
        ".svg": "dataurl",
    },
    assetNames: "assets/[name]-[hash]",
    minify: process.env.NODE_ENV === 'production'
})
    .then(async (result, x, y) => {
        console.log("⚡ esbuild complete! ⚡")
    })
    .catch(() => process.exit(1));

