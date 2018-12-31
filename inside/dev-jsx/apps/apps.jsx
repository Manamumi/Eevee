'use strict'
import React from 'react'
import 'whatwg-fetch'
import {WorkButton} from '../common/form.jsx'

class AppItem extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      revealedToken: false,
      deleting: false,
      error: false
    }
  }

  revealToken() {
    this.setState({
      revealedToken: true
    })
  }

  delete() {
    this.setState({
      deleting: true
    })

    const formData = new FormData()
    formData.append('token', this.props.token)
    formData.append('csrf_token', inside.csrfToken)

    fetch('/apps/api/', {
      method: 'DELETE',
      credentials: 'include',
      body: formData
    }).then((resp) => {
      if (!resp.ok) {
        this.setState({
          error: 'Oops! Something went wrong while deleting your application. Please try again later.',
          deleting: false
        })
        return
      }
      this.setState({
        deleting: false
      })
      this.props.onDelete(this.props.token)
    })
  }

  render() {
    const ownerTag = this.props.owner ?
      this.props.owner + ' (@' + this.props.ownerGitlabUsername + ')' :
      '@' + this.props.ownerGitlabUsername

    let appToken = (
      <a href='javascript:void(0)' onClick={this.revealToken.bind(this)}>
        Click to Reveal
      </a>
    )

    if (this.state.revealedToken) {
      appToken = (
        <kbd>
          {this.props.token}
        </kbd>
      )
    }

    let screen = null

    if (this.state.deleting) {
      screen = (
        <div className='apps-blocking-screen'>
        </div>
      )
    }

    let error = null;

    if (this.state.error) {
      error = (<div className='alert alert-danger'>{this.state.error}</div>)
    }

    return (
      <li className='apps-app-item list-group-item'>
        {screen}
        {error}
        <h4>
          <kbd>
            {this.props.name}
          </kbd>
          <button type='button' className='close' aria-label='Close' onClick={this.delete.bind(this)}>
            <span aria-hidden='true'>&times;</span>
          </button>
        </h4>
        <dl className='row mb-0'>
          <dt className='col-sm-1'>Owner</dt>
          <dd className='col-sm-11'>
            <a href={'<redacted>' + this.props.ownerGitlabUsername}>
              {ownerTag}
            </a>
          </dd>
          <dt className='col-sm-1'>Token</dt>
          <dd className='col-sm-11'>
            {appToken}
          </dd>
        </dl>
      </li>
    )
  }
}

export default class AppsList extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      apps: [],
      loading: true,
      error: null,
      creatingApp: false
    }
  }

  componentDidMount() {
    const that = this

    fetch('/apps/api/').then(function (resp) {
      return resp.json()
    }).then((obj, err) => {
      if (err) {
        that.setState({
          error: 'Could not load Inside applications. Please try again later.',
          loading: false
        })
      }

      that.setState({
        apps: obj.apps,
        loading: false
      })
    })
  }

  createApp() {
    const that = this;

    this.setState({
      error: null
    })

    bootbox.prompt('Enter a name for your application.', name => {
      if (name === null) {
        return
      }

      if (name.length === 0) {
        that.setState({
          error: 'Your application name must have at least one character.'
        })
        return
      }

      if (that.state.apps.filter(app => app.name === name).length !== 0) {
        that.setState({
          error: 'An application with this name already exists.'
        })
        return
      }

      that.setState({
        creatingApp: true
      })

      const formData = new FormData()
      formData.append('name', name)
      formData.append('csrf_token', inside.csrfToken)

      fetch('/apps/api/', {
        method: 'POST',
        credentials: 'include',
        body: formData
      }).then(obj => {
        return obj.json()
      }).then(app => {
        that.setState({
          apps: [...that.state.apps, app]
        })
      }).catch(() => {
        that.setState({
          error: 'Oops! Something went wrong while creating your application. Please try again later.'
        })
      }).finally(() => {
        that.setState({
          creatingApp: false
        })
      })
    })
  }

  onDelete(token) {
    this.setState({
      apps: this.state.apps.filter(app => app.token!==token)
    })
  }

  render() {
    let error = null;

    if (this.state.error) {
      error = (
        <div className='alert alert-danger'>
          <i className='fas fa-exclamation-circle mr-2'></i>
          {this.state.error}
        </div>
      )
    }

    let cardBody = (
      <div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4'></div>
      </div>
    )

    let appList = null;

    if (!this.state.error && !this.state.loading) {
      const createAppButton = (
        <WorkButton
          className='btn btn-primary'
          working={this.state.creatingApp}
          onClick={this.createApp.bind(this)}
        >
          Create New Application
        </WorkButton>
      )

      if (this.state.apps.length > 0) {
        const appItems = this.state.apps.map((app, i) => {
          return (
            <AppItem
              name={app.name}
              owner={app.owner}
              ownerGitlabUsername={app.ownerGitlabUsername}
              token={app.token}
              isActive={app.isActive}
              onDelete={this.onDelete.bind(this)}
              key={i}
            />
          )
        })

        appList = (
          <ul className='list-group list-group-flush'>
            {appItems}
          </ul>
        )
      }

      cardBody = createAppButton
    }

    return (
      <div className='card shadow-sm mb-3'>
        <div className='card-header'>
          Inside Applications
        </div>
        <div className='card-body'>
          {error}
          <div className='alert alert-warning'>
            <i className='fas fa-exclamation-triangle mr-2'></i>
            Application tokens allow services to access Inside without going through SSO. Keep them secret. Keep them safe.
          </div>
          {cardBody}
        </div>
        {appList}
      </div>
    )
  }
}