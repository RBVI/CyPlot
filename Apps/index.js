'use strict';

const dataSources = {
    cy1: [1, 2, 3], // eslint-disable-line no-magic-numbers
    cy2: [4, 3, 2], // eslint-disable-line no-magic-numbers
    cy3: [17, 13, 9] // eslint-disable-line no-magic-numbers
  }

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactDom = require('react-dom');

var _reactDom2 = _interopRequireDefault(_reactDom);

require('./index.css');

var _App = require('./App');

var _App2 = _interopRequireDefault(_App);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

_reactDom2.default.render(_react2.default.createElement(_App2.default, {dataSources: this.dataSources}), document.getElementById('root'));