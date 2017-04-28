const gulp = require('gulp')
const babel = require('gulp-babel')
const uglify = require('gulp-uglify')
const path = require('path')

gulp.task('es6Build', () => {
    return gulp.src('assets/js/common/*.js')
        //Babel编译
        .pipe(babel({
            presets: ['es2015']
        }))
        .pipe(uglify())
        .pipe(gulp.dest('./build/js'))
})

gulp.task('es6Watcher', ()=> {
    gulp.watch('assets/js/common/*.js', ['es6Build'])
})

gulp.task('default', ['es6Build', 'es6Watcher'])