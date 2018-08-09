const webpack = require('webpack');

module.exports = {
  entry: {
		// react: "react",
		index: './src/index.js',
		app: './src/App.js'
	},
  output: {
    filename: '[name].bundle.js',
		libraryTarget: 'var',
		library: ['app', 'App'],
	},
  module: {
    rules: [
      {
        test: /\.js?$/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['react', 'es2015'],
            plugins: [
              [
                'module-resolver',
                {
                  root: ['./'],
                  alias: {
                    components: './src/components',
                    lib: './src/lib',
                    styles: './src/styles',
										// react: "dummyReact.js",
                  },
                },
              ],
            ],
          },
        },
        exclude: [/node_modules/],
      },
      {
        test: /\.(css|scss)?$/,
        use: ['style-loader', 'css-loader', 'sass-loader'],
      },
    ],
  },

  plugins: [
		new webpack.IgnorePlugin(/vertx/),
		//new webpack.ProvidePlugin({
		//	React: "React", react: "React", "window.react": "React", "window.React": "React"
		//}),
	],
  devServer: {
    open: true,
    contentBase: './dev',
  },
  // devtool: 'eval-source-map',
	optimization: {
		minimize: true,
		splitChunks: {
			chunks: "all"
		}
	},
	externals: {
//		react: {
//						root:'React',
//						commonjs2: 'react',
//						commonjs: 'react',
//						amd: 'react'
//		},
//		'react-dom': {
//						root: 'ReactDOM',
//						commonjs2: 'react-dom',
//						commonjs: 'react-dom',
//						amd: 'react-dom'
//		}
	},
};
