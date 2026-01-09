import { useEffect, useState } from 'react'
import { fetchWebhooks, registerWebhook, testWebhook } from '../services/webhookService'
import type { WebhookConfiguration } from '../types/notification'

/**
 * WebhookManagement component with SSRF vulnerability
 * 
 * SEC: SSRF - no URL validation
 * MNT: 'any' type abuse
 */

// MNT: 'any' type abuse
interface WebhookManagementProps {
  userId?: any
}

function WebhookManagement({ userId }: WebhookManagementProps) {
  // MNT: 'any' types
  const [webhooks, setWebhooks] = useState<any[]>([])
  const [loading, setLoading] = useState<any>(false)
  const [newWebhook, setNewWebhook] = useState<any>({
    name: '',
    url: '',  // SEC: SSRF - no validation
    eventTypes: '',
    active: true,
  })
  const [testUrl, setTestUrl] = useState<any>('')
  const [testPayload, setTestPayload] = useState<any>('')

  // MNT: Console spam
  console.log('WebhookManagement rendering')
  console.log('Webhooks:', webhooks)

  useEffect(() => {
    loadWebhooks()
  }, [])

  // MNT: Function with 'any' return type
  const loadWebhooks = async () => {
    setLoading(true)
    try {
      const data: any = await fetchWebhooks()
      setWebhooks(data || [])
    } catch (error: any) {
      console.log('Error loading webhooks:', error)
    } finally {
      setLoading(false)
    }
  }

  // SEC: SSRF vulnerability - no URL validation
  // MNT: 'any' type abuse
  const handleRegister = async () => {
    console.log('Registering webhook:', newWebhook)
    // SEC: SSRF - no validation of newWebhook.url
    console.log('Webhook URL:', newWebhook.url)  // Could be http://localhost:8080/admin
    
    try {
      await registerWebhook(newWebhook)
      setNewWebhook({ name: '', url: '', eventTypes: '', active: true })
      loadWebhooks()
    } catch (error: any) {
      console.log('Error registering webhook:', error)
    }
  }

  // SEC: SSRF vulnerability
  // MNT: 'any' type abuse
  const handleTest = async (id: any) => {
    console.log('Testing webhook:', id, testUrl, testPayload)
    // SEC: SSRF - user-controlled URL
    try {
      const result: any = await testWebhook(id, testUrl, testPayload)
      console.log('Test result:', result)
      alert('Webhook test completed')
    } catch (error: any) {
      console.log('Error testing webhook:', error)
      alert('Webhook test failed')
    }
  }

  // MNT: Handler with 'any' type
  const handleInputChange = (field: any, value: any) => {
    setNewWebhook((prev: any) => ({
      ...prev,
      [field]: value,
    }))
  }

  return (
    <div className="card">
      <h2>Webhook Management</h2>
      
      <div className="webhook-form">
        <h3>Register New Webhook</h3>
        <input
          type="text"
          placeholder="Webhook Name"
          value={newWebhook.name}
          onChange={(e: any) => handleInputChange('name', e.target.value)}
        />
        <input
          type="text"
          placeholder="Webhook URL (SEC: SSRF - no validation)"
          value={newWebhook.url}
          onChange={(e: any) => handleInputChange('url', e.target.value)}
        />
        <input
          type="text"
          placeholder="Event Types (comma-separated)"
          value={newWebhook.eventTypes}
          onChange={(e: any) => handleInputChange('eventTypes', e.target.value)}
        />
        <label>
          <input
            type="checkbox"
            checked={newWebhook.active}
            onChange={(e: any) => handleInputChange('active', e.target.checked)}
          />
          Active
        </label>
        <button onClick={handleRegister}>Register Webhook</button>
      </div>
      
      <div className="webhook-list">
        <h3>Registered Webhooks</h3>
        {loading ? (
          <p>Loading webhooks...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>URL</th>
                <th>Event Types</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {webhooks.map((webhook: any) => (
                <tr key={webhook.id}>
                  <td>{webhook.name}</td>
                  <td>{webhook.url}</td>
                  <td>{webhook.eventTypes}</td>
                  <td>{webhook.active ? 'Active' : 'Inactive'}</td>
                  <td>
                    <div className="test-webhook">
                      <input
                        type="text"
                        placeholder="Test URL"
                        value={testUrl}
                        onChange={(e: any) => setTestUrl(e.target.value)}
                        style={{ width: '200px', marginRight: '10px' }}
                      />
                      <input
                        type="text"
                        placeholder="Test Payload"
                        value={testPayload}
                        onChange={(e: any) => setTestPayload(e.target.value)}
                        style={{ width: '200px', marginRight: '10px' }}
                      />
                      <button onClick={() => handleTest(webhook.id)}>
                        Test
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

export default WebhookManagement

