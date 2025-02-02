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
        ".svg": "dataurl",
    },
    assetNames: "assets/[name]-[hash]",
    minify: process.env.NODE_ENV === 'production'
})
    .then(async (result, x, y) => {
        console.log("Compressing result files...");
        await compressFile("target/js-dist/index.js");
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