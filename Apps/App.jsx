import React, {Component} from 'react';
import plotly from 'plotly.js/dist/plotly';
import PlotlyEditor from 'react-chart-editor';
import 'react-chart-editor/lib/react-chart-editor.css';

/*const dataSources = {
  cy1: [1, 2, 3], // eslint-disable-line no-magic-numbers
  cy2: [4, 3, 2], // eslint-disable-line no-magic-numbers
  cy3: [17, 13, 9], // eslint-disable-line no-magic-numbers
};*/



const config = {editable: true};

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {data: [], layout: {}, frames: []};
    this.dataSources = props.dataSources;
    this.dataSourceOptions = Object.keys(dataSources).map(name => ({
      value: name,
      label: name,
    }));
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
          onUpdate={(data, layout, frames) =>
            this.setState({data, layout, frames})
          }
          useResizeHandler
          debug
          advancedTraceTypeSelector
        />
      </div>
    );
  }
}

export default App;
