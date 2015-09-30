{
    appDir: "./",
    dir: "../cloudvm-dist",
    baseUrl: "./",
    removeCombined: true,
    optimize: 'uglify2',
    uglify2: {
        sequences: false,
        compress: {
           properties:true
        },
        mangle: false
    },
	paths: {
		//vendor
		'jquery': '../../javascripts/jquery-1.11.3',
		'bootstrap': '../../javascripts/bootstrap',
		'common': '../../javascripts/common',
		'angular': '../../javascripts/angular',
		'angular-animate': '../../javascripts/angular-animate',
		'angular-route': '../../javascripts/angular-route',
		'ui-bootstrap': '../../javascripts/ui-bootstrap-tpls-0.13.3',
		'ng-toaster': '../../javascripts/toaster',
		'ng-rzslider': '../../javascripts/rzslider',
		//js文件
		'app': '../../apps/cloudvm/app',
		'app.router': '../../apps/cloudvm/app.route'
    },
    shim: {
        'angular': {
          exports: 'angular'
        },
        'angular-animate': {
          deps: ['angular'],
          exports: 'angularAnimate'
        },
        'angular-route': {
          deps: ['angular'],
          exports: 'angularRoute'
        },
        'ui-bootstrap': {
          deps: ['angular'],
          exports: 'uiBootstrap'
        },
        'ng-toaster': {
          deps: ['angular', 'angular-animate'],
          exports: 'ngToaster'
        },
        'ng-rzslider': {
          deps: ['angular'],
          exports: 'ngRzslider'
        },
        'bootstrap': {
          deps: ['jquery'],
          exports: 'bootstrap'
        }
      },
    modules: [
        {
            name: "main"
        }
    ]
}