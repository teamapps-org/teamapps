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
    minify: process.env.NODE_ENV === 'production'
})
    .then(async (result, x, y) => {
        console.log("Compressing result files...");
        await compressFile("dist/teamapps-core.js");
        await compressFile("dist/teamapps-core.css");
        console.log("⚡ esbuild complete! ⚡")
    })
    .catch(() => process.exit(1));


function compressFile(filePath) {
    return new Promise((resolve, reject) => {
        const stream = createReadStream(filePath);
        let resultFilePath = `${filePath}.gz`;
        stream
            .pipe(createGzip())
            .pipe(createWriteStream(resultFilePath))
            .on("finish", () => {
                console.log(`Compressed ${filePath} to ${resultFilePath}`);
                resolve();
            })
            .on("error", () => {
                reject();
            });
    });
}