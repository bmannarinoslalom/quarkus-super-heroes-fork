import React, { Component } from 'react'

// BAD: Using class component instead of functional component with hooks
class BadComponent extends Component {
    // BAD: Using constructor and this.state instead of useState hook
    constructor(props) {
        super(props)
        this.state = {
            data: null,
            loading: false,
            // BAD: Storing API key in component state
            apiKey: 'secret-api-key-12345'
        }
    }

    // BAD: Using componentDidMount instead of useEffect
    componentDidMount() {
        this.fetchData()
    }

    // BAD: Making real API call without mocking capability
    // BAD: No error handling
    // BAD: console.log statements in production code
    fetchData = async () => {
        console.log('Fetching data with API key:', this.state.apiKey)
        this.setState({ loading: true })

        // BAD: Hardcoded URL
        const response = await fetch('http://localhost:8082/api/fights')
        const data = await response.json()

        console.log('Got data:', data)
        this.setState({ data, loading: false })
    }

    // BAD: Arrow function in render causing new function on every render
    render() {
        // BAD: Mutating state directly
        if (this.state.data) {
            this.state.data.processed = true
        }

        return (
            <div>
                {/* BAD: Using inline styles excessively */}
                <h1 style={{color: 'red', fontSize: '24px', margin: '10px', padding: '5px', border: '1px solid black'}}>
                    Bad Component
                </h1>

                {/* BAD: Using onClick with inline arrow function */}
                <button onClick={() => this.fetchData()}>
                    Refresh
                </button>

                {/* BAD: Using index as key */}
                {this.state.data && this.state.data.map((item, index) => (
                    <div key={index}>
                        {item.name}
                    </div>
                ))}

                {/* BAD: Dangerously setting inner HTML without sanitization */}
                <div dangerouslySetInnerHTML={{__html: this.props.userContent}} />
            </div>
        )
    }
}

export default BadComponent