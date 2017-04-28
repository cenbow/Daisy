const gulp = require('gulp'),
    fs = require('fs'),
    babel = require('gulp-babel'),
    hash = require('gulp-hash'),
    uglify = require('gulp-uglify'),
    cb = require('gulp-callback'),
    path = require('path'),
    sourcemaps = require('gulp-sourcemaps'),
    assetsPaths = [
        path.join(__dirname, 'js/**/*.js'),
        path.join(__dirname, 'js/**/**/*.js')
    ],
    distPath = path.join(__dirname,'../static/dist/js')

let changedPath = ''

//编译ES6到ES5
gulp.task('es6Build',['updateHash'], () => {
    var outPath = distPath,
        srcPath=assetsPaths

    if(changedPath){
        outPath = changedPath.replace('assets','static/dist')
        outPath = outPath.replace(/([\/|\\][\w\.]{2,})([\.js]{3})/,'')
        srcPath = changedPath
    }

    return gulp.src(srcPath)
        .pipe(sourcemaps.init())
        .pipe(babel({
            presets: ['es2015']
        }))
        .pipe(uglify())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(outPath))
})

//更新hashmap文件(JSON)
gulp.task('updateHash',()=>{
    return gulp.src(assetsPaths)
        .pipe(hash({template:'<%= name %>/<%= hash %>'}))
        .pipe(hash.manifest('assets.json',true))
        .pipe(gulp.dest(distPath))
        .pipe(cb(buildPaths))
})

//监听获取当前修改的文件路径
gulp.watch(assetsPaths, function(e){
    if(e.type==='changed'||e.type==='added'){
        changedPath = e.path
    }
})

//监听执行编译
gulp.watch(assetsPaths, ['es6Build'])

//生成资源映射文件(hashmap)
function buildPaths(){
    let assetsHash = fs.readFileSync(path.join(distPath,'assets.json'),'utf-8'),
        hashList=getHashList(JSON.parse(assetsHash))

    fs.writeFileSync(
        path.join(__dirname, '../static/dist/js/', "assets.json"),
        hashList
    )
}

/**
 * 获取资源文件的hash值
 * @param {Object} paths 处理的所有资源队列
 * @returns {Object}
 * */
function getHashList(paths) {
    var hashList = ['{'],
        dot = ','

    let pathsList = Object.keys(paths),
        pathReg = /\/([\w\.]{2,})([\.js]{3})/,
        lastName = pathsList[pathsList.length-1].replace(pathReg,'')

    Object.keys(paths).forEach((item)=>{
        let name = item.replace(pathReg,''),
            hash = paths[item].split('/').reverse()[0]

        if(name === lastName){
            dot = ''
        }

        hashList.push('\n"' + name + '":"/static/dist/js/'
            + name+'.js'
            +'?'+hash + '"' + dot)
    })
    hashList.push('\n}')
    return hashList.join('')
}