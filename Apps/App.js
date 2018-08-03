'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _plotly = require('plotly.js/dist/plotly');

var _plotly2 = _interopRequireDefault(_plotly);

var _reactChartEditor = require('react-chart-editor');

var _reactChartEditor2 = _interopRequireDefault(_reactChartEditor);

require('react-chart-editor/lib/react-chart-editor.css');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var dataSources = {
  cy1: [1, 2, 3], // eslint-disable-line no-magic-numbers
  cy2: [4, 3, 2], // eslint-disable-line no-magic-numbers
  cy3: [17, 13, 9] // eslint-disable-line no-magic-numbers
};

var dataSourceOptions = Object.keys(dataSources).map(function (name) {
  return {
    value: name,
    label: name
  };
});

var config = { editable: true };

var App = function (_Component) {
  _inherits(App, _Component);

  function App() {
    _classCallCheck(this, App);

    var _this = _possibleConstructorReturn(this, (App.__proto__ || Object.getPrototypeOf(App)).call(this));

    _this.state = { data: [], layout: {}, frames: [] };
    return _this;
  }

  _createClass(App, [{
    key: 'render',
    value: function render() {
      var _this2 = this;

      return _react2.default.createElement(
        'div',
        { className: 'app' },
        _react2.default.createElement(_reactChartEditor2.default, {
          data: this.state.data,
          layout: this.state.layout,
          config: config,
          frames: this.state.frames,
          dataSources: dataSources,
          dataSourceOptions: dataSourceOptions,
          plotly: _plotly2.default,
          onUpdate: function onUpdate(data, layout, frames) {
            return _this2.setState({ data: data, layout: layout, frames: frames });
          },
          useResizeHandler: true,
          debug: true,
          advancedTraceTypeSelector: true
        })
      );
    }
  }]);

  return App;
}(_react.Component);

exports.default = App;