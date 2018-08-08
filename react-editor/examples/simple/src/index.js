var React = require('react');
var ReactDOM = require('react-dom');
import './index.css';
import App from './App';

const dataSources = {
  col1: [1, 2, 3], // eslint-disable-line no-magic-numbers
  col2: [4, 3, 2], // eslint-disable-line no-magic-numbers
  col3: [17, 13, 9], // eslint-disable-line no-magic-numbers
};

ReactDOM.render(<App dataSources={dataSources}/>, document.getElementById('root'));
