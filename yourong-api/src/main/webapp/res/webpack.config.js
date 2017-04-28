var webpack = require('webpack'),
    path = require('path'),
    version = process.argv[2] || ''
module.exports = {
    entry:{
        popularityPark:'./source/js/modules/popularityPark.js'
        //,app:'./app.js'
    },
    output: {
        path: `./build/entry${version}`,
        publicPath: `./build/entry${version}`,
        filename: "[name].js",
        chunkFilename: "[name].js"
    },
    resolve: {
        alias: {
            assets: path.join(__dirname, '/assets'),
            css: path.join(__dirname, '/assets/css'),
            js: path.join(__dirname, '/assets/js'),
            components: path.join(__dirname, '/components')
        }
    },
    module: {
        // avoid webpack trying to shim process
        noParse: /es6-promise\.js$/,
        loaders: [
            {
                test: /\.vue$/,
                loader: 'vue'
            },
            {
                test: /\.js$/,
                // excluding some local linked packages.
                // for normal use cases only node_modules is needed.
                exclude: /node_modules|vue\/dist|vue-router\/|vue-loader\/|vue-hot-reload-api\//,
                loader: 'babel'
            }
        ]
    },
    babel: {
        presets: ['es2015'],
        plugins: ['transform-runtime']
    }
}

//process.env.NODE_ENV = os.platform()==='linux'?'production':'development'
process.env.NODE_ENV = 'production'
if (process.env.NODE_ENV === 'production') {
    module.exports.plugins = [
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: '"production"'
            }
        }),
        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            }
        }),
        new webpack.optimize.OccurenceOrderPlugin()
    ]
} else {
    module.exports.devtool = '#source-map'
    module.exports.plugins = [
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': '"development"'
        })
    ]
}