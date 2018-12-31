'use strict'
import React from 'react'
import ReactDOM from 'react-dom'
import {BrowserRouter as Router, Route} from 'react-router-dom'
import AppsList from './apps/apps.jsx'
import CoffeeEntry from './coffee/coffee.jsx'

// We should take care not to break pages.
if (document.getElementById('react-main')) {
  ReactDOM.render((
    // You should add your internal page groups here.
    <Router>
      <div>
        <Route path={'/coffee/*'} component={CoffeeEntry} />
        <Route path={'/apps/'} component={AppsList} />
      </div>
    </Router>
  ), document.getElementById('react-main'))
}
