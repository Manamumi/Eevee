'use strict'
import React from 'react'
import 'whatwg-fetch'
import {WorkButton} from '../common/form.jsx'

function genCoffeeUrlFromParts(parts) {
  if (parts[0] !== '/coffee' || parts[0] !== '/coffee/') {
    parts = ['/coffee', ...parts]
  }
  return parts.join('/')
}

class CoffeeBreadcrumb extends React.Component {
  render() {
    const additionalParts = this.props.parts.map((part, index) => {
      const parts = this.props.parts.slice(0, index + 1)
      return (
        <li className='breadcrumb-item' key={index}>
          <a href={genCoffeeUrlFromParts(parts)}>{decodeURIComponent(part)}</a>
        </li>
      )
    })

    return (
      <nav aria-label='breadcrumb'>
        <ol className='breadcrumb mb-0'>
          <li className='breadcrumb-item'>
            <a href='/coffee/'>root</a>
          </li>
          {additionalParts}
        </ol>
      </nav>
    )
  }
}

export default class CoffeeEntry extends React.Component {
  constructor(props) {
    super(props)

    const pathParts = window.location.pathname.replace(
      /^\/coffee\//, ``
    ).split(`/`).filter(part => part.length > 0)

    const route = pathParts.join('/')

    this.state = {
      entryKey: null,
      children: [],
      entryType: null,
      entryValue: null,
      pathParts: pathParts,
      route: route,
      error: null,
      info: null,
      success: null,
      saving: false,
      adding: false,
      deleting: false
    }
  }

  componentDidMount() {
    const that = this

    fetch('/coffee/api/' + this.state.route).then(function (resp) {
      return resp.json()
    }).then(function (obj, err) {
      if (err) {
        that.setState({
          error: 'Could not find any entry matching the specified path.'
        })
        return
      }

      that.setState({
        entryKey: obj.key,
        children: obj.children,
        entryType: obj.entryType,
        entryValue: obj.value
      })
    })
  }

  onSave(e) {
    e.preventDefault()

    this.setState({
      saving: true,
      success: null,
      error: null,
      info: 'We\'re updating this entry in Coffee. Please wait.'
    })

    const formData = new FormData(this.entryEditorForm);
    const that = this;

    fetch('/coffee/api/', {
      method: 'POST',
      body: formData,
      credentials: 'include'
    }).then((resp) => {
      if (!resp.ok) {
        that.setState({
          error: 'Failed to update entry in Coffee. Please try again later.',
          info: null
        })
        return
      }
      that.setState({
        success: 'Successfully updated entry in Coffee.',
        info: null
      })
    }).finally(function () {
      that.setState({
        saving: false
      })
    })
  }

  onAdd() {
    const that = this

    bootbox.prompt('Enter a key for the child entry.', function (key) {
      if (key === null) {
        return
      }

      if (key.length === 0) {
        that.setState({
          error: 'Child key may not be empty.',
          success: null,
          info: null
        })
        return
      }

      if (key.match(/\.|\//g)) {
        that.setState({
          error: 'Child key must not be a FQN. Child keys may not contain "/" nor ".".',
          success: null,
          info: null
        })
        return
      }

      if (key.match(/\s/g)) {
        that.setState({
          error: 'Child key must not contain whitespace.',
          success: null,
          info: null
        })
        return
      }

      if (that.state.children[key]) {
        that.setState({
          error: 'This key is already in use.',
          success: null,
          info: null
        })
        return
      }

      that.setState({
        adding: true,
        error: null,
        success: null,
        info: 'Creating new child in Coffee. Please wait.'
      })

      const formData = new FormData()
      // Need to not include the root key if a route is not specified
      // because technically the root "key" is just a warning for people
      // and is not a valid key :)
      formData.append('key', that.state.route ? that.state.entryKey + '.' + key : key)
      formData.append('entryType', 'String')
      formData.append('value', '')
      formData.append('csrf_token', inside.csrfToken)

      fetch('/coffee/api/', {
        method: 'POST',
        credentials: 'include',
        body: formData
      }).then((resp) => {
        if (!resp.ok) {
          that.setState({
            adding: false,
            error: 'Unable to update entry in Coffee. Please try again later.'
          })
          return
        }

        window.location = genCoffeeUrlFromParts(that.state.pathParts.concat(key))
      })
    })
  }

  onDelete() {
    // Check if this is the root node...
    // That's a big no-no...
    if (this.state.route.length === 0) {
      this.setState({
        error: 'You may not delete the root node!'
      })
      return
    }

    const that = this

    bootbox.confirm('Are you sure you want to delete this entry? This will delete all its children as well.', function (confirm) {
      if (!confirm) {
        return
      }

      that.setState({
        deleting: true,
        info: 'We\'re deleting this entry in Coffee. Please wait.',
        error: null,
        success: null
      })

      let formData = new FormData();
      formData.append('key', that.state.entryKey);
      formData.append('csrf_token', inside.csrfToken);

      fetch('/coffee/api/', {
        method: 'DELETE',
        credentials: 'include',
        body: formData
      }).then((resp) => {
          if (!resp.ok) {
            that.setState({
              deleting: false,
              info: null,
              error: 'Unable to delete entry in Coffee. Please try again later.'
            })
            return
          }
          window.location = genCoffeeUrlFromParts(that.state.pathParts.slice(0, that.state.pathParts.length - 1));
        }
      )
    })
  }

  onDataTypeChange(e) {
    const type = e.target.value
    let newValue = null

    switch (type) {
      case 'String':
      case 'Number':
        newValue = ''
        break
      case 'Boolean':
        newValue = true
        break
      case 'StringList':
        newValue = []
    }

    this.setState({
      entryType: e.target.value,
      entryValue: newValue
    })
  }

  render() {
    let error = null;
    let info = null;
    let success = null;

    if (this.state.error) {
      error = (
        <div className='alert alert-danger'>
          <i className='fas fa-exclamation-circle mr-2'></i>
          {this.state.error}
        </div>
      )
    }

    if (this.state.info) {
      info = (
        <div className='alert alert-primary'>
          <i className='fas fa-info-circle mr-2'></i>
          {this.state.info}
        </div>
      )
    }

    if (this.state.success) {
      success = (
        <div className='alert alert-success'>
          <i className='fas fa-thumbs-up mr-2'></i>
          {this.state.success}
        </div>
      )
    }

    let saveButton = (
      <WorkButton
        type='button'
        className='btn btn-primary mr-2'
        id='saveEntry'
        working={this.state.saving}
        onClick={this.onSave.bind(this)}
        disabled={this.state.saving || this.state.adding || this.state.deleting}
      >
        Save Entry
      </WorkButton>
    );
    let addButton = (
      <WorkButton
        type='button'
        className='btn btn-secondary mr-2'
        id='addChild'
        working={this.state.adding}
        onClick={this.onAdd.bind(this)}
        disabled={this.state.saving || this.state.adding || this.state.deleting}
      >
        Add Child
      </WorkButton>
    );
    let deleteButton = (
      <WorkButton
        type='button'
        className='btn btn-danger mr-2'
        id='deleteEntry'
        working={this.state.deleting}
        onClick={this.onDelete.bind(this)}
        disabled={this.state.saving || this.state.adding || this.state.deleting}
      >
        Dangerously Delete Entry and Children
      </WorkButton>
    )

    let cardBodyBody = (
      <div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4 mb-2'></div>
        <div className='loading-placeholder pb-4'></div>
      </div>
    )

    if (this.state.entryKey) {
      const entryDataTypeOptions = [
        'String',
        'Number',
        'Boolean',
        'StringList'
      ].map(type => {
        return (
          <option
            value={type}
            key={type}
          >
            {type}
          </option>
        )
      })

      const valueField = (_ => {
        switch (this.state.entryType) {
          case 'String':
            return (
              <input
                type='text'
                name='value'
                defaultValue={this.state.entryValue}
                className='form-control'
              />
            )
          case 'Number':
            return (
              <input
                type='number'
                name='value'
                defaultValue={this.state.entryValue}
                className='form-control'
              />
            )
          case 'Boolean':
            return (
              <select
                name='value'
                defaultValue={this.state.entryValue}
                className='form-control'
              >
                <option value='True'>True</option>
                <option value='False'>False</option>
              </select>
            )
          case 'StringList':
            return (
              <textarea
                name='value'
                className='form-control h-100'
                defaultValue={this.state.entryValue.join('\n')}
                rows={10}
              />
            )
        }
      })()

      let entryChildren = (
        <li className='list-group-item'>This entry has no children.</li>
      )

      if (Object.getOwnPropertyNames(this.state.children).length) {
        entryChildren = Object.entries(this.state.children).map(([key, child]) => {
          return (
            <li className='list-group-item' key={key}>
              <a href={genCoffeeUrlFromParts(this.state.pathParts.concat(key))}>
                [{child.entryType}] {key}
              </a>
            </li>
          )
        })
      }

      cardBodyBody = (
        <div className='mb-3'>
          <h2 className='mb-3'><kbd id='entryKey'>{this.state.entryKey}</kbd></h2>
          <form
            ref={c => {
              this.entryEditorForm = c
            }}
            onSubmit={this.onSave.bind(this)}
            method='POST'
            action={null}
          >
            <div className={'mb-3'}>
              {saveButton}
              {addButton}
              {deleteButton}
            </div>
            <input name='csrf_token' type='hidden' value={inside.csrfToken} readOnly/>
            <input type='hidden' value={this.state.entryKey} name='key' readOnly/>
            <div className='form-group'>
              <h6>Entry Data Type</h6>
              <select
                name='entryType'
                className='form-control'
                value={this.state.entryType}
                onChange={this.onDataTypeChange.bind(this)}
              >
                {entryDataTypeOptions}
              </select>
            </div>
            <div className='form-group'>
              <h6>Entry Value</h6>
              {valueField}
            </div>
          </form>
          <h6>Children</h6>
          <div>
            <ul className='list-group' id='entryChildren'>
              {entryChildren}
            </ul>
          </div>
        </div>
      )
    }

    let cardBody = (
      <div className='card-body'>
        {error}
        {info}
        {success}
        {cardBodyBody}
      </div>
    )

    return (
      <div className='card shadow-sm mb-3'>
        <CoffeeBreadcrumb parts={this.state.pathParts}/>
        {cardBody}
      </div>
    )
  }
}