(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'SEP', 'kotlin-test'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('SEP'), require('kotlin-test'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'SEP-test'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'SEP-test'.");
    }
    if (typeof SEP === 'undefined') {
      throw new Error("Error loading module 'SEP-test'. Its dependency 'SEP' was not found. Please, check whether 'SEP' is loaded prior to 'SEP-test'.");
    }
    if (typeof this['kotlin-test'] === 'undefined') {
      throw new Error("Error loading module 'SEP-test'. Its dependency 'kotlin-test' was not found. Please, check whether 'kotlin-test' is loaded prior to 'SEP-test'.");
    }
    root['SEP-test'] = factory(typeof this['SEP-test'] === 'undefined' ? {} : this['SEP-test'], kotlin, SEP, this['kotlin-test']);
  }
}(this, function (_, Kotlin, $module$SEP, $module$kotlin_test) {
  'use strict';
  var Sample = $module$SEP.sample.Sample;
  var assertTrue = $module$kotlin_test.kotlin.test.assertTrue_ifx8ge$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var test = $module$kotlin_test.kotlin.test.test;
  var suite = $module$kotlin_test.kotlin.test.suite;
  var hello = $module$SEP.sample.hello;
  var contains = Kotlin.kotlin.text.contains_li3zpu$;
  function SampleTests() {
  }
  SampleTests.prototype.testMe = function () {
    assertTrue((new Sample()).checkMe() > 0);
  };
  SampleTests.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SampleTests',
    interfaces: []
  };
  function SampleTestsJS() {
  }
  SampleTestsJS.prototype.testHello = function () {
    assertTrue(contains(hello(), 'JS'));
  };
  SampleTestsJS.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SampleTestsJS',
    interfaces: []
  };
  var package$sample = _.sample || (_.sample = {});
  package$sample.SampleTests = SampleTests;
  package$sample.SampleTestsJS = SampleTestsJS;
  suite('sample', false, function () {
    suite('SampleTests', false, function () {
      test('testMe', false, function () {
        return (new SampleTests()).testMe();
      });
    });
    suite('SampleTestsJS', false, function () {
      test('testHello', false, function () {
        return (new SampleTestsJS()).testHello();
      });
    });
  });
  Kotlin.defineModule('SEP-test', _);
  return _;
}));

//# sourceMappingURL=SEP-test.js.map
