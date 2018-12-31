'use strict'
module.exports = function (grunt) {
  const sass = require('node-sass')

  grunt.initConfig({
    clean: {
      all: {
        src: ['static/**', '**/.sass-cache', '**/*.pyc', '**/*.map']
      }
    },
    standard: {
      app: {
        options: {
          globals: [
            'inside',
            'FormData',
            'fetch'
          ],
          fix: true
        },
        src: [
          'dev-jsx/*.jsx'
        ]
      }
    },
    uglify: {
      everything: {
        files: {
          'static/js/inside.min.js': ['staging/js/jsx.js', 'dev-js/inside.jsx']
        }
      },
      dev: {
        files: {
          'static/js/inside.min.js': ['staging/js/jsx.js', 'dev-js/inside.jsx']
        },
        options: {
          mangle: false,
          compress: false
        }
      }
    },
    sass: {
      options: {
        implementation: sass,
        sourceMap: false,
        outputStyle: 'compressed'
      },
      dist: {
        files: {
          'static/css/inside.eevee.min.css': 'dev-css/inside.scss'
        }
      }
    },
    browserify: {
      watch: {
        options: {
          plugins: ['transform-react-jsx'],
          transform: [['babelify', {presets: ['stage-0', 'es2015', 'react']}]],
          watch: true,
          keepAlive: true
        },
        src: ['dev-jsx/inside.jsx'],
        dest: 'staging/js/jsx.js'
      },
      dev: {
        options: {
          plugins: ['transform-react-jsx'],
          transform: [['babelify', {presets: ['stage-0', 'es2015', 'react']}]],
        },
        src: ['dev-jsx/inside.jsx'],
        dest: 'staging/js/jsx.js'
      }
    },
    watch: {
      sass: {
        files: 'dev-css/**/*.scss',
        tasks: ['sass']
      },
      js: {
        files: ['dev-js/**/*.jsx', 'staging/js/*.js'],
        tasks: ['standard', 'uglify:dev']
      }
    }
  })

  grunt.loadNpmTasks('grunt-browserify')
  grunt.loadNpmTasks('grunt-contrib-clean')
  grunt.loadNpmTasks('grunt-standard')
  grunt.loadNpmTasks('grunt-contrib-uglify')
  grunt.loadNpmTasks('grunt-contrib-watch')
  grunt.loadNpmTasks('grunt-sass')

  grunt.registerTask('default', ['clean', 'standard', 'browserify:dev', 'uglify:everything', 'sass'])
}