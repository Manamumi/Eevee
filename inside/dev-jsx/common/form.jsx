import React from 'react'

export class InputWrapper extends React.Component {
  render () {
    let subTextEle = null

    if (this.props.subText) {
      subTextEle = (
        <span className='label-subtext'>{this.props.subText}</span>
      )
    }

    return (
      <div className='asgard-form-input-group'>
        <label className='asgard-form-input-label'>{this.props.label}{subTextEle}</label>
        {this.props.children}
      </div>
    )
  }
}

export class Input extends React.Component {
  render () {
    const { subText, ...props } = this.props;

    return (
      <InputWrapper label={this.props.label} subText={this.props.subText}>
        <input {...props} ref={(c) => { this.input = c }} />
      </InputWrapper>
    )
  }
}

export class RadioSelect extends Input {
  constructor (props) {
    if (!props.options) {
      throw Error('You must specify at least one option.')
    }

    if (props.onChange && typeof props.onChange !== 'function') {
      throw Error('onChange handler must be a function.')
    }

    super(props)

    this.state = {
      value: this.props.options[0].value
    }

    this.handleSelect = this.handleSelect.bind(this)
  }

  toggleSelect (value) {
    this.setState({
      value: value
    })

    this.handleSelect(value)
  }

  handleSelect (value) {
    if (this.props.onChange) {
      this.props.onChange(value)
    }
  }

  render () {
    let radioOptions = this.props.options.map((option, key) => {
      return (
        <span
          className='radio-select-option'
          key={key}
        >
          <input
            type='radio'
            name={this.props.name}
            id={`radio-select-option-${option.value}`}
            checked={option.value === this.state.value}
            value={option.value}
            readOnly
          />
          <span
            className='radio-select-option-button'
            onClick={() => { this.toggleSelect(option.value) }}
          />
          <label
            htmlFor={`radio-select-option-${option.value}`}
            onClick={() => { this.toggleSelect(option.value) }}
          >
            {option.text}
          </label>
        </span>
      )
    })

    return (
      <InputWrapper label={this.props.label} subText={this.props.subText}>
        <div className='radio-select'>
          {radioOptions}
        </div>
      </InputWrapper>
    )
  }
}

export class CheckSelect extends Input {
  constructor (props) {
    if (props.onChange && typeof props.onChange !== 'function') {
      throw Error('onChange handler must be a function.')
    }

    super(props)

    this.state = {
      value: this.props.selected || false
    }

    this.handleSelect = this.handleSelect.bind(this)
  }

  toggleSelect () {
    this.setState({
      value: !this.state.value
    })

    this.handleSelect()
  }

  handleSelect () {
    if (this.props.onChange) {
      this.props.onChange(this.state.value)
    }
  }

  render () {
    return (
      <InputWrapper label={this.props.label} subText={this.props.subText}>
        <div className='check-select'>
          <span
            className='check-select-option'
          >
            <input
              type='checkbox'
              name={this.props.name}
              id={`check-select-option-${this.props.label}`}
              checked={this.state.value}
              readOnly
            />
            <span
              className='check-select-option-button'
              onClick={this.toggleSelect.bind(this)}
            />
            <label
              htmlFor={`check-select-option-${this.props.label}`}
              onClick={this.toggleSelect.bind(this)}
            >
              {this.props.toggleText}
            </label>
          </span>
        </div>
      </InputWrapper>
    )
  }
}

/**
 * WorkButton represents a button that should be used
 * to execute some possible long-running work.
 *
 * You can control the state of the button
 * by passing along a `working` property. The `working` property should
 * be passed within the parent's render method.
 */
export class WorkButton extends React.Component {
  render () {
    let { working, ...props } = this.props
    let contents = this.props.children

    if (working) {
      return (
        <button {...props} disabled>
          <div className='bouncing-dots'>
            <div className='bounce1'></div>
            <div className='bounce2'></div>
            <div className='bounce3'></div>
          </div>
        </button>
      )
    }

    return (
      <button {...props}>
        {contents}
      </button>
    )
  }
}
