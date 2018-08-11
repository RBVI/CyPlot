// This code will add the collapse/expand controls to the
// side bar.  This must be included *after* the plot
// is rendered
var plots = myPlot.getElementsByClassName('plotly_editor_plot');
var plot = plots[0];
var controls = myPlot.getElementsByClassName('editor_controls');
var control = controls[0];
var hidden = false;
var hideElement = document.createElement('A');
hideElement.innerHTML = '&nbsp;<&nbsp;';
hideElement.style.position = 'absolute';
hideElement.style.top = '15px';
hideElement.style.left = '130px';
hideElement.style.backgroundColor = 'DodgerBlue';
hideElement.style.color = 'white';
hideElement.onclick = hideControls;
plot.appendChild(hideElement);

function hideControls() {
    if (hidden) { 
        control.style.display = 'flex';
        hideElement.innerHTML = '&nbsp;<&nbsp;';
        hideElement.style.left = '130px';
        hidden = false;
    } else {
        control.style.display = 'none';
        hideElement.innerHTML = '&nbsp;>&nbsp;';
        hideElement.style.left = '20px';
        hidden = true;
    }
}
