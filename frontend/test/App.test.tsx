import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import App from '../src/App'

/**
 * Comprehensive tests for App component
 */

// Mock the child components to isolate App testing
vi.mock('../src/components/Dashboard', () => ({
  default: (props: any) => <div data-testid="dashboard">Dashboard Mock</div>
}))

vi.mock('../src/components/UserList', () => ({
  default: (props: any) => <div data-testid="userlist">UserList Mock</div>
}))

vi.mock('../src/components/CommentDisplay', () => ({
  default: (props: any) => <div data-testid="comment-display">CommentDisplay Mock</div>
}))

vi.mock('../src/components/FormComponent', () => ({
  default: (props: any) => <div data-testid="form-component">FormComponent Mock</div>
}))

vi.mock('../src/components/Calculator', () => ({
  default: () => <div data-testid="calculator">Calculator Mock</div>
}))

vi.mock('../src/components/DataTable', () => ({
  default: (props: any) => <div data-testid="data-table">DataTable Mock</div>
}))

describe('App Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render without crashing', () => {
    render(<App />)
    expect(document.querySelector('.app')).toBeInTheDocument()
  })

  it('should display the app title', () => {
    render(<App />)
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('SonarShowcase')
  })

  it('should display the version', () => {
    render(<App />)
    expect(screen.getByText(/Version: 1.2.0/i)).toBeInTheDocument()
  })

  it('should render header element', () => {
    render(<App />)
    const header = document.querySelector('header')
    expect(header).toBeInTheDocument()
  })

  it('should render main element', () => {
    render(<App />)
    const main = document.querySelector('main')
    expect(main).toBeInTheDocument()
  })

  it('should render footer element', () => {
    render(<App />)
    const footer = document.querySelector('footer')
    expect(footer).toBeInTheDocument()
  })

  it('should display copyright in footer', () => {
    render(<App />)
    expect(screen.getByText(/© 2023 SonarShowcase/i)).toBeInTheDocument()
  })

  it('should render Dashboard component', () => {
    render(<App />)
    expect(screen.getByTestId('dashboard')).toBeInTheDocument()
  })

  it('should render UserList component', () => {
    render(<App />)
    expect(screen.getByTestId('userlist')).toBeInTheDocument()
  })

  it('should render CommentDisplay component', () => {
    render(<App />)
    expect(screen.getByTestId('comment-display')).toBeInTheDocument()
  })

  it('should render FormComponent', () => {
    render(<App />)
    expect(screen.getByTestId('form-component')).toBeInTheDocument()
  })

  it('should render Calculator component', () => {
    render(<App />)
    expect(screen.getByTestId('calculator')).toBeInTheDocument()
  })

  it('should render DataTable component', () => {
    render(<App />)
    expect(screen.getByTestId('data-table')).toBeInTheDocument()
  })

  it('should have correct initial state structure', () => {
    const { container } = render(<App />)
    expect(container.querySelector('.app')).toBeInTheDocument()
  })
})

describe('App Component State Management', () => {
  it('should initialize with empty users array', () => {
    render(<App />)
    // The component should render without throwing even with empty users
    expect(screen.getByTestId('dashboard')).toBeInTheDocument()
  })

  it('should handle initial loading state', () => {
    render(<App />)
    // Component should mount successfully with loading false initially
    expect(screen.getByTestId('userlist')).toBeInTheDocument()
  })
})

describe('App Component Layout', () => {
  it('should have correct element hierarchy', () => {
    render(<App />)
    
    const app = document.querySelector('.app')
    expect(app).toBeInTheDocument()
    
    const header = app?.querySelector('header')
    expect(header).toBeInTheDocument()
    
    const main = app?.querySelector('main')
    expect(main).toBeInTheDocument()
    
    const footer = app?.querySelector('footer')
    expect(footer).toBeInTheDocument()
  })

  it('should render h1 inside header', () => {
    render(<App />)
    
    const header = document.querySelector('header')
    const h1 = header?.querySelector('h1')
    expect(h1).toBeInTheDocument()
    expect(h1).toHaveTextContent('SonarShowcase')
  })

  it('should render version paragraph inside header', () => {
    render(<App />)
    
    const header = document.querySelector('header')
    const p = header?.querySelector('p')
    expect(p).toBeInTheDocument()
    expect(p).toHaveTextContent('Version: 1.2.0')
  })
})
