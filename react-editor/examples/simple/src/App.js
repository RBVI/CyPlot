import React, {Component} from 'react';
import plotly from 'plotly.js/dist/plotly';
import * as d3 from "d3";
import PlotlyEditor from 'react-chart-editor';
import CustomEditor from './CustomEditor';
import 'react-chart-editor/lib/react-chart-editor.css';

/*
const dataSources = {
  col1: [1, 2, 3], // eslint-disable-line no-magic-numbers
  col2: [4, 3, 2], // eslint-disable-line no-magic-numbers
  col3: [17, 13, 9], // eslint-disable-line no-magic-numbers
};
const dataSourceOptions = Object.keys(dataSources).map(name => ({
  value: name,
  label: name,
}));
*/

const config = {editable: true};

class App extends Component {
  constructor(props) {
    super(props);
		// alert("props: "+props.toSource());
		this.dataSources = props.dataSources;
		// alert("dataSources: "+dataSources.toSource());
		if ("dataSourceOptions" in props) {
			this.dataSourceOptions = props.dataSourceOptions;
		} else {
			this.dataSourceOptions = Object.keys(dataSources).map(name => ({value: name, label: name}));
		}

    this.state = {data: [], layout: {}, frames: []};
		if ("data" in props) {
    	this.state.data = props.data;
			// alert("data: "+data.toSource());
		}
		if ("layout" in props) {
    	this.state.layout = props.layout;
		}
		if ("frames" in props) {
    	this.state.frames = props.frames;
		}
		// alert("data: "+this.state.toSource());
  }

  render() {
    return (
      <div className="app">
        <PlotlyEditor
          data={this.state.data}
          layout={this.state.layout}
          config={config}
          frames={this.state.frames}
          dataSources={this.dataSources}
          dataSourceOptions={this.dataSourceOptions}
          plotly={plotly}
          onUpdate={(data, layout, frames) => this.setState({data, layout, frames})}
          useResizeHandler
          // debug
          advancedTraceTypeSelector
        >
        </PlotlyEditor>
      </div>
    );
  }
}

export default App;
