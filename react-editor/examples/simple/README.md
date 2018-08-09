# PlotlyEditor for use with CyPlot

This project was derived from the excellent PlotlyEditor code simple example, and modified to suit the needs 
of CyPlot, which has to run entirely within a browser.  There are a couple of notable changes:

1. We don't use the react-scripts.  We needed significant control over the bundling so everything is
driven by webpack
2. We use a modified plotly to work better with CyBrowser

## Quick start

```
npm install
./node_modules/.bin/webpack --mode=production
```

### Some useful URLs
React, webpack, and Babel tutorial: https://www.valentinog.com/blog/react-webpack-babel/

