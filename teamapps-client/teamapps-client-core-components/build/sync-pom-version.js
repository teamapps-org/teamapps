import fs from 'fs';
import parse from "xml-parser";

var args = process.argv.splice(2);
var pomPath = args.length ? args[0] : 'pom.xml';
var packagePath = args.length >= 2 ? args[1] : 'package.json';

main(pomPath, packagePath);

function main (pomPath, packagePath) {
    var pomContent = fs.readFileSync(pomPath, 'utf8');
    var pom = parse(pomContent);
    var pomVersionNode = extractPomVersionNode(pom.root);
    if (!pomVersionNode) {
        var parent = extractParentNode(pom.root);
        pomVersionNode = extractPomVersionNode(parent);
    }

    var pomVersion = pomVersionNode.content.replace(/[\.-](RELEASE|FINAL)$/i, '');

    console.log('found version ' + pomVersion + ' in pom.xml');

    var packageContent = fs.readFileSync(packagePath, 'utf8');
    var packageVersion = JSON.parse(packageContent).version;

    console.log('found version ' + packageVersion + ' in package.json');

    if (pomVersion === packageVersion) {
        return;
    }

    var newPackageContent = packageContent.replace(formatVersion(packageVersion), formatVersion(pomVersion));
    fs.writeFileSync(packagePath, newPackageContent, 'utf8');

    console.log('package.json updated to version ' + pomVersion);
}

function extractParentNode(node) {
    return node.children.filter(function (item) {
        return item.name === 'parent';
    })[0];
}

function extractPomVersionNode(parent) {
    return parent.children.filter(function (item) {
        return item.name === 'version';
    })[0];
}

function formatVersion (version) { return '"version": "' + version + '"'; }