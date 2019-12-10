(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'SEP'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'SEP'.");
    }
    root.SEP = factory(typeof SEP === 'undefined' ? {} : SEP, kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Unit = Kotlin.kotlin.Unit;
  function hello() {
    return 'Hello from JS';
  }
  function Sample() {
  }
  Sample.prototype.checkMe = function () {
    return 12;
  };
  Sample.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Sample',
    interfaces: []
  };
  function Platform() {
    Platform_instance = this;
    this.name = 'JS';
  }
  Platform.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Platform',
    interfaces: []
  };
  var Platform_instance = null;
  function Platform_getInstance() {
    if (Platform_instance === null) {
      new Platform();
    }
    return Platform_instance;
  }
  function helloWorld(salutation) {
    var tmp$;
    var message = salutation + ' from Kotlin.JS ' + hello() + ', check me value: ' + (new Sample()).checkMe();
    (tmp$ = document.getElementById('js-response')) != null ? (tmp$.textContent = message) : null;
  }
  function main$lambda(it) {
    helloWorld('Hi!');
    return Unit;
  }
  function main() {
    document.addEventListener('DOMContentLoaded', main$lambda);
  }
  var package$sample = _.sample || (_.sample = {});
  package$sample.hello = hello;
  package$sample.Sample = Sample;
  Object.defineProperty(package$sample, 'Platform', {
    get: Platform_getInstance
  });
  package$sample.helloWorld = helloWorld;
  package$sample.main = main;
  main();
  Kotlin.defineModule('SEP', _);
  return _;
}));

//# sourceMappingURL=SEP.js.map
