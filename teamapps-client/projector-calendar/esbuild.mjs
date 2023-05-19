import esbuild from 'esbuild';
import {lessLoader} from 'esbuild-plugin-less';
import path from "node:path";
import alias from 'esbuild-plugin-alias';
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
    plugins: [
        alias({
            '@less/index.less': path.resolve(__dirname, `src/main/less/index.less`),
        }),
        lessLoader(),
    ],
    minify: process.env.NODE_ENV === 'production'
})
    .then(async (result, x, y) => {
        console.log("Compressing result files...");
        console.log("⚡ esbuild complete! ⚡")
    })
    .catch(() => process.exit(1));

