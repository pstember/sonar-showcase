import { useState, useEffect } from 'react'
import Dashboard from './components/Dashboard'
import UserList from './components/UserList'
import CommentDisplay from './components/CommentDisplay'
import FormComponent from './components/FormComponent'
import Calculator from './components/Calculator'
import DataTable from './components/DataTable'

/**
 * Main App component with prop drilling anti-pattern
 * 
 * MNT-04: Deep prop drilling - passing props through many levels
 */
function App() {
  // MNT: any type abuse
  const [users, setUsers] = useState<any[]>([])
  const [selectedUser, setSelectedUser] = useState<any>(null)
  const [loading, setLoading] = useState<any>(false)
  const [error, setError] = useState<any>(null)
  const [config, setConfig] = useState<any>({})
  const [theme, setTheme] = useState<any>('light')
  const [notifications, setNotifications] = useState<any[]>([])
  
  // MNT: Hardcoded values
  const apiUrl = 'http://localhost:8080/api'
  const appName = 'SonarShowcase'
  const version = '1.2.0'
  
  // MNT: Magic numbers
  const maxRetries = 3
  const timeout = 5000
  const pageSize = 10
  
  // MNT: Console.log spam
  console.log('App rendering...')
  console.log('Users:', users)
  console.log('Loading:', loading)

  useEffect(() => {
    console.log('App mounted')
    // TODO: Fetch initial data
  }, [])

  // MNT: Handler with poor naming
  const handleStuff = (data: any) => {
    console.log('Handling stuff:', data)
    setSelectedUser(data)
  }

  // MNT: Another poorly named handler
  const doThing = (x: any, y: any) => {
    console.log('Doing thing with', x, y)
    return x + y
  }

  return (
    <div className="app">
      <header>
        <h1>{appName}</h1>
        <p>Version: {version}</p>
      </header>
      
      <main>
        {/* MNT-04: Prop drilling - passing many props through components */}
        <Dashboard 
          users={users}
          setUsers={setUsers}
          selectedUser={selectedUser}
          setSelectedUser={setSelectedUser}
          loading={loading}
          setLoading={setLoading}
          error={error}
          setError={setError}
          config={config}
          setConfig={setConfig}
          theme={theme}
          setTheme={setTheme}
          notifications={notifications}
          setNotifications={setNotifications}
          apiUrl={apiUrl}
          appName={appName}
          version={version}
          maxRetries={maxRetries}
          timeout={timeout}
          pageSize={pageSize}
          onHandleStuff={handleStuff}
          onDoThing={doThing}
        />
        
        <UserList 
          users={users}
          selectedUser={selectedUser}
          onSelect={handleStuff}
          loading={loading}
          theme={theme}
          apiUrl={apiUrl}
        />
        
        <CommentDisplay 
          theme={theme}
          config={config}
        />
        
        <FormComponent 
          config={config}
          theme={theme}
          apiUrl={apiUrl}
        />
        
        <Calculator />
        
        <DataTable 
          users={users}
          loading={loading}
          pageSize={pageSize}
        />
      </main>
      
      <footer>
        <p>© 2023 SonarShowcase</p>
      </footer>
    </div>
  )
}

export default App

