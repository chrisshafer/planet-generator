var gulp = require('gulp');
var plugins = require('gulp-load-plugins')({
    pattern: ["gulp-*", "del"],
    scope: ["dependencies", "devDependencies"]
});

var projectName = "planet-generator";
var workingDir = "./resources";
var scalaJSDir = "./target/scala-2.11";
var developmentDir = "./.temp/"+projectName;
var bowerFilter = plugins.filter(['**/*.{min.js,min.css,css,js}']);

gulp.task('preview', function() {
    return gulp.src(".temp/"+projectName+"/")
        .pipe(plugins.serverLivereload({
            livereload: true,
            directoryListing: false,
            basePath: "/"
        }));
});

gulp.task('watch', function() {
    gulp.watch([scalaJSDir + "/**/*.js", scalaJSDir + "/**/*.map"], ["scalajs"]);
    gulp.watch(workingDir + "/**/*.js", ["js"]);
    gulp.watch(workingDir + "/scss/**/*.scss", ["scss"]);
    gulp.watch(workingDir + "/css/**/*.css", ["css"]);
    gulp.watch(["**/*.html", "!node_modules/**", "!bower_components/**", "!dev/**", "!dist/**"], ["html"]);
    gulp.watch("**/*.{woff,ttf,woff2}", ["font"]);
    gulp.watch(workingDir + "/img/**/*.{png,jpeg,jpg,gif,svg}", ["img"]);
});

gulp.task("html", function() {
    return gulp.src(["**/*.html", "!node_modules/**", "!bower_components/**", "!dev/**", "!dist/**"])
        .pipe(gulp.dest(developmentDir));
});

gulp.task("scss", function() {
    return gulp.src(workingDir + '/scss/**/*.scss')
        .pipe(plugins.sass().on('error', plugins.sass.logError))
        .pipe(gulp.dest(developmentDir + "/resources/css"));
});

gulp.task("css", function() {
    return gulp.src(workingDir + '/css/**/*.css')
        .pipe(gulp.dest(developmentDir + "/resources/css"));
});

gulp.task("js", function() {
    return gulp.src([workingDir + '/js/**/*.js'])
        .pipe(gulp.dest(developmentDir + "/resources/js"));
});

gulp.task("scalajs", function() {
    return gulp.src([scalaJSDir + '/**/*.js', scalaJSDir + '/**/*.map'])
        .pipe(gulp.dest(developmentDir + "/resources/js"));
});

gulp.task("image", function() {
    return gulp.src([workingDir + '/img/**/*.*'])
        .pipe(gulp.dest(developmentDir + "/resources/img"));
});

gulp.task("font", function() {
    return gulp.src(['**/*.woff','**/*.ttf','**/*.woff2'])
        .pipe(plugins.rename({dirname: ''}))
        .pipe(gulp.dest(developmentDir + "/resources/fonts"));
});

gulp.task("copy", function() {
    return gulp.src(workingDir + "/lib/**/*")
        .pipe(gulp.dest(developmentDir + "/resources/lib"));
});


gulp.task('build', plugins.sequence(["copy", "css", "scss", "js", "scalajs", "html", "font", "image"]));
gulp.task('develop', plugins.sequence(["copy", "css", "scss", "js", "scalajs", "html", "font", "image"], "preview", "watch"));
gulp.task('default', ['build']);
