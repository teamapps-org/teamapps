import esbuild from 'esbuild';
import {lessLoader} from 'esbuild-plugin-less';
import path from "node:path";
import alias from 'esbuild-plugin-alias';

import {fileURLToPath} from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

console.log(__dirname);

esbuild.build({
    entryPoints: ['dist/index.js'],
    bundle: true,
    outfile: 'dist/teamapps-core.js',
    format: 'esm',
    platform: "neutral",
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
            '@less/teamapps.less': path.resolve(__dirname, `less/teamapps.less`),
        }),
        lessLoader(),
    ],
})
    .then(() => console.log("⚡ Build complete! ⚡"))
    .catch(() => process.exit(1));