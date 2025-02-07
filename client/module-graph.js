const fs = require('fs')
const path = require('path')

const glob = require('glob')

const packages = glob.sync('projector-*')

let mermaid = `graph TD;`

packages.forEach(p => {
    const jsonPath = path.join(__dirname, p, 'package.json')
    const exists = fs.existsSync(jsonPath)
    if (!exists) {
        return
    }

    const pkg = require(`./${p}/package.json`)
    const dependencies = Object.keys({
        ...pkg.dependencies,
        ...pkg.devDependencies,
    })

    const activeUIDeps = dependencies
        .filter(v => v.includes('projector-'))

    activeUIDeps.forEach(dep => {
        mermaid += `\n    ${pkg.name}-->${dep};`
    })
})

console.log(mermaid);